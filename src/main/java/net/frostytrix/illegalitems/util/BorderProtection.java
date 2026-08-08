package net.frostytrix.illegalitems.util;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResult;

/**
 * Keeps non-operators from building through a border.
 *
 * <p>A sealed column cannot be built in or dug out at any height, which also covers the border
 * block itself — it sits in its own column, so it cannot be mined either.
 */
public final class BorderProtection {
	private BorderProtection() {
	}

	public static void initialize() {
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (BorderBarrier.isWorldbuilder(player)) {
				return true;
			}

			return !BorderBarrier.columnHasBorder(world, pos.getX(), pos.getZ());
		});

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (BorderBarrier.isWorldbuilder(player)
					|| !(player.getStackInHand(hand).getItem() instanceof BlockItem)) {
				return ActionResult.PASS;
			}

			// Where the block would land, not what was clicked.
			var placement = hit.getBlockPos().offset(hit.getSide());

			return BorderBarrier.columnHasBorder(world, placement.getX(), placement.getZ())
					? ActionResult.FAIL
					: ActionResult.PASS;
		});
	}
}
