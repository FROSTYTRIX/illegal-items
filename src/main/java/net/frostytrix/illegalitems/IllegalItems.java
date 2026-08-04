package net.frostytrix.illegalitems;

import net.fabricmc.api.ModInitializer;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModEnchantments;
import net.frostytrix.illegalitems.registry.ModEntities;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.frostytrix.illegalitems.registry.ModItemGroups;
import net.frostytrix.illegalitems.registry.ModItems;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IllegalItems implements ModInitializer {
	public static final String MOD_ID = "illegal_items";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModFluids.initialize();
		ModBlocks.initialize();
		ModItems.initialize();
		ModEntities.initialize();
		ModEnchantments.initialize();
		ModItemGroups.initialize();

		LOGGER.info("Illegal Items loaded.");
	}
}
