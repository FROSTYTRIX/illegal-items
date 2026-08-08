package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.frostytrix.illegalitems.util.InfiniteConsumables;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

/**
 * Anything enchanted with Infinity is never used up when eaten or drunk — food, potions and milk
 * alike.
 *
 * <p>{@code finishConsumption} is where a consumed stack shrinks, and it is the only place that
 * happens, so skipping that one call leaves everything else intact — the hunger, the effects, the
 * sound, the particles.
 *
 * <p>Empty bottles are not duplicated by this. Vanilla only hands back a use remainder when the
 * stack actually got smaller ({@code UseRemainderComponent.convert} returns early otherwise), so an
 * endless potion simply never produces one.
 *
 * <p>Infinity cannot normally reach any of these items; it gets there through the anvil, which this
 * mod already lets accept any enchantment on any item.
 */
@Mixin(ConsumableComponent.class)
public class ConsumableComponentMixin {

	@Redirect(method = "finishConsumption", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;decrementUnlessCreative(ILnet/minecraft/entity/LivingEntity;)V"))
	private void illegalItems$keepInfiniteConsumables(ItemStack stack, int amount, LivingEntity user) {
		if (!InfiniteConsumables.isInfinite(stack)) {
			stack.decrementUnlessCreative(amount, user);
		}
	}
}
