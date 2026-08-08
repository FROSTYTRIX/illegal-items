package net.frostytrix.illegalitems.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * The Education Edition border block. Looks like a wall, and behaves like one at its own height,
 * but the barrier it projects is unbounded: see {@code BorderBarrier} for the part that stops
 * anything crossing the column above or below it.
 */
public class BorderBlock extends WallBlock {
	public BorderBlock(Settings settings) {
		super(settings);
	}

	/** Border blocks trail red dust, which is the only hint that the column above them is sealed. */
	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		for (int i = 0; i < 2; i++) {
			world.addParticleClient(DustParticleEffect.DEFAULT,
					pos.getX() + random.nextDouble(),
					pos.getY() + random.nextDouble() * 1.5,
					pos.getZ() + random.nextDouble(),
					0.0, 0.0, 0.0);
		}
	}
}
