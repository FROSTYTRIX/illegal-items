package net.frostytrix.illegalitems.client;

import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Works out which half of a mixed slab you are pointing at, so it can sound like that one.
 *
 * <p>Which half you mean is a question only the client can answer, and only about the block under
 * the crosshair — the sound group is asked for by the state alone, with no position attached. That
 * is fine in practice: the sounds this decides are the ones you make by hitting a block, and you are
 * looking at a block when you hit it.
 */
public final class MixedSlabSounds {
	private MixedSlabSounds() {
	}

	/** The half under the crosshair, or null if that is not a mixed slab. */
	public static BlockState lookedAtHalf() {
		MinecraftClient client = MinecraftClient.getInstance();

		if (client.world == null || !(client.crosshairTarget instanceof BlockHitResult hit)) {
			return null;
		}

		BlockPos pos = hit.getBlockPos();

		if (!client.world.getBlockState(pos).isOf(ModBlocks.MIXED_SLAB)
				|| !(client.world.getBlockEntity(pos) instanceof MixedSlabBlockEntity slab)) {
			return null;
		}

		return upperHalf(hit, pos) ? slab.topHalf() : slab.bottomHalf();
	}

	/** The top face is always the upper half, the bottom face always the lower, sides go by height. */
	private static boolean upperHalf(BlockHitResult hit, BlockPos pos) {
		Direction side = hit.getSide();

		if (side == Direction.UP) {
			return true;
		}

		if (side == Direction.DOWN) {
			return false;
		}

		return hit.getPos().y - pos.getY() > 0.5;
	}
}
