package net.frostytrix.illegalitems.entity;

import java.util.EnumSet;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Hops the moment it touches the ground, forever, carrying itself along as it goes.
 *
 * <p>Each hop gets a shove in whatever direction the mob is currently facing, and that direction is
 * re-rolled every few seconds. Pathfinding is not used at all: a mob that is airborne almost every
 * tick never gives the navigator a chance to make progress, which is why leaving this to
 * {@code WanderAroundFarGoal} produced something that jumped enthusiastically on the spot. Steering
 * the hops directly is both simpler and closer to the aimless drift of the originals.
 */
public class ConstantJumpGoal extends Goal {
	private static final double HOP_SPEED = 0.28;
	private static final int MIN_HEADING_TICKS = 40;
	private static final int MAX_HEADING_TICKS = 100;

	private final PathAwareEntity human;
	private float heading;
	private int ticksUntilTurn;

	public ConstantJumpGoal(PathAwareEntity human) {
		this.human = human;
		// Takes the movement control so nothing else tries to steer at the same time.
		setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		return true;
	}

	@Override
	public boolean shouldContinue() {
		return true;
	}

	@Override
	public void start() {
		pickNewHeading();
	}

	@Override
	public void tick() {
		if (--ticksUntilTurn <= 0) {
			pickNewHeading();
		}

		if (!human.isOnGround()) {
			return;
		}

		human.setYaw(heading);
		human.bodyYaw = heading;

		double radians = heading * MathHelper.RADIANS_PER_DEGREE;
		Vec3d velocity = human.getVelocity();
		human.setVelocity(-MathHelper.sin((float) radians) * HOP_SPEED,
				velocity.y,
				MathHelper.cos((float) radians) * HOP_SPEED);
		human.getJumpControl().setActive();
	}

	private void pickNewHeading() {
		heading = human.getRandom().nextFloat() * 360.0F;
		ticksUntilTurn = MIN_HEADING_TICKS
				+ human.getRandom().nextInt(MAX_HEADING_TICKS - MIN_HEADING_TICKS);
	}
}
