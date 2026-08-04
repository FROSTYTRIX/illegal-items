package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.AnvilScreenHandler;

/**
 * Turns the anvil into the illegal-enchantment machine: mutually exclusive enchantments stack
 * together, combining is no longer capped at the enchantment's normal maximum level, and items that
 * normally hold no enchantments at all can be enchanted.
 *
 * <p>Whether an enchantment suits an item is <em>not</em> handled here — Fabric API already
 * redirects that call for its own event, and a second redirect on it crashes the game. That part
 * goes through {@code EnchantmentEvents.ALLOW_ENCHANTING} in {@code ModEnchantments} instead.
 *
 * <p>Scoped to {@code updateResult} on purpose. The enchanting table, loot tables and villager
 * trades keep vanilla limits — the anvil is the only way to build an illegal stack.
 */
@Mixin(AnvilScreenHandler.class)
public class AnvilScreenHandlerMixin {

	/** Vanilla refuses to put mutually exclusive enchantments on one item, e.g. Sharpness and Smite. */
	@Redirect(method = "updateResult", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/Enchantment;canBeCombined(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
	private boolean illegalItems$ignoreExclusivity(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second) {
		return true;
	}

	/**
	 * Vanilla clamps the combined level to the enchantment's maximum. Returning {@link Integer#MAX_VALUE}
	 * removes the ceiling, so each combine keeps adding a level with nothing to stop it.
	 */
	@Redirect(method = "updateResult", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private int illegalItems$uncapLevel(Enchantment enchantment) {
		return Integer.MAX_VALUE;
	}

	/** Vanilla refuses to enchant items that hold no enchantments at all, e.g. a stick or a block. */
	@Redirect(method = "updateResult", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/EnchantmentHelper;canHaveEnchantments(Lnet/minecraft/item/ItemStack;)Z"))
	private boolean illegalItems$enchantAnything(ItemStack stack) {
		return true;
	}
}
