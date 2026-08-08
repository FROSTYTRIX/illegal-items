package net.frostytrix.illegalitems.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

/**
 * The Pocket Edition nether reactor.
 *
 * <p>Build the 3x3x3 shell around the core and tap it: the shell turns to glowing obsidian, a
 * netherrack spire grows around it, and for 45 seconds it showers out nether loot and angry
 * zombified piglins. When it finishes, the shell becomes plain obsidian and the core is spent.
 *
 * <p>The countdown lives in the block state rather than a block entity — {@link #COUNTDOWN} ticks
 * from 15 to 0 on a scheduled tick every {@link #STEP_TICKS}, which is both the loot timer and the
 * 45 second lifetime.
 */
public class NetherReactorCoreBlock extends Block {
	public static final EnumProperty<ReactorPhase> PHASE = EnumProperty.of("phase", ReactorPhase.class);
	public static final IntProperty COUNTDOWN = IntProperty.of("countdown", 0, 15);

	/** 15 steps of three seconds each is the 45 second reaction. */
	private static final int STEPS = 15;
	private static final int STEP_TICKS = 60;

	/** The spire, as offsets from the core: a wide hollow tower that tapers as it rises. */
	private static final int SPIRE_BOTTOM = -2;
	private static final int SPIRE_TOP = 21;
	private static final int SPIRE_BASE_RADIUS = 5;

	/** The shell, in the order it turns to obsidian over the course of the reaction. */
	private static final List<BlockPos> SHELL = shellPositions();

	public NetherReactorCoreBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(PHASE, ReactorPhase.UNUSED).with(COUNTDOWN, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(PHASE, COUNTDOWN);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player,
			BlockHitResult hit) {
		if (state.get(PHASE) != ReactorPhase.UNUSED) {
			return ActionResult.PASS;
		}

		if (world.isClient()) {
			return ActionResult.SUCCESS;
		}

		// Say why nothing happened rather than failing silently — an unlit reactor is otherwise
		// indistinguishable from a broken one.
		if (!isStructureComplete(world, pos)) {
			player.sendMessage(Text.translatable("message.illegal_items.reactor_incomplete"), true);
			return ActionResult.PASS;
		}

		activate((ServerWorld) world, pos, state);
		return ActionResult.SUCCESS;
	}

	/**
	 * Lights the reactor if the shell is built correctly. Separate from {@link #onUse} so the
	 * reaction can be driven without a player.
	 *
	 * <p>Pocket Edition also refused to work outside Y 4 to 96, a limit that came from its 128 block
	 * tall worlds. That restriction is dropped here, since it would rule out most of a 1.21.5 world.
	 */
	public boolean tryActivate(ServerWorld world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if (!state.isOf(this) || state.get(PHASE) != ReactorPhase.UNUSED) {
			return false;
		}

		if (!isStructureComplete(world, pos)) {
			return false;
		}

		activate(world, pos, state);
		return true;
	}

	// The shell, as offsets from the core. Bottom layer is gold on the corners and cobblestone
	// everywhere else, the middle layer is cobblestone on the corners only, and the top layer is a
	// cobblestone plus. Everything not listed has to be air.
	private static boolean isStructureComplete(World world, BlockPos core) {
		for (BlockPos offset : shell(Blocks.GOLD_BLOCK)) {
			if (!world.getBlockState(core.add(offset.getX(), offset.getY(), offset.getZ()))
					.isOf(Blocks.GOLD_BLOCK)) {
				return false;
			}
		}

		for (BlockPos offset : shell(Blocks.COBBLESTONE)) {
			if (!world.getBlockState(core.add(offset.getX(), offset.getY(), offset.getZ()))
					.isOf(Blocks.COBBLESTONE)) {
				return false;
			}
		}

		return true;
	}

	private static List<BlockPos> shell(Block material) {
		List<BlockPos> positions = new ArrayList<>();

		if (material == Blocks.GOLD_BLOCK) {
			for (int dx = -1; dx <= 1; dx += 2) {
				for (int dz = -1; dz <= 1; dz += 2) {
					positions.add(new BlockPos(dx, -1, dz));
				}
			}

			return positions;
		}

		// Bottom: the whole layer except the corners.
		positions.add(new BlockPos(0, -1, 0));
		positions.add(new BlockPos(1, -1, 0));
		positions.add(new BlockPos(-1, -1, 0));
		positions.add(new BlockPos(0, -1, 1));
		positions.add(new BlockPos(0, -1, -1));

		// Middle: corners only.
		for (int dx = -1; dx <= 1; dx += 2) {
			for (int dz = -1; dz <= 1; dz += 2) {
				positions.add(new BlockPos(dx, 0, dz));
			}
		}

		// Top: a plus.
		positions.add(new BlockPos(0, 1, 0));
		positions.add(new BlockPos(1, 1, 0));
		positions.add(new BlockPos(-1, 1, 0));
		positions.add(new BlockPos(0, 1, 1));
		positions.add(new BlockPos(0, 1, -1));

		return positions;
	}

