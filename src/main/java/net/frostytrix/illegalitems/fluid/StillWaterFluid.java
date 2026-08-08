package net.frostytrix.illegalitems.fluid;

import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;

/**
 * Water that never spreads.
 *
 * <p>Extending {@link WaterFluid} rather than {@code FlowableFluid} directly is deliberate: it keeps
 * the swimming, drowning, fog, particles, fire extinguishing and — importantly — the vanilla water
 * bucket as this fluid's bucket item, so scooping it hands you real water. Being in the
 * {@code minecraft:water} fluid tag covers the rest of the interactions other mods look for.
 *
 * <p>The one thing taken away is spreading: {@link #onScheduledTick} is where {@code FlowableFluid}
 * does its flowing, so overriding it to do nothing leaves the source sitting exactly where it was
 * placed, forever.
 */
public abstract class StillWaterFluid extends WaterFluid {

	@Override
	public Fluid getStill() {
		return ModFluids.STILL_WATER;
	}

	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_WATER;
	}

	/**
	 * Vanilla's {@code WaterFluid} matches only {@code Fluids.WATER} and {@code FLOWING_WATER}, so
	 * inheriting it would leave this fluid not even matching itself. The fluid renderer uses this
	 * when sampling the corner heights of a fluid surface, and a fluid that fails its own check
	 * resolves every corner to zero — which draws the whole thing flat against the block below.
	 *
	 * <p>Deliberately not matching vanilla water: the check is asymmetric (vanilla would still not
	 * match this one), and {@code minecraft:water} tag membership is the correct way to be treated
	 * as water.
	 */
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == ModFluids.STILL_WATER || fluid == ModFluids.FLOWING_WATER;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return ModBlocks.WATER.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}

	/** Vanilla spreads here. Doing nothing is the whole point of this fluid. */
	@Override
	public void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState state) {
	}

	public static class Still extends StillWaterFluid {
		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends StillWaterFluid {
		@Override
		protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
			super.appendProperties(builder);
			builder.add(LEVEL);
		}

		@Override
		public int getLevel(FluidState state) {
			return state.get(LEVEL);
		}

		@Override
		public boolean isStill(FluidState state) {
			return false;
		}
	}
}
