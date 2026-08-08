package net.frostytrix.illegalitems.registry;

import net.minecraft.state.property.BooleanProperty;

/** Block state properties the mod adds to blocks that are not its own. */
public final class ModProperties {
	private ModProperties() {
	}

	/**
	 * Added to vanilla's bubble column, marking whether it carries water.
	 *
	 * <p>Named for the wet case rather than the dry one on purpose. A {@code BooleanProperty} lists its
	 * values as {@code [true, false]}, and a block's default state takes the first value of every
	 * property — so calling this {@code wet} makes {@code wet=true} the default at no cost, and every
	 * bubble column the game makes for itself carries on exactly as before. Only a column placed from
	 * the item is dry.
	 */
	public static final BooleanProperty WET = BooleanProperty.of("wet");
}
