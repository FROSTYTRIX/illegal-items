package net.frostytrix.illegalitems.util;

/**
 * Roman numerals for enchantment levels past the ten vanilla ships lang keys for.
 *
 * <p>Roman notation stops being readable well before it stops being possible, so anything at or
 * above {@link #ROMAN_LIMIT} falls back to digits — "Sharpness 2147483647" beats forty thousand Ms.
 */
public final class Numerals {
	private Numerals() {
	}

	/** Vanilla ships {@code enchantment.level.1} through {@code enchantment.level.10}. */
	public static final int VANILLA_MAX_LANG_LEVEL = 10;

	/** Standard Roman numerals only reach 3999 before needing overbars. */
	public static final int ROMAN_LIMIT = 4000;

	private static final int[] VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
	private static final String[] SYMBOLS = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

	public static boolean hasVanillaLangKey(int level) {
		return level >= 1 && level <= VANILLA_MAX_LANG_LEVEL;
	}

	/** Roman numerals where they stay readable, plain digits everywhere else. */
	public static String of(int level) {
		if (level < 1 || level >= ROMAN_LIMIT) {
			return Integer.toString(level);
		}

		StringBuilder builder = new StringBuilder();
		int remaining = level;

		for (int i = 0; i < VALUES.length; i++) {
			while (remaining >= VALUES[i]) {
				builder.append(SYMBOLS[i]);
				remaining -= VALUES[i];
			}
		}

		return builder.toString();
	}
}
