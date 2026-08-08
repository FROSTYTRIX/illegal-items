package net.frostytrix.illegalitems.registry;

import net.frostytrix.illegalitems.IllegalItems;
import net.frostytrix.illegalitems.fluid.StillLavaFluid;
import net.frostytrix.illegalitems.fluid.StillWaterFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

/**
 * The non-spreading fluids. A {@code FlowableFluid} always needs both a still and a flowing form
 * even when it never flows, because the fluid state machine resolves one from the other.
 */
public final class ModFluids {
	private ModFluids() {
	}

	public static final FlowableFluid STILL_WATER = register("still_water", new StillWaterFluid.Still());
	public static final FlowableFluid FLOWING_WATER = register("flowing_water", new StillWaterFluid.Flowing());

	public static final FlowableFluid STILL_LAVA = register("still_lava", new StillLavaFluid.Still());
	public static final FlowableFluid FLOWING_LAVA = register("flowing_lava", new StillLavaFluid.Flowing());

	private static <T extends Fluid> T register(String name, T fluid) {
		return Registry.register(Registries.FLUID, IllegalItems.id(name), fluid);
	}

	public static void initialize() {
	}
}
