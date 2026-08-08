package net.frostytrix.illegalitems.util;

import java.util.UUID;

import net.minecraft.entity.LivingEntity;

/**
 * Decides which baby mobs grow to adult size while keeping their baby proportions.
 *
 * <p>The roll is derived from the entity's UUID rather than rolled at spawn and stored. That keeps
 * it stable for free: the same mob always gives the same answer, it survives saving and reloading
 * with no extra data to persist, and the client works it out identically to the server, so nothing
 * has to be synced for the mob to render at the right size.
 */
public final class GiantBabies {
	private GiantBabies() {
	}

	/** Chance, in percent, that any given baby is a giant one. */
	public static final int CHANCE_PERCENT = 5;

	public static boolean isGiantBaby(LivingEntity entity) {
		if (!entity.isBaby()) {
			return false;
		}

		UUID id = entity.getUuid();
		return bucket(id.getMostSignificantBits() ^ id.getLeastSignificantBits()) < CHANCE_PERCENT;
	}

	/**
	 * Spreads a UUID's bits into a well-distributed 0-99 bucket. This runs inside
	 * {@code getScaleFactor}, which is called constantly, so it stays allocation free rather than
	 * building a {@code Random} each time.
	 */
	private static int bucket(long bits) {
		long z = bits + 0x9E3779B97F4A7C15L;
		z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
		z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
		z = z ^ (z >>> 31);
		return (int) Math.floorMod(z, 100L);
	}
}
