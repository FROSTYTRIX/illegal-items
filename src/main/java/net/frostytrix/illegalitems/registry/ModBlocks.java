package net.frostytrix.illegalitems.registry;

import java.util.function.Function;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.block.BorderBlock;
import net.frostytrix.illegalitems.block.HayRespawnAnchorBlock;
import net.frostytrix.illegalitems.block.MixedSlabBlock;
import net.frostytrix.illegalitems.block.NetherReactorCoreBlock;
import net.frostytrix.illegalitems.block.StillFluidBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.block.WoodType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

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

	/**
	 * Bedrock's "update!" block — a plain solid block whose whole point is its texture. Breakable by
	 * hand and drops itself, so it behaves like any ordinary building block. Gravel sounds, as on
	 * Bedrock.
	 */
	public static final Block INFO_UPDATE = register("info_update", AbstractBlock.Settings.create()
			.strength(1.0F)
			.sounds(BlockSoundGroup.GRAVEL));

	/** A respawn anchor made of hay: charges and glows, but sets no spawn and never explodes. */
	public static final Block HAY_RESPAWN_ANCHOR = register("hay_respawn_anchor",
			HayRespawnAnchorBlock::new,
			AbstractBlock.Settings.create()
					.strength(0.5F)
					.sounds(BlockSoundGroup.GRASS)
					.luminance(HayRespawnAnchorBlock::luminance),
			true);

	/**
	 * A block whose texture is the game's own black-and-magenta placeholder. Its model points at a
	 * texture that deliberately does not exist, so the missing one is the real article rather than a
	 * drawing of it — the same trick the broken leather armour uses. Costs one "Missing resource"
	 * warning in the log per resource reload, which is expected.
	 */
	public static final Block MISSING_TEXTURE = register("missing_texture",
			AbstractBlock.Settings.create()
					.strength(1.0F)
					.sounds(BlockSoundGroup.STONE));

	/**
	 * One block that is two different slabs. Registered without an item, because the only way to get
	 * one is to put the second slab into an existing slab — see {@code SlabMerging}.
	 */
	public static final Block MIXED_SLAB = register("mixed_slab", MixedSlabBlock::new,
			AbstractBlock.Settings.create()
					.strength(2.0F, 6.0F)
					.sounds(BlockSoundGroup.STONE)
					.dropsNothing(),
			false);

	/** Grass and cobblestone as they looked back when this was still called Cave Game. */
	public static final Block CAVE_GAME_GRASS = register("cave_game_grass",
			AbstractBlock.Settings.create()
					.strength(0.6F)
					.sounds(BlockSoundGroup.GRASS));

	public static final Block CAVE_GAME_COBBLESTONE = register("cave_game_cobblestone",
			AbstractBlock.Settings.create()
					.strength(2.0F, 6.0F)
					.requiresTool()
					.sounds(BlockSoundGroup.STONE));

	/**
	 * The Education Edition border block: an unbreakable wall that seals its whole column, and that
	 * pistons cannot shift.
	 */
	public static final Block BORDER = register("border",
			BorderBlock::new,
			AbstractBlock.Settings.create()
					.strength(-1.0F, 3600000.0F)
					.pistonBehavior(PistonBehavior.BLOCK)
					.sounds(BlockSoundGroup.STONE),
			true);

	/** The Nether Reactor Core from Pocket Edition, structure and reaction included. */
	public static final Block NETHER_REACTOR_CORE = register("nether_reactor_core",
			NetherReactorCoreBlock::new,
			AbstractBlock.Settings.create()
					.strength(3.0F, 3.0F)
					.requiresTool()
					.sounds(BlockSoundGroup.METAL),
			true);

	/**
	 * The full building palette in netherite, which vanilla never gave us — netherite stops at the
	 * plain block. Everything copies the vanilla netherite block's settings, so they keep its hardness,
	 * blast resistance and sounds, and all of them need a diamond-or-better pickaxe to drop.
	 *
	 * <p>The vanilla constructors for these are {@code protected}, so each one is instantiated as an
	 * anonymous subclass — the {@code {}} is what makes the call legal from outside the block package.
	 */
	/** Sounds and open/close behaviour for the gate. Netherite has no vanilla wood type, so this is one. */
	private static final WoodType NETHERITE_WOOD_TYPE = new WoodType("illegal_items:netherite",
			BlockSetType.IRON, BlockSoundGroup.NETHERITE, BlockSoundGroup.NETHERITE,
			SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN);

	public static final Block NETHERITE_STAIRS = register("netherite_stairs",
			settings -> new StairsBlock(Blocks.NETHERITE_BLOCK.getDefaultState(), settings) {},
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK), true);

	public static final Block NETHERITE_SLAB = register("netherite_slab",
			SlabBlock::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK), true);

	public static final Block NETHERITE_WALL = register("netherite_wall",
			WallBlock::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK), true);

	public static final Block NETHERITE_FENCE = register("netherite_fence",
			FenceBlock::new, AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK), true);

	public static final Block NETHERITE_FENCE_GATE = register("netherite_fence_gate",
			settings -> new FenceGateBlock(NETHERITE_WOOD_TYPE, settings),
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK), true);

	/**
	 * Like the iron door, this one takes no notice of being right-clicked — redstone only. Netherite
	 * is not a material you get a handle on.
	 */
	public static final Block NETHERITE_DOOR = register("netherite_door",
			settings -> new DoorBlock(BlockSetType.IRON, settings) {},
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK).nonOpaque(), true);

	public static final Block NETHERITE_TRAPDOOR = register("netherite_trapdoor",
			settings -> new TrapdoorBlock(BlockSetType.IRON, settings) {},
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK).nonOpaque(), true);

	/** Thin blocks keep vanilla's low hardness — a netherite-hard button is only an annoyance. */
	public static final Block NETHERITE_PRESSURE_PLATE = register("netherite_pressure_plate",
			settings -> new PressurePlateBlock(BlockSetType.IRON, settings) {},
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK).noCollision().strength(0.5F), true);

	public static final Block NETHERITE_BUTTON = register("netherite_button",
			settings -> new ButtonBlock(BlockSetType.IRON, 20, settings) {},
			AbstractBlock.Settings.copy(Blocks.NETHERITE_BLOCK).noCollision().strength(0.5F), true);

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
