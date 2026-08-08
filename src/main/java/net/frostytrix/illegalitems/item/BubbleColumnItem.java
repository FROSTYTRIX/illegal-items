package net.frostytrix.illegalitems.item;

import net.frostytrix.illegalitems.registry.ModProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

/**
 * Places a real {@code minecraft:bubble_column}: dry, rising, and lasting exactly one tick.
 *
 * <p>Bedrock's does the same: the block goes down and is gone again immediately, which is half the
 * point of an item that was never meant to be held. The tick scheduled here is what ends it — see
 * {@code BubbleColumnMixin}, which removes any dry column that ticks.
 */
public class BubbleColumnItem extends BlockItem {
	public BubbleColumnItem(Block block, Settings settings) {
		super(block, settings);
	}

	/**
	 * Vanilla's version checks whether the state can be placed and returns null if not, which is what
	 * aborts the whole placement — no block, and not even a swing of the arm. Going through it and
	 * marking the state dry afterwards is too late: the state it tests is the block's default, which is
	 * wet, so it hits the soul-sand requirement and gives up. The same two steps are done here in the
	 * other order, so what gets tested is the dry state that is actually going down.
	 */
	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = getBlock().getPlacementState(context);

		if (state == null) {
			return null;
		}

		// drag=false is the rising column, the soul sand kind. The block's default is drag=true, the
		// magma whirlpool, which drags you down and draws its one particle at the top of the block.
		BlockState dry = state.with(ModProperties.WET, false).with(BubbleColumnBlock.DRAG, false);
		return canPlace(context, dry) ? dry : null;
	}

	/**
	 * How long a placed column lasts, in ticks. Bedrock's is gone again immediately, so 1 is the real
	 * value; 0 leaves it standing indefinitely, which is useful for actually looking at one.
	 */
	private static final int LIFETIME_TICKS = 1;

	/** Bubbles thrown out the moment it is placed. */
	private static final int BURST = 12;

	/** Vanilla's drift for a rising column: straight up, a little under a twentieth of a block a tick. */
	private static final double DRIFT = 0.04;

	@Override
	protected boolean place(ItemPlacementContext context, BlockState state) {
		boolean placed = super.place(context, state);

		if (!placed) {
			return false;
		}

		// Nothing else schedules a tick on a dry column — canPlaceAt keeps neighbour updates off it —
		// so skipping this is all it takes to make one permanent.
		if (LIFETIME_TICKS > 0) {
			context.getWorld().scheduleBlockTick(context.getBlockPos(), getBlock(), LIFETIME_TICKS);
		}

		burst(context, state);
		return true;
	}

	/**
	 * Bubbles on the tick it goes down, rather than waiting for the block's own random display tick.
	 *
	 * <p>That tick is a client-side lottery and may not come round for a while — a column living a
	 * single tick would usually be gone before it ever drew anything. Sent from the server so everyone
	 * sees the same burst.
	 */
	private void burst(ItemPlacementContext context, BlockState state) {
		if (!(context.getWorld() instanceof ServerWorld world)) {
			return;
		}

		BlockPos pos = context.getBlockPos();
		Random random = world.getRandom();
		boolean down = state.get(BubbleColumnBlock.DRAG);
		ParticleEffect particle = down ? ParticleTypes.CURRENT_DOWN : ParticleTypes.BUBBLE_COLUMN_UP;

		for (int i = 0; i < BURST; i++) {
			// A count of zero is what makes the three deltas a direction rather than a spread, which is
			// the only way to give these a velocity of their own.
			world.spawnParticles(particle,
					pos.getX() + random.nextDouble(),
					pos.getY() + random.nextDouble(),
					pos.getZ() + random.nextDouble(),
					0, 0.0, down ? -1.0 : 1.0, 0.0, DRIFT);
		}
	}
}
