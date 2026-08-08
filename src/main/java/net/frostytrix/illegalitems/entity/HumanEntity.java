package net.frostytrix.illegalitems.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

/**
 * Dock's "human" test mob from Indev 0.31, which lasted about two days before being pulled.
 *
 * <p>It had no animation at all, so it slid around the world in a single frozen pose, jumping
 * constantly and never once stopping. That is reproduced literally: the wander goal supplies the
 * aimless drift, {@link ConstantJumpGoal} keeps it hopping, and the renderer deliberately withholds
 * the limb swing so it glides rather than walks.
 *
 * <p>Harmless — the originals paid you no attention whatsoever.
 */
public class HumanEntity extends PathAwareEntity {
	public HumanEntity(EntityType<? extends PathAwareEntity> type, World world) {
		super(type, world);
	}

	public static DefaultAttributeContainer.Builder createHumanAttributes() {
		return PathAwareEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 20.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.3)
				.add(EntityAttributes.FOLLOW_RANGE, 16.0);
	}

	/**
	 * Steve stays where he is put. Without this he is an ordinary despawnable creature: any that drift
	 * more than 32 blocks away start rolling to vanish, and past 128 they go instantly. Vanilla animals
	 * survive for exactly this reason — {@code AnimalEntity} overrides the same method — and
	 * {@link PathAwareEntity} does not, so it has to be said here.
	 */
	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}

	@Override
	protected void initGoals() {
		goalSelector.add(0, new SwimGoal(this));
		// Steers itself; see ConstantJumpGoal for why pathfinding is not used here.
		goalSelector.add(1, new ConstantJumpGoal(this));
		// Infdev's version could still turn its head even once it stopped moving.
		goalSelector.add(2, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		goalSelector.add(3, new LookAroundGoal(this));
	}
}
