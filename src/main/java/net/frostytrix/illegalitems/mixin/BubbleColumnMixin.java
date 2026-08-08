package net.frostytrix.illegalitems.mixin;

import net.frostytrix.illegalitems.registry.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Gives vanilla's bubble column a dry variant, for the one the mod's item places.
 *
 * <p>A bubble column is water: {@code getFluidState} hands back a still water state outright, with no
 * property behind it and no position to vary on. So the property is added here instead, and because
 * it is named for the wet case it defaults to true — every column the game makes for itself is
 * untouched, and only one placed from the item comes out dry.
 *
 * <p>A dry column also behaves like the Bedrock one: it is not held up by anything, and it is gone
 * again on the next tick rather than lingering or collapsing into water.
 */
@Mixin(BubbleColumnBlock.class)
public class BubbleColumnMixin {
	@Inject(method = "appendProperties", at = @At("TAIL"))
	private void illegalItems$addWet(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
		builder.add(ModProperties.WET);
	}

	@Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
	private void illegalItems$dryHasNoWater(BlockState state, CallbackInfoReturnable<FluidState> info) {
		if (!state.get(ModProperties.WET)) {
			info.setReturnValue(Fluids.EMPTY.getDefaultState());
		}
	}

	/**
	 * Vanilla wants soul sand, magma or another column underneath and turns anything else back into
	 * water. A dry column is allowed to stand on nothing, which is what stops it leaving a block of
	 * water behind when it goes.
	 */
	@Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
	private void illegalItems$dryStandsOnNothing(BlockState state, WorldView world, BlockPos pos,
			CallbackInfoReturnable<Boolean> info) {
		if (!state.get(ModProperties.WET)) {
			info.setReturnValue(true);
		}
	}

	/**
	 * Makes a dry column lift you, which is the entire point of a bubble column.
	 *
	 * <p>Vanilla decides between the surface effect and the real one by looking at the block above: no
	 * collision shape and no fluid means it treats the column as having reached open air, and gives
	 * only the bobbing and a puff of bubbles at the top. A dry column always looks like that from
	 * above — even to another dry column stacked on it, since neither carries fluid — so it would only
	 * ever bob. Taking the other branch outright is what turns it back into a lift.
	 *
	 * <p>Vanilla's own effect is not used, because its numbers assume water. It adds 0.06 to upward
	 * velocity per tick, which works underwater where gravity is nearly cancelled, but loses outright
	 * to the 0.08 an entity falls at in open air — from a standing start you would never leave the
	 * ground, and the column would only ever top up speed you already had. These figures are the same
	 * shape, raised enough to win in air.
	 */
	@Inject(method = "onEntityCollision", at = @At("HEAD"), cancellable = true)
	private void illegalItems$dryStillLifts(BlockState state, World world, BlockPos pos, Entity entity,
			EntityCollisionHandler handler, CallbackInfo info) {
		if (state.get(ModProperties.WET)) {
			return;
		}

		Vec3d velocity = entity.getVelocity();
		double y = state.get(BubbleColumnBlock.DRAG)
				? Math.max(-MAX_SINK, velocity.y - SINK_PER_TICK)
				: Math.min(MAX_RISE, velocity.y + RISE_PER_TICK);

		entity.setVelocity(velocity.x, y, velocity.z);
		// As vanilla does, so a ride up and back down is not a fall.
		entity.onLanding();
		info.cancel();
	}

	/**
	 * Tuned so a dry column carries you at exactly the speed a water one does, rather than at vanilla's
	 * raw figures, which only make sense against water's much weaker gravity.
	 *
	 * <p>In water the column adds 0.06 a tick against a drag of 0.8 and a gravity of 0.005, settling at
	 * 0.215 a tick — 4.3 blocks a second — after about 11 ticks. In air the drag is 0.98 and the gravity
	 * 0.08, so the caps below are the speeds that come out at that same 4.3 up and 2.9 down once air has
	 * taken its share, and the rise per tick is what reproduces water's unhurried ramp rather than
	 * snapping to full speed in four ticks.
	 *
	 * <p>Falling is the exception: gravity alone already pulls harder than the whirlpool does, so there
	 * the cap does the work on its own and the descent reaches full speed sooner than in water.
	 */
	private static final double RISE_PER_TICK = 0.10;
	private static final double MAX_RISE = 0.2994;
	private static final double SINK_PER_TICK = 0.03;
	private static final double MAX_SINK = 0.068;

	/** The tick the item scheduled when it placed this: the column's whole life, then air again. */
	@Inject(method = "scheduledTick", at = @At("HEAD"), cancellable = true)
	private void illegalItems$dryLastsOneTick(BlockState state, ServerWorld world, BlockPos pos,
			Random random, CallbackInfo info) {
		if (!state.get(ModProperties.WET)) {
			world.removeBlock(pos, false);
			info.cancel();
		}
	}
}
