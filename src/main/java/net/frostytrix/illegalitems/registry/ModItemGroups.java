package net.frostytrix.illegalitems.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import java.util.List;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/**
 * Everything the mod adds goes into vanilla's operator tab — the one holding the command block and
 * the barrier — rather than a tab of its own, which is where Bedrock keeps this content too.
 *
 * <p>{@code ItemGroups.OPERATOR} is private in 1.21.5, so the key is rebuilt from its identifier.
 */
public final class ModItemGroups {
	private ModItemGroups() {
	}

	public static final RegistryKey<ItemGroup> OPERATOR =
			RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.ofVanilla("op_blocks"));

	public static void initialize() {
		ItemGroupEvents.modifyEntriesEvent(OPERATOR).register(entries -> {
			ModItems.operatorEntries().forEach(entries::add);
			UNCRAFTABLE.forEach(item -> entries.add(uncraftable(item)));
		});
	}

	/**
	 * The Uncraftable family, which are not new items at all: each is an ordinary vanilla item carrying
	 * no potion contents. Brewing always attaches contents, so nothing in the game can produce one and
	 * no creative tab lists them — {@code /give} is the only way in, because a bare give hands over the
	 * item's default stack. Registering lookalikes would have been the wrong move; these are the genuine
	 * articles, so they stack and behave like the ones you already know.
	 *
	 * <p>Vanilla names them from the absent contents, and these four are the whole set — they are
	 * exactly the items with an {@code .effect.empty} translation key. Drinking or throwing one does
	 * nothing, the arrow flies and hits like a plain one, and the magenta colouring is the fallback
	 * vanilla paints a potion with no effects to tint it.
	 */
	private static final List<Item> UNCRAFTABLE =
			List.of(Items.POTION, Items.SPLASH_POTION, Items.LINGERING_POTION, Items.TIPPED_ARROW);

	/**
	 * Empty potion contents, said out loud rather than left to the item's defaults. It is the presence
	 * of contents holding no potion that earns these their {@code .effect.empty} name — a stack with no
	 * contents component at all is just an ordinary tipped arrow or bottle.
	 */
	private static ItemStack uncraftable(Item item) {
		ItemStack stack = new ItemStack(item);
		stack.set(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
		return stack;
	}
}
