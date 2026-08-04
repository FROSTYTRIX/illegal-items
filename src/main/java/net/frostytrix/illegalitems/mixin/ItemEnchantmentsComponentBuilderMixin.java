package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.component.type.ItemEnchantmentsComponent;

/**
 * The clamps that actually produced the 255 ceiling. There are two independent ones, and both have
 * to go — they are reached by different callers:
 *
 * <ul>
 *   <li>{@code set} is what the anvil uses, via {@code AnvilScreenHandler.updateResult}.
 *   <li>{@code add} is what {@code /enchant} uses, via {@code ItemStack.addEnchantment}.
 * </ul>
 *
 * <p>Lifting only one leaves the other route still capped at 255.
 */
@Mixin(ItemEnchantmentsComponent.Builder.class)
public class ItemEnchantmentsComponentBuilderMixin {

	/** Used by the anvil. */
	@ModifyConstant(method = "set", constant = @Constant(intValue = 255))
	private int illegalItems$uncapSetLevel(int original) {
		return Integer.MAX_VALUE;
	}

	/** Used by {@code /enchant} and anything else going through {@code ItemStack.addEnchantment}. */
	@ModifyConstant(method = "add", constant = @Constant(intValue = 255))
	private int illegalItems$uncapAddedLevel(int original) {
		return Integer.MAX_VALUE;
	}
}
