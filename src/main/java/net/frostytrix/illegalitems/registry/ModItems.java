package net.frostytrix.illegalitems.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.item.PortalBlockItem;
import net.frostytrix.illegalitems.item.RedstoneWireItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

/**
 * Item registry. Every item added by the mod goes through {@link #register}, which wires up the
 * registry key that item settings require since 1.21.2 and records the item so
 * {@link ModItemGroups} can drop it into the operator tab.
 */
public final class ModItems {
	private ModItems() {
	}

	/** Everything the mod registers, in registration order, for the operator creative tab. */
	private static final List<Item> OPERATOR_ENTRIES = new ArrayList<>();

	// The three portal blocks. They already exist in vanilla but have no item form, so these are
	// fresh items in our namespace that place the vanilla block.
	public static final Item NETHER_PORTAL = register("nether_portal",
			settings -> new PortalBlockItem(Blocks.NETHER_PORTAL, settings), new Item.Settings());
	public static final Item END_PORTAL = registerBlockItem("end_portal", Blocks.END_PORTAL);
	public static final Item END_GATEWAY = registerBlockItem("end_gateway", Blocks.END_GATEWAY);

	/** Redstone dust at every power level, 0 through 15. */
	public static final Item[] REDSTONE_WIRE = registerRedstoneWires();

	private static Item[] registerRedstoneWires() {
		Item[] wires = new Item[RedstoneWireBlock.POWER.getValues().size()];

		for (int power = 0; power < wires.length; power++) {
			int level = power;
			wires[power] = register("redstone_wire_" + power,
					settings -> new RedstoneWireItem(level, settings), new Item.Settings());
		}

		return wires;
	}

	public static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, IllegalItems.id(name));
		Item item = Registry.register(Registries.ITEM, key, factory.apply(settings.registryKey(key)));
		OPERATOR_ENTRIES.add(item);
		return item;
	}

	public static Item register(String name, Item.Settings settings) {
		return register(name, Item::new, settings);
	}

	public static Item register(String name) {
		return register(name, Item::new, new Item.Settings());
	}

	/** Registers an item that places an already-existing (vanilla) block. */
	public static Item registerBlockItem(String name, Block block) {
		return register(name, settings -> new BlockItem(block, settings), new Item.Settings());
	}

	static List<Item> operatorEntries() {
		return OPERATOR_ENTRIES;
	}

	/** Called from the mod initializer so the static fields above are loaded in a predictable order. */
	public static void initialize() {
	}
}
