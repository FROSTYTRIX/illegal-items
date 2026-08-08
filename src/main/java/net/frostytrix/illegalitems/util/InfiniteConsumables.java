package net.frostytrix.illegalitems.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Works out whether a stack is something Infinity has made inexhaustible.
 *
 * <p>Applies to anything you consume — food, potions, milk. There is no need to check what kind of
 * item it is: this is only ever consulted while something is being eaten or drunk.
 */
public final class InfiniteConsumables {
	private InfiniteConsumables() {
	}

	public static boolean isInfinite(ItemStack stack) {
		ItemEnchantmentsComponent enchantments =
				stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

		for (RegistryEntry<Enchantment> enchantment : enchantments.getEnchantments()) {
			if (enchantment.matchesKey(Enchantments.INFINITY)) {
				return true;
			}
		}

		return false;
	}
}
