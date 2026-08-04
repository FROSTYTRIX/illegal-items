package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

import net.minecraft.component.type.ItemEnchantmentsComponent;

/**
 * Removes the 255 ceiling from the component that stores enchantments on a stack.
 *
 * <p>Lifting {@code getMaxLevel} in the anvil and {@code /enchant} is not enough on its own: the
 * level still has to survive being written to the item. Vanilla guards that in two places here —
 * the constructor throws outside 0..255, and {@code ENCHANTMENT_LEVEL_CODEC} is
 * {@code Codec.intRange(1, 255)}, which is what serialises the level to disk.
 *
 * <p>The network path needs nothing: the level is sent as {@code PacketCodecs.VAR_INT}, which is
 * already unbounded.
 */
@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {

	/** The constructor's {@code level > 255} bounds check. */
	@ModifyConstant(method = "<init>", constant = @Constant(intValue = 255))
	private static int illegalItems$uncapBoundsCheck(int original) {
		return Integer.MAX_VALUE;
	}

	/** The upper bound of {@code ENCHANTMENT_LEVEL_CODEC}, which saves the level to disk. */
	@ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 255))
	private static int illegalItems$uncapSaveCodec(int original) {
		return Integer.MAX_VALUE;
	}
}
