package net.frostytrix.illegalitems.mixin;

import java.util.Collection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.EnchantCommand;

/**
 * Lets {@code /enchant} hand out illegal levels. Vanilla rejects anything above the enchantment's
 * maximum with "level is not supported", which is why Sharpness stopped at 5.
 *
 * <p>The command's level argument is already an unbounded {@code integer(0)}, so lifting the
 * maximum is all that is needed — {@code /enchant @s sharpness 255} then works.
 *
 * <p>Whether the enchantment suits the item is not handled here. Fabric API redirects that call for
 * its own event, so it goes through {@code EnchantmentEvents.ALLOW_ENCHANTING} in
 * {@code ModEnchantments}, same as the anvil.
 */
@Mixin(EnchantCommand.class)
public class EnchantCommandMixin {

	@Redirect(method = "execute", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private static int illegalItems$uncapCommandLevel(Enchantment enchantment) {
		return Integer.MAX_VALUE;
	}

	/** Matches the anvil, where mutually exclusive enchantments already stack. */
	@Redirect(method = "execute", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/enchantment/EnchantmentHelper;isCompatible(Ljava/util/Collection;Lnet/minecraft/registry/entry/RegistryEntry;)Z"))
	private static boolean illegalItems$ignoreCommandExclusivity(
			Collection<RegistryEntry<Enchantment>> existing, RegistryEntry<Enchantment> candidate) {
		return true;
	}
}
