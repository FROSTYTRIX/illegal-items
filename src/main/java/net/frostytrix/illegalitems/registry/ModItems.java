package net.frostytrix.illegalitems.registry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.item.BubbleColumnItem;
import net.frostytrix.illegalitems.item.PortalBlockItem;
import net.frostytrix.illegalitems.item.RedstoneWireItem;
import net.frostytrix.illegalitems.item.SwingOnUseItem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.item.equipment.EquipmentType;
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

	/**
	 * The bubble column, which has no item form in vanilla either. This places the real
	 * {@code minecraft:bubble_column} — dry, and gone again on the next tick, as on Bedrock.
	 */
	public static final Item BUBBLE_COLUMN = register("bubble_column",
			settings -> new BubbleColumnItem(Blocks.BUBBLE_COLUMN, settings), new Item.Settings());

	// Fire blocks have no item form in vanilla; these place the real thing.
	public static final Item FIRE = registerBlockItem("fire", Blocks.FIRE);
	public static final Item SOUL_FIRE = registerBlockItem("soul_fire", Blocks.SOUL_FIRE);

	/**
	 * A tipped arrow carrying no potion at all. Vanilla always gives tipped arrows potion contents, so
	 * this one shoots and behaves exactly like a plain arrow while still looking tipped.
	 *
	 * <p>Not the same thing as vanilla's Uncraftable Tipped Arrow, which the operator tab also carries:
	 * that one is a real {@code minecraft:tipped_arrow} holding contents with no potion in them, while
	 * this is its own item with no contents whatsoever. They look alike and fly alike; the mod keeps
	 * both because they are two different ways of being nothing.
	 */
	public static final Item TIPPED_ARROW = register("tipped_arrow", ArrowItem::new, new Item.Settings());

	/**
	 * Broken leather armour: the item itself is invisible, and worn it shows the black-and-magenta
	 * missing texture.
	 *
	 * <p>Since 1.21.4 what a worn piece looks like is data, not code — the material names an
	 * equipment asset and the client draws whatever layers that asset lists. This copies vanilla
	 * leather's stats exactly and points at an asset whose layer texture deliberately does not
	 * exist, so the game falls back to its own missing texture and the armour protects normally
	 * while looking thoroughly broken.
	 *
	 * <p>Shipping a hand-made chequer instead does not look right: the real fallback stretches one
	 * 16x16 texture across the whole armour UV, giving big quadrants rather than small squares. The
	 * only cost is two "Missing resource" warnings per resource reload, which are expected here.
	 */
	private static final RegistryKey<EquipmentAsset> MISSING_TEXTURE_ASSET =
			RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, IllegalItems.id("missing_texture"));

	private static final ArmorMaterial INVISIBLE_LEATHER = new ArmorMaterial(
			ArmorMaterials.LEATHER.durability(),
			ArmorMaterials.LEATHER.defense(),
			ArmorMaterials.LEATHER.enchantmentValue(),
			ArmorMaterials.LEATHER.equipSound(),
			ArmorMaterials.LEATHER.toughness(),
			ArmorMaterials.LEATHER.knockbackResistance(),
			ArmorMaterials.LEATHER.repairIngredient(),
			MISSING_TEXTURE_ASSET);

	public static final Item LEATHER_HELMET = registerArmor("leather_helmet", EquipmentType.HELMET);
	public static final Item LEATHER_CHESTPLATE = registerArmor("leather_chestplate", EquipmentType.CHESTPLATE);
	public static final Item LEATHER_LEGGINGS = registerArmor("leather_leggings", EquipmentType.LEGGINGS);
	public static final Item LEATHER_BOOTS = registerArmor("leather_boots", EquipmentType.BOOTS);

	private static Item registerArmor(String name, EquipmentType type) {
		return register(name, Item::new, new Item.Settings().armor(INVISIBLE_LEATHER, type));
	}

	/**
	 * A map you cannot see while holding it, though your hands stay right where they should be.
	 *
	 * <p>The two-handed map pose is not tied to the filled map item — {@code HeldItemRenderer} keys
	 * it off the {@code map_id} component, so carrying that component is enough to get vanilla's
	 * own "hold it up in both hands" rendering, arms included. Suppressing just the map itself is
	 * done client side in {@code HeldItemRendererMixin}, which leaves the arms untouched.
	 */
	public static final Item MAP = register("map", Item::new,
			new Item.Settings().component(DataComponentTypes.MAP_ID, new MapIdComponent(0)));

	/**
	 * The spawn egg that spawns nothing and is called "item.spawn_egg.name".
	 *
	 * <p>Its name comes from the lang file, which maps this item's key to that literal string. There
	 * is no way to make the game genuinely fail to translate it: {@code getTranslationKey} and
	 * {@code getName} are final on {@code Item}, and while {@code Item.getName(stack)} does read the
	 * {@code item_name} component, {@code Item.Settings} overwrites that with the key-derived default
	 * when the item is built. The lang entry gets the same result and is a good deal less fragile.
	 *
	 * <p>Using it does nothing, but it swings your arm so it at least feels like it tried.
	 */
	public static final Item SPAWN_EGG = register("spawn_egg", SwingOnUseItem::new, new Item.Settings());

	/** Spawn egg for the Education Edition agent. */
	public static final Item AGENT_SPAWN_EGG = register("agent_spawn_egg",
			settings -> new SpawnEggItem(ModEntities.AGENT, settings), new Item.Settings());

	/** Spawn egg for the Education Edition NPC. */
	public static final Item NPC_SPAWN_EGG = register("npc_spawn_egg",
			settings -> new SpawnEggItem(ModEntities.NPC, settings), new Item.Settings());

	/** Spawn egg for Dock's Indev human. */
	public static final Item HUMAN_SPAWN_EGG = register("human_spawn_egg",
			settings -> new SpawnEggItem(ModEntities.HUMAN, settings), new Item.Settings());

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
