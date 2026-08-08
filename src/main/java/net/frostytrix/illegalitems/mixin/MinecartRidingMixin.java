package net.frostytrix.illegalitems.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

/**
 * Picks up the mobs vanilla refuses to seat.
 *
 * <p>Both minecart controllers skip anything matching {@code PlayerEntity}, {@code IronGolemEntity}
 * or {@code AbstractMinecartEntity} and shove it aside instead of seating it, so an iron golem can
 * never board one. That check is a bare {@code instanceof} with no call to intercept, and the
 * MixinExtras bundled with the loader here is too old for expression injection, so rather than
 * rewrite either controller this sweeps up whatever they left behind.
 *
 * <p>Runs after the controller has had its turn, and only on an empty rideable cart, so mobs vanilla
 * already seats are untouched — by then the cart has a passenger and this does nothing. Players and
 * other minecarts stay excluded on purpose: players board by right-clicking, and carts riding carts
 * is not a thing anybody wants.
 */
@Mixin(AbstractMinecartEntity.class)
public class MinecartRidingMixin {

	@Inject(method = "tick", at = @At("TAIL"))
	private void illegalItems$seatExcludedMobs(CallbackInfo info) {
		AbstractMinecartEntity self = (AbstractMinecartEntity) (Object) this;

		if (self.getWorld().isClient() || !self.isRideable() || self.hasPassengers()) {
			return;
		}

		List<Entity> candidates = self.getWorld().getOtherEntities(self,
				self.getBoundingBox().expand(0.2, 0.0, 0.2),
				entity -> entity instanceof LivingEntity
						&& !(entity instanceof PlayerEntity)
						&& !entity.hasVehicle());

		if (!candidates.isEmpty()) {
			candidates.get(0).startRiding(self);
		}
	}
}
