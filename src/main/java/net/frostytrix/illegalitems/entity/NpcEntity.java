package net.frostytrix.illegalitems.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.frostytrix.illegalitems.screen.NpcSkinScreenHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * The Education Edition NPC: a signpost that happens to be person-shaped.
 *
 * <p>It has no AI whatsoever, never moves, ignores gravity, and cannot be damaged or affected by
 * potions. Commands still remove it, as with the agent, so there is a way to clear one.
 *
 * <p>Its skin is one of {@link #SKINS}, chosen rather than rolled, matching the real one's picker.
 * Sneak and right-click opens it.
 */
public class NpcEntity extends MobEntity {
	/** Every skin present in the mod's assets, in the order the picker walks through them. */
	public static final String[] SKINS = {
			"npc_4", "npc_5", "npc_6", "npc_7", "npc_8", "npc_9", "npc_10",
			"npc_apiary_1", "npc_apiary_2", "npc_apiary_3", "npc_apiary_4", "npc_apiary_5",
			"npc_construction_1", "npc_construction_2", "npc_construction_3", "npc_construction_4",
			"npc_construction_5",
			"npc_scientist_1", "npc_scientist_2", "npc_scientist_3", "npc_scientist_4",
			"npc_scientist_5", "npc_scientist_6", "npc_scientist_7", "npc_scientist_8",
			"npc_scientist_9", "npc_scientist_10",
			"npc_teacher_1", "npc_teacher_2", "npc_teacher_3", "npc_teacher_4", "npc_teacher_5",
	};

	private static final TrackedData<Integer> SKIN =
			DataTracker.registerData(NpcEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public NpcEntity(EntityType<? extends MobEntity> type, World world) {
		super(type, world);
		setNoGravity(true);
		setInvulnerable(true);
		setPersistent();
	}

	public static DefaultAttributeContainer.Builder createNpcAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 20.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.0);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SKIN, 0);
	}

	/** No goals at all — the real one is furniture. */
	@Override
	protected void initGoals() {
	}

	public int getSkin() {
		return dataTracker.get(SKIN);
	}

	public void setSkin(int skin) {
		dataTracker.set(SKIN, Math.floorMod(skin, SKINS.length));
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (getWorld().isClient()) {
			return ActionResult.SUCCESS;
		}

		// Sneaking opens the skin picker. Plain right-click is left free for the dialogue interface.
		if (player.isSneaking()) {
			player.openHandledScreen(new SimpleNamedScreenHandlerFactory(
					(syncId, inventory, viewer) -> new NpcSkinScreenHandler(syncId, inventory, this),
					Text.translatable("container.illegal_items.npc_skins")));
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	@Override
	public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
		return true;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	/** As with the agent: nothing hurts it, but commands can still clear one away. */
	@Override
	public void kill(ServerWorld world) {
		remove(RemovalReason.KILLED);
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	protected void pushAway(Entity entity) {
	}

	@Override
	public boolean canBeHitByProjectile() {
		return false;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("Skin", getSkin());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		setSkin(nbt.getInt("Skin").orElse(0));
	}
}
