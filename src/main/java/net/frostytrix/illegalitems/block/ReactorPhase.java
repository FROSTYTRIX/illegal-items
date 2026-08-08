package net.frostytrix.illegalitems.block;

import net.minecraft.util.StringIdentifiable;

/** The three appearances of a nether reactor core, as on Pocket Edition. */
public enum ReactorPhase implements StringIdentifiable {
	UNUSED("unused"),
	ACTIVE("active"),
	BURNT_OUT("burnt_out");

	private final String name;

	ReactorPhase(String name) {
		this.name = name;
	}

	@Override
	public String asString() {
		return name;
	}
}