	private void activate(ServerWorld world, BlockPos core, BlockState state) {
		// The spire goes up all at once; it is the shell that changes slowly, over the reaction.
		replaceShell(world, core, Blocks.CRYING_OBSIDIAN);
		raiseSpire(world, core, SPIRE_BOTTOM, SPIRE_TOP);

		world.setBlockState(core, state.with(PHASE, ReactorPhase.ACTIVE).with(COUNTDOWN, STEPS));
		world.scheduleBlockTick(core, this, STEP_TICKS);
		world.playSound(null, core, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN,
				SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (state.get(PHASE) != ReactorPhase.ACTIVE) {
			return;
		}

		int left = state.get(COUNTDOWN);

		if (left <= 0) {
			replaceShell(world, pos, Blocks.OBSIDIAN);
			world.setBlockState(pos, state.with(PHASE, ReactorPhase.BURNT_OUT).with(COUNTDOWN, 0));
			world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 0.7F);
			return;
		}

		// The glowing obsidian creeps over to plain obsidian across the 45 seconds, and the loot
		// keeps coming while it does.
		int step = STEPS - left;
		int done = SHELL.size() * step / STEPS;
		int next = SHELL.size() * (step + 1) / STEPS;

		for (int i = done; i < next; i++) {
			BlockPos offset = SHELL.get(i);
			world.setBlockState(pos.add(offset.getX(), offset.getY(), offset.getZ()),
					Blocks.OBSIDIAN.getDefaultState());
		}

		dropLoot(world, pos, random);

		if (random.nextInt(3) == 0) {
			spawnPiglin(world, pos);
		}

		world.setBlockState(pos, state.with(COUNTDOWN, left - 1));
		world.scheduleBlockTick(pos, this, STEP_TICKS);
	}

	private static void replaceShell(ServerWorld world, BlockPos core, Block replacement) {
		for (BlockPos offset : SHELL) {
			world.setBlockState(core.add(offset.getX(), offset.getY(), offset.getZ()),
					replacement.getDefaultState());
		}
	}

	/** Every shell position, gold first, in a stable order. */
	private static List<BlockPos> shellPositions() {
		List<BlockPos> all = new ArrayList<>(shell(Blocks.GOLD_BLOCK));
		all.addAll(shell(Blocks.COBBLESTONE));
		return List.copyOf(all);
	}

	/**
	 * Raises the netherrack spire between two heights, measured from the core.
	 *
	 * <p>A hollow tower five blocks out at the base, narrowing towards the top, with a floor every
	 * six layers so it has rooms inside like the original. Anything already solid is left alone,
	 * which is what keeps the reactor's own obsidian shell from being swallowed.
	 */
	private static void raiseSpire(ServerWorld world, BlockPos core, int fromY, int toY) {
		for (int y = Math.max(fromY, SPIRE_BOTTOM); y <= Math.min(toY, SPIRE_TOP); y++) {
			int radius = radiusAt(y);

			for (int x = -radius; x <= radius; x++) {
				for (int z = -radius; z <= radius; z++) {
					boolean wall = Math.abs(x) == radius || Math.abs(z) == radius;
					boolean floor = y == SPIRE_BOTTOM || y == SPIRE_TOP
							|| (y - SPIRE_BOTTOM) % 6 == 0;

					if (!wall && !floor) {
						continue;
					}

					BlockPos at = core.add(x, y, z);
					BlockState existing = world.getBlockState(at);

					if (!existing.isAir() && !existing.isReplaceable()) {
						continue;
					}

					world.setBlockState(at, Math.floorMod(x * 7 + y * 13 + z * 5, 23) == 0
							? Blocks.GLOWSTONE.getDefaultState()
							: Blocks.NETHERRACK.getDefaultState());
				}
			}
		}
	}

	/** Five wide at the bottom, tapering to a point over the top third. */
	private static int radiusAt(int y) {
		float progress = (float) (y - SPIRE_BOTTOM) / (SPIRE_TOP - SPIRE_BOTTOM);
		int radius = Math.round(SPIRE_BASE_RADIUS - progress * progress * (SPIRE_BASE_RADIUS - 1));
		return Math.max(1, radius);
	}

	private static void dropLoot(ServerWorld world, BlockPos core, Random random) {
		// Roughly a stack each of glowstone and quartz over the reaction, with a quarter stack of
		// the odds and ends, matching what the original reactor produced.
		ItemStack stack = switch (random.nextInt(8)) {
			case 0, 1, 2 -> new ItemStack(Items.GLOWSTONE_DUST, 4);
			case 3, 4, 5 -> new ItemStack(Items.QUARTZ, 4);
			case 6 -> new ItemStack(random.nextBoolean() ? Items.CACTUS : Items.SUGAR_CANE, 1);
			default -> new ItemStack(switch (random.nextInt(4)) {
				case 0 -> Items.BROWN_MUSHROOM;
				case 1 -> Items.RED_MUSHROOM;
				case 2 -> Items.PUMPKIN_SEEDS;
				default -> Items.MELON_SEEDS;
			}, 1);
		};

		ItemEntity item = new ItemEntity(world,
				core.getX() + 0.5, core.getY() + 1.5, core.getZ() + 0.5, stack);
		item.setVelocity((random.nextDouble() - 0.5) * 0.2, 0.25, (random.nextDouble() - 0.5) * 0.2);
		world.spawnEntity(item);
	}

	private static void spawnPiglin(ServerWorld world, BlockPos core) {
		ZombifiedPiglinEntity piglin =
				EntityType.ZOMBIFIED_PIGLIN.create(world, SpawnReason.TRIGGERED);

		if (piglin == null) {
			return;
		}

		piglin.refreshPositionAndAngles(core.getX() + 0.5, core.getY() + 1.0, core.getZ() + 0.5,
				world.random.nextFloat() * 360.0F, 0.0F);
		world.spawnEntity(piglin);

		// The originals were hostile on sight, unlike the neutral ones in modern versions.
		PlayerEntity nearest = world.getClosestPlayer(piglin, 16.0);

		if (nearest != null) {
			piglin.setAngryAt(nearest.getUuid());
			piglin.setTarget(nearest);
		}
	}
}
