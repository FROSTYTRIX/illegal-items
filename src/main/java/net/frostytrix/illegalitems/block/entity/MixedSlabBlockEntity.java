package net.frostytrix.illegalitems.block.entity;

import net.fabricmc.fabric.api.blockview.v2.RenderDataBlockEntity;
import net.frostytrix.illegalitems.registry.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.SlabType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

/**
 * Remembers which two slabs a mixed slab is made of.
 *
 * <p>A block state can only ever name one block, so there is nowhere in a slab's own state to record
 * that its other half is something else. That second identity lives here instead.
 *
 * <p>The pair is handed to the chunk baker through {@link RenderDataBlockEntity}, which is what lets
 * a model that is otherwise given no world or position work out what to draw.
 */
public class MixedSlabBlockEntity extends BlockEntity implements RenderDataBlockEntity {
	/** The two halves, already normalised to {@code type=bottom} and {@code type=top}. */
	public record Halves(BlockState bottom, BlockState top) {
	}

	private static final String BOTTOM_KEY = "Bottom";
	private static final String TOP_KEY = "Top";

	private BlockState bottom = defaultHalf(SlabType.BOTTOM);
	private BlockState top = defaultHalf(SlabType.TOP);

	public MixedSlabBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.MIXED_SLAB, pos, state);
	}

	private static BlockState defaultHalf(SlabType type) {
		return Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, type);
	}

	public BlockState bottomHalf() {
		return bottom;
	}

	public BlockState topHalf() {
		return top;
	}

	public void setHalves(BlockState bottom, BlockState top) {
		this.bottom = bottom;
		this.top = top;
		markDirty();

		// The geometry is baked into the chunk mesh, so the chunk has to be told to build it again.
		if (world != null) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		}
	}

	@Override
	public Object getRenderData() {
		return new Halves(bottom, top);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.writeNbt(nbt, registries);
		nbt.put(BOTTOM_KEY, NbtHelper.fromBlockState(bottom));
		nbt.put(TOP_KEY, NbtHelper.fromBlockState(top));
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
		super.readNbt(nbt, registries);
		var blocks = registries.getOrThrow(RegistryKeys.BLOCK);
		bottom = nbt.getCompound(BOTTOM_KEY).map(half -> NbtHelper.toBlockState(blocks, half))
				.orElseGet(() -> defaultHalf(SlabType.BOTTOM));
		top = nbt.getCompound(TOP_KEY).map(half -> NbtHelper.toBlockState(blocks, half))
				.orElseGet(() -> defaultHalf(SlabType.TOP));

		// The chunk is meshed as soon as the block update lands, which is before this data follows it.
		// Without asking for another build the slab keeps whatever the defaults were, and every mixed
		// slab in the world renders as plain stone.
		if (world != null && world.isClient()) {
			world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
		}
	}

	// Without these two the client is never told what the halves are, and every mixed slab in the
	// world renders as whatever the defaults happen to be.
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
		return createNbt(registries);
	}
}
