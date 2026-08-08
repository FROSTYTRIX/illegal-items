package net.frostytrix.illegalitems.entity;

import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * The Education Edition agent: the little robot that Code Builder drives around.
 *
 * <p>Faithful to how it behaves there, which is mostly by <em>not</em> doing things. It never moves
 * on its own, nothing can hurt it, and it passes straight through blocks and entities rather than
 * colliding with them. What it does have is a 27 slot inventory, opened by right-clicking it.
 *
 * <p>Without Code Builder there is nothing to program it with, so this is the agent as an object:
 * a stationary, indestructible container that ignores the world around it.
 */
public class AgentEntity extends MobEntity implements NamedScreenHandlerFactory {
	public static final int INVENTORY_SIZE = 27;

	private final SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);

	public AgentEntity(EntityType<? extends MobEntity> type, World world) {
		super(type, world);
		// Passes through blocks, vehicles and projectiles, and gravity does not apply.
		noClip = true;
		setNoGravity(true);
		setInvulnerable(true);
	}

	public static DefaultAttributeContainer.Builder createAgentAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 20.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.0);
	}

	/** Deliberately no goals: the agent only ever moves when something tells it to. */
	@Override
	protected void initGoals() {
	}

	/**
	 * No amount of damage touches an agent. Both the invulnerability check and the damage call
	 * itself are closed, because {@code /kill} arrives as generic-kill damage that is flagged to
	 * bypass ordinary invulnerability.
	 */
	@Override
	public boolean isInvulnerableTo(ServerWorld world, DamageSource source) {
		return true;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return false;
	}

	/**
	 * Commands are the exception, and deliberately so: with damage fully blocked, {@code /kill}
	 * would otherwise leave no way at all to get rid of one. Removing the entity directly sidesteps
	 * the damage path it normally goes through.
	 */
	@Override
	public void kill(ServerWorld world) {
		remove(RemovalReason.KILLED);
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	protected void pushAway(net.minecraft.entity.Entity entity) {
	}

	@Override
	public boolean canUsePortals(boolean allowVehicles) {
		return false;
	}

	/** It cannot be seated in anything, matching the real one. */
	@Override
	protected boolean canStartRiding(net.minecraft.entity.Entity vehicle) {
		return false;
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		if (!getWorld().isClient()) {
			player.openHandledScreen(this);
		}

		return ActionResult.SUCCESS;
	}

	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Text getDisplayName() {
		return Text.translatable("entity.illegal_items.agent");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, inventory);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		Inventories.writeNbt(nbt, inventory.getHeldStacks(), getRegistryManager());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		Inventories.readNbt(nbt, inventory.getHeldStacks(), getRegistryManager());
	}

	/** Spilling the inventory on death cannot happen — nothing can kill it — but be tidy anyway. */
	@Override
	protected void dropInventory(ServerWorld world) {
		super.dropInventory(world);

		for (int slot = 0; slot < inventory.size(); slot++) {
			dropStack(world, inventory.getStack(slot));
		}
	}
}
