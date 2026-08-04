package net.frostytrix.illegalitems.registry;

import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.util.TriState;

/**
 * Lifts the "this enchantment does not belong on this item" restriction, so the anvil will put
 * Sharpness on a helmet or Feather Falling on a sword.
 *
 * <p>This goes through Fabric API's event rather than a mixin: Fabric already redirects the vanilla
 * call for this exact purpose, and redirecting it a second time makes mixin abort the game.
 */
public final class ModEnchantments {
	private ModEnchantments() {
	}

	public static void initialize() {
		EnchantmentEvents.ALLOW_ENCHANTING.register((enchantment, target, context) -> TriState.TRUE);
	}
}
