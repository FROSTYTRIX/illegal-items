package net.frostytrix.illegalitems.registry;

import java.util.List;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.Item;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;

/**
 * Puts the spawn eggs into structure chests, since an egg is not something you can sensibly craft.
 *
 * <p>Each one is hidden where it belongs: the Agent in an ancient city, among the other machinery
 * nobody can explain; the NPCs in village houses, which is where they would be standing anyway;
 * Steve down an abandoned mineshaft, forgotten for about as long as he lasted in Indev; and the
 * spawn egg that spawns nothing under an X on a treasure map, so digging up the loot gets you a
 * lifetime supply of no mob at all.
 */
public final class ModLoot {
	private ModLoot() {
	}

	/** One egg, one chest table, and the chance of it turning up in any given chest. */
	private record Drop(RegistryKey<LootTable> table, Item egg, float chance) {
	}

	private static final List<Drop> DROPS = List.of(
			new Drop(LootTables.ANCIENT_CITY_CHEST, ModItems.AGENT_SPAWN_EGG, 0.20F),

			new Drop(LootTables.VILLAGE_PLAINS_CHEST, ModItems.NPC_SPAWN_EGG, 0.10F),
			new Drop(LootTables.VILLAGE_DESERT_HOUSE_CHEST, ModItems.NPC_SPAWN_EGG, 0.10F),
			new Drop(LootTables.VILLAGE_SAVANNA_HOUSE_CHEST, ModItems.NPC_SPAWN_EGG, 0.10F),
			new Drop(LootTables.VILLAGE_SNOWY_HOUSE_CHEST, ModItems.NPC_SPAWN_EGG, 0.10F),
			new Drop(LootTables.VILLAGE_TAIGA_HOUSE_CHEST, ModItems.NPC_SPAWN_EGG, 0.10F),

			new Drop(LootTables.ABANDONED_MINESHAFT_CHEST, ModItems.HUMAN_SPAWN_EGG, 0.15F),

			// Half the time, the treasure is nothing whatsoever.
			new Drop(LootTables.BURIED_TREASURE_CHEST, ModItems.SPAWN_EGG, 0.50F),

			// The portals turn up where their own dimension keeps its valuables: the nether one among
			// the piglins' hoard, the two end ones in the towers at the end of everything.
			new Drop(LootTables.BASTION_TREASURE_CHEST, ModItems.NETHER_PORTAL, 0.25F),
			new Drop(LootTables.BASTION_OTHER_CHEST, ModItems.NETHER_PORTAL, 0.10F),
			new Drop(LootTables.BASTION_BRIDGE_CHEST, ModItems.NETHER_PORTAL, 0.10F),
			new Drop(LootTables.BASTION_HOGLIN_STABLE_CHEST, ModItems.NETHER_PORTAL, 0.10F),

			new Drop(LootTables.END_CITY_TREASURE_CHEST, ModItems.END_PORTAL, 0.20F),
			new Drop(LootTables.END_CITY_TREASURE_CHEST, ModItems.END_GATEWAY, 0.15F));

	public static void initialize() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Only touch the game's own tables. A data pack that has replaced one has made its own
			// decisions and should not have ours added on top.
			if (!source.isBuiltin()) {
				return;
			}

			for (Drop drop : DROPS) {
				if (drop.table().equals(key)) {
					tableBuilder.pool(LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1.0F))
							.conditionally(RandomChanceLootCondition.builder(drop.chance()))
							.with(ItemEntry.builder(drop.egg())));
				}
			}
		});
	}
}
