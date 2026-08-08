package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

/**
 * Lets the two bosses ride minecarts.
 *
 * <p>Both override {@code canStartRiding} to return false unconditionally, which is what stops them
 * boarding anything at all. This lifts that for minecarts only — they still cannot be put in boats
 * or ridden onto other mobs, which keeps the change to what was actually asked for.
 */
@Mixin({ WitherEntity.class, EnderDragonEntity.class })
public class BossRidingMixin {

	@Inject(method = "canStartRiding", at = @At("RETURN"), cancellable = true)
	private void illegalItems$rideMinecarts(Entity vehicle, CallbackInfoReturnable<Boolean> info) {
		if (vehicle instanceof AbstractMinecartEntity) {
			info.setReturnValue(true);
		}
	}
}
