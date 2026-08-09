package net.frostytrix.illegalitems;

import net.fabricmc.api.ModInitializer;
import net.frostytrix.illegalitems.registry.ModAttachments;
import net.frostytrix.illegalitems.registry.ModBlockEntities;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModEnchantments;
import net.frostytrix.illegalitems.registry.ModEntities;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.frostytrix.illegalitems.registry.ModItemGroups;
import net.frostytrix.illegalitems.registry.ModItems;
import net.frostytrix.illegalitems.registry.ModLoot;
import net.frostytrix.illegalitems.util.BorderProtection;
import net.frostytrix.illegalitems.util.DyableDogs;
import net.frostytrix.illegalitems.util.PortalHarvest;
import net.frostytrix.illegalitems.util.SlabMerging;
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
		ModBlockEntities.initialize();
		ModItems.initialize();
		ModEntities.initialize();
		ModEnchantments.initialize();
		ModAttachments.initialize();
		ModItemGroups.initialize();
		ModLoot.initialize();
		DyableDogs.initialize();
		BorderProtection.initialize();
		PortalHarvest.initialize();
		SlabMerging.initialize();

		LOGGER.info("Illegal Items loaded.");
	}
}
