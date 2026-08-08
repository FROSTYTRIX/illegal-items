package net.frostytrix.illegalitems.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;

/**
 * Block form of the non-spreading fluids.
 *
 * <p>{@code FluidBlock}'s constructor is protected, so this subclass exists mainly to reach it.
 * Draining is inherited untouched: {@code tryDrainFluid} hands back the fluid's bucket item, and
 * because these fluids report the vanilla water and lava buckets, filling a bucket here gives real
 * water or lava rather than anything mod-specific.
 */
public class StillFluidBlock extends FluidBlock {
	public StillFluidBlock(FlowableFluid fluid, Settings settings) {
		super(fluid, settings);
	}

	/** Placed sources are always full — these never decay, so the level never changes. */
	public BlockState fullState() {
		return getDefaultState().with(LEVEL, 0);
	}
}
