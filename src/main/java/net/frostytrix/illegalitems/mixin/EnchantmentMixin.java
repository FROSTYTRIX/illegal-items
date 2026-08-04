package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.frostytrix.illegalitems.util.Numerals;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Makes illegal enchantment levels render properly.
 *
 * <p>Vanilla builds the level suffix as {@code Text.translatable("enchantment.level." + level)} and
 * only ships lang keys for 1 through 10, so anything higher shows the raw key —
 * "Sharpness enchantment.level.255". This swaps in a real numeral for those levels and leaves the
 * vanilla path untouched for 1 through 10.
 */
@Mixin(Enchantment.class)
public class EnchantmentMixin {

	private static final String LEVEL_KEY_PREFIX = "enchantment.level.";

	@Redirect(method = "getName", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
	private static MutableText illegalItems$levelNumeral(String key) {
		if (key.startsWith(LEVEL_KEY_PREFIX)) {
			try {
				int level = Integer.parseInt(key.substring(LEVEL_KEY_PREFIX.length()));

				if (!Numerals.hasVanillaLangKey(level)) {
					return Text.literal(Numerals.of(level));
				}
			} catch (NumberFormatException ignored) {
				// Not a level key after all; fall through to the vanilla lookup.
			}
		}

		return Text.translatable(key);
	}
}
