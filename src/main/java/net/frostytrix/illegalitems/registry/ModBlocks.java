package net.frostytrix.illegalitems.registry;

import java.util.function.Function;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.block.StillFluidBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * Block registry. {@link #register} also creates the matching item unless asked not to, which
 * matters here because several bedrock-only blocks have no item form at all.
 */
public final class ModBlocks {
	private ModBlocks() {
	}

	/**
	 * Real water and lava that never spread. These are genuine fluid blocks backed by
	 * {@link ModFluids}, so you swim and drown in the water and burn in the lava — they simply do
	 * not flow outwards. Settings copied from the vanilla fluid blocks.
	 */
	public static final Block WATER = register("water",
			settings -> new StillFluidBlock(ModFluids.STILL_WATER, settings),
			AbstractBlock.Settings.copy(Blocks.WATER), true);

	public static final Block LAVA = register("lava",
			settings -> new StillFluidBlock(ModFluids.STILL_LAVA, settings),
			AbstractBlock.Settings.copy(Blocks.LAVA), true);

	public static Block register(String name, Function<AbstractBlock.Settings, Block> factory,
			AbstractBlock.Settings settings, boolean withItem) {
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, IllegalItems.id(name));
		Block block = factory.apply(settings.registryKey(blockKey));
		Registry.register(Registries.BLOCK, blockKey, block);

		if (withItem) {
			// Routed through ModItems so the block item lands in the operator tab like everything else.
			ModItems.registerBlockItem(name, block);
		}

		return block;
	}

	public static Block register(String name, AbstractBlock.Settings settings) {
		return register(name, Block::new, settings, true);
	}

	public static void initialize() {
	}
}
