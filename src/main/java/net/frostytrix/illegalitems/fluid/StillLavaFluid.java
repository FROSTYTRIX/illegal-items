package net.frostytrix.illegalitems.fluid;

import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;

/**
 * Lava that never spreads. The counterpart to {@link StillWaterFluid} — see there for why this
 * extends the vanilla fluid instead of {@code FlowableFluid}.
 *
 * <p>Keeping {@link LavaFluid} as the base means it still burns entities, lights the area, sets
 * fire to its surroundings and hands back a real lava bucket.
 */
public abstract class StillLavaFluid extends LavaFluid {

	@Override
	public Fluid getStill() {
		return ModFluids.STILL_LAVA;
	}

	@Override
	public Fluid getFlowing() {
		return ModFluids.FLOWING_LAVA;
	}

	/** See {@link StillWaterFluid#matchesType} — without this the fluid renders flat. */
	@Override
	public boolean matchesType(Fluid fluid) {
		return fluid == ModFluids.STILL_LAVA || fluid == ModFluids.FLOWING_LAVA;
	}

	@Override
	public BlockState toBlockState(FluidState state) {
		return ModBlocks.LAVA.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
	}

	/** Vanilla spreads here. Doing nothing is the whole point of this fluid. */
	@Override
	public void onScheduledTick(ServerWorld world, BlockPos pos, BlockState blockState, FluidState state) {
	}

	public static class Still extends StillLavaFluid {
		@Override
		public int getLevel(FluidState state) {
			return 8;
		}

		@Override
		public boolean isStill(FluidState state) {
			return true;
		}
	}

	public static class Flowing extends StillLavaFluid {
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
