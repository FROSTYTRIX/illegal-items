package net.frostytrix.illegalitems.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

/**
 * Does nothing at all, but swings your arm while doing it.
 *
 * <p>Returning success from {@code useOnBlock} is what makes the game play the swing animation, so
 * the item feels like it did something even though it did not — which is exactly how the real
 * spawn egg that spawns nothing behaves.
 */
public class SwingOnUseItem extends Item {
	public SwingOnUseItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		return ActionResult.SUCCESS;
	}
}
