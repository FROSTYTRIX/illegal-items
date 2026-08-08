package net.frostytrix.illegalitems.util;

import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

/**
 * The infinite barrier a border block projects above and below itself.
 *
 * <p>A border seals its whole column, floor to sky, so the test is "does this column contain a
 * border anywhere" rather than anything to do with height. Scanning 384 blocks per column would be
 * far too slow to run on movement, so each chunk section is first asked whether its palette
 * contains a border at all — for the overwhelming majority of sections that is a couple of
 * comparisons and the 16 block lookups are skipped entirely.
 */
public final class BorderBarrier {
	private BorderBarrier() {
	}

	/** Operators are the worldbuilders here: the barrier does not apply to them. */
	public static boolean isExempt(Entity entity) {
		// Weapons, projectiles and explosions treat a border as an ordinary wall, so only living
		// things are held back by the column. The ender dragon ignores it outright.
		if (!(entity instanceof LivingEntity) || entity instanceof EnderDragonEntity) {
			return true;
		}

		return entity instanceof PlayerEntity player && isWorldbuilder(player);
	}

	/**
	 * Being an operator only counts while in creative — drop into survival and the border treats you
	 * like anybody else. {@code isCreativeLevelTwoOp} is vanilla's own name for that combination, and
	 * spectators are waved through because nothing stops them anyway.
	 */
	public static boolean isWorldbuilder(PlayerEntity player) {
		return player.isCreativeLevelTwoOp() || player.isSpectator();
	}

	public static boolean columnHasBorder(World world, int x, int z) {
		int chunkX = ChunkSectionPos.getSectionCoord(x);
		int chunkZ = ChunkSectionPos.getSectionCoord(z);

		if (!world.isChunkLoaded(chunkX, chunkZ)) {
			return false;
		}

		Chunk chunk = world.getChunk(chunkX, chunkZ);
		ChunkSection[] sections = chunk.getSectionArray();
		int localX = x & 15;
		int localZ = z & 15;

		for (ChunkSection section : sections) {
			if (section == null || section.isEmpty()
					|| !section.hasAny(state -> state.isOf(ModBlocks.BORDER))) {
				continue;
			}

			for (int y = 0; y < 16; y++) {
				if (section.getBlockState(localX, y, localZ).isOf(ModBlocks.BORDER)) {
					return true;
				}
			}
		}

		return false;
	}
}
