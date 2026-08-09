package net.frostytrix.illegalitems.block;

import com.mojang.serialization.MapCodec;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

/**
 * A single block that is two different slabs, one on top of the other.
 *
 * <p>Vanilla will not do this: stacking slabs gives {@code type=double} only when both are the same
 * block, because one block state cannot name two blocks. The pair lives in
 * {@link MixedSlabBlockEntity} instead, and the model reads it back when the chunk is baked.
 *
 * <p>There is no item for this block and no recipe. The only way to get one is to put the second
 * slab in, which is the whole point of it.
 */
public class MixedSlabBlock extends BlockWithEntity {
	public static final MapCodec<MixedSlabBlock> CODEC = createCodec(MixedSlabBlock::new);

	public MixedSlabBlock(Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BlockWithEntity> getCodec() {
		return CODEC;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new MixedSlabBlockEntity(pos, state);
	}

	/**
	 * Gives both slabs back. A loot table cannot express this, since it names its items ahead of time
	 * and which two they are is not known until the block entity is read.
	 *
	 * <p>This has to be the loot hook rather than {@code onStateReplaced}: the chunk destroys the block
	 * entity before that one is called, so it would find nothing there and drop nothing. The loot
	 * builder is handed the block entity while it still exists.
	 */
	@Override
	protected List<ItemStack> getDroppedStacks(BlockState state, LootWorldContext.Builder builder) {
		return builder.getOptional(LootContextParameters.BLOCK_ENTITY) instanceof MixedSlabBlockEntity slab
				? List.of(new ItemStack(slab.bottomHalf().getBlock()), new ItemStack(slab.topHalf().getBlock()))
				: List.of();
	}

	/** The mean of the two halves, so a wood-and-stone slab sits between the two. */
	public static float mean(MixedSlabBlockEntity slab, ToFloatFunction<BlockState> property) {
		return (property.apply(slab.bottomHalf()) + property.apply(slab.topHalf())) / 2.0F;
	}

	/** A tiny stand-in for {@code Function<BlockState, Float>} that does not box every call. */
	@FunctionalInterface
	public interface ToFloatFunction<T> {
		float apply(T value);
	}

	/** Middle-clicking one hands over the bottom half, there being no item for the block itself. */
	@Override
	protected ItemStack getPickStack(WorldView world, BlockPos pos, BlockState state, boolean includeData) {
		return world.getBlockEntity(pos) instanceof MixedSlabBlockEntity slab
				? new ItemStack(slab.bottomHalf().getBlock())
				: ItemStack.EMPTY;
	}
}
