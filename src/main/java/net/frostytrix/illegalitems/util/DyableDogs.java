package net.frostytrix.illegalitems.util;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.frostytrix.illegalitems.registry.ModAttachments;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;

/**
 * Decides when dye lands on the dog rather than its collar.
 *
 * <p>Re-applying the collar's current colour always dyes the coat. A new colour only dyes it
 * {@link #CHANGE_CHANCE_PERCENT} of the time — and on a dog whose coat is already dyed, a new colour
 * strips it back to an ordinary collared wolf instead.
 */
public final class DyableDogs {
	private DyableDogs() {
	}

	/** Chance, in percent, that changing a collar's colour also dyes the dog itself. */
	public static final int CHANGE_CHANCE_PERCENT = 5;

	public static void initialize() {
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!(entity instanceof WolfEntity wolf)) {
				return ActionResult.PASS;
			}

			ItemStack stack = player.getStackInHand(hand);

			if (!(stack.getItem() instanceof DyeItem dye) || !wolf.isTamed() || !wolf.isOwner(player)) {
				return ActionResult.PASS;
			}

			DyeColor applied = dye.getColor();

			if (applied != wolf.getCollarColor()) {
				// Vanilla recolours the collar and uses up the dye either way; all that is decided
				// here is whether the coat keeps the dye.
				if (!world.isClient()) {
					if (wolf.getAttachedOrElse(ModAttachments.DYED_BODY, false)) {
						// A new colour on an already-dyed dog strips it back to an ordinary one
						// wearing the new collar.
						wolf.removeAttached(ModAttachments.DYED_BODY);
					} else if (wolf.getRandom().nextInt(100) < CHANGE_CHANCE_PERCENT) {
						wolf.setAttached(ModAttachments.DYED_BODY, true);
					}
				}

				return ActionResult.PASS;
			}

			// Vanilla ignores dye that matches the collar and falls through to the sit toggle, so
			// this case has to be handled here or re-dyeing would just sit the dog down. The result
			// is returned on both sides so the client does not briefly predict that sit either.
			if (!world.isClient()) {
				wolf.setAttached(ModAttachments.DYED_BODY, true);
				stack.decrementUnlessCreative(1, player);
			}

			return ActionResult.SUCCESS;
		});
	}
}
