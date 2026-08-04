package net.frostytrix.illegalitems.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
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
		ItemGroupEvents.modifyEntriesEvent(OPERATOR)
				.register(entries -> ModItems.operatorEntries().forEach(entries::add));
	}
}
