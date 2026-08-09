package net.frostytrix.illegalitems.util;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Putting a different slab into a slab's empty half, which vanilla refuses.
 *
 * <p>{@code SlabBlock.canReplace} only agrees to fill the empty half when the slab being held is the
 * same block as the one already there — anything else and the click is treated as placing a fresh
 * slab in the next block along. This steps in first for exactly the mismatched case and puts down a
 * mixed slab holding both.
 *
 * <p>Which half counts as empty is worked out the same way vanilla does it: the face you clicked,
 * or for a side click, which half of it you hit.
 */
public final class SlabMerging {
	private SlabMerging() {
	}

	public static void initialize() {
		UseBlockCallback.EVENT.register(SlabMerging::onUse);
	}

	private static ActionResult onUse(PlayerEntity player, World world, Hand hand, BlockHitResult hit) {
		ItemStack stack = player.getStackInHand(hand);

		if (player.isSneaking() || !(stack.getItem() instanceof BlockItem item)
				|| !(item.getBlock() instanceof SlabBlock incoming)) {
			return ActionResult.PASS;
		}

		BlockPos pos = hit.getBlockPos();
		BlockState existing = world.getBlockState(pos);

		// Same block is vanilla's own job, and a full block has no empty half to fill.
		if (!(existing.getBlock() instanceof SlabBlock) || existing.isOf(incoming)
				|| existing.get(SlabBlock.TYPE) == SlabType.DOUBLE || !fillsEmptyHalf(existing, hit)) {
			return ActionResult.PASS;
		}

		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		boolean existingOnBottom = existing.get(SlabBlock.TYPE) == SlabType.BOTTOM;
		BlockState bottom = existingOnBottom ? half(existing.getBlock(), SlabType.BOTTOM)
				: half(incoming, SlabType.BOTTOM);
		BlockState top = existingOnBottom ? half(incoming, SlabType.TOP)
				: half(existing.getBlock(), SlabType.TOP);

		world.setBlockState(pos, ModBlocks.MIXED_SLAB.getDefaultState());

		if (world.getBlockEntity(pos) instanceof MixedSlabBlockEntity slab) {
			slab.setHalves(bottom, top);
		}

		world.playSound(null, pos, incoming.getDefaultState().getSoundGroup().getPlaceSound(),
				SoundCategory.BLOCKS, 1.0F, 0.8F);

		if (!player.isCreative()) {
			stack.decrement(1);
		}

		return ActionResult.SUCCESS;
	}

	private static BlockState half(net.minecraft.block.Block slab, SlabType type) {
		return slab.getDefaultState().with(SlabBlock.TYPE, type);
	}

	/** Vanilla's own rule: the top face of a bottom slab, or the upper half of one of its sides. */
	private static boolean fillsEmptyHalf(BlockState existing, BlockHitResult hit) {
		Direction side = hit.getSide();
		double height = hit.getPos().y - hit.getBlockPos().getY();

		if (existing.get(SlabBlock.TYPE) == SlabType.BOTTOM) {
			return side == Direction.UP || (side.getAxis().isHorizontal() && height > 0.5);
		}

		return side == Direction.DOWN || (side.getAxis().isHorizontal() && height < 0.5);
	}
}
