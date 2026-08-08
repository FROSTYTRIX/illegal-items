package net.frostytrix.illegalitems.util;

import java.util.Map;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.frostytrix.illegalitems.registry.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * Mining the portals out of the world with a sufficiently illegal pickaxe.
 *
 * <p>All three portal blocks are unbreakable in vanilla — hardness of -1, so nothing touches them.
 * A netherite pickaxe carrying Efficiency 10 and Unbreaking 10 gets through anyway, and both of
 * those are past vanilla's ceilings of 5 and 3, so the only way to hold one is the mod's own illegal
 * enchanting. A tool that should not exist is what it takes to take a block that should not move.
 *
 * <p>They come out as hard as obsidian rather than instantly, and only the real block drops the
 * item, so a portal has to be found or lit before it can be taken.
 */
public final class PortalHarvest {
	private PortalHarvest() {
	}

	/** Both enchantments must be at least this high. Vanilla caps them at 5 and 3. */
	private static final int REQUIRED_LEVEL = 10;

	/**
	 * Portals have no hardness of their own to break against, so they are given one. Picked so that
	 * the weakest pickaxe that qualifies — netherite at Efficiency 10, a speed of 110 — takes three
	 * seconds: {@code 220 x 30 / 110} is 60 ticks. Four times obsidian, and every level of Efficiency
	 * past the tenth pulls it down from there.
	 */
	private static final float HARDNESS = 220.0F;

	private static final Map<Block, Item> DROPS = Map.of(
			Blocks.NETHER_PORTAL, ModItems.NETHER_PORTAL,
			Blocks.END_PORTAL, ModItems.END_PORTAL,
			Blocks.END_GATEWAY, ModItems.END_GATEWAY);

	/** Whether this player, holding what they are holding, may break this block at all. */
	public static boolean canHarvest(BlockState state, PlayerEntity player) {
		return DROPS.containsKey(state.getBlock()) && isPortalPick(player.getMainHandStack());
	}

	/**
	 * The vanilla breaking-speed formula, with the missing hardness filled in.
	 *
	 * <p>This only responds to Efficiency because the three portals are also put into
	 * {@code #minecraft:mineable/pickaxe}. {@code getBlockBreakingSpeed} adds the mining-efficiency
	 * attribute only when the tool's base multiplier is already above 1, which happens only for a tool
	 * that is correct for the block — without the tag the multiplier sits at 1, Efficiency is skipped
	 * entirely, and every qualifying pickaxe would take the same flat 75 seconds.
	 */
	public static float breakingDelta(BlockState state, PlayerEntity player) {
		return player.getBlockBreakingSpeed(state) / HARDNESS / 30.0F;
	}

	private static boolean isPortalPick(ItemStack stack) {
		return stack.isOf(Items.NETHERITE_PICKAXE)
				&& level(stack, Enchantments.EFFICIENCY) >= REQUIRED_LEVEL
				&& level(stack, Enchantments.UNBREAKING) >= REQUIRED_LEVEL;
	}

	/**
	 * Enchantments are registry entries rather than plain objects in 1.21.5, and reaching one by key
	 * needs a registry lookup we do not have here — so the stack's own list is walked instead.
	 */
	private static int level(ItemStack stack, RegistryKey<Enchantment> key) {
		ItemEnchantmentsComponent enchantments = EnchantmentHelper.getEnchantments(stack);

		for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
			if (entry.matchesKey(key)) {
				return enchantments.getLevel(entry);
			}
		}

		return 0;
	}

	public static void initialize() {
		PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
			if (world.isClient() || player.isCreative()) {
				return;
			}

			Item drop = DROPS.get(state.getBlock());

			if (drop != null && isPortalPick(player.getMainHandStack())) {
				Block.dropStack(world, pos, new ItemStack(drop));
			}
		});
	}
}
