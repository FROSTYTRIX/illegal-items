package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.frostytrix.illegalitems.util.BorderBarrier;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Stops anything walking, swimming, climbing or tunnelling into a column sealed by a border block,
 * at any height above or below it.
 *
 * <p>Clamping the movement vector as it enters {@code Entity.move} rather than teleporting the
 * entity back means it slides along the barrier like a wall instead of juddering, and it runs on
 * both sides so the client predicts the same stop and does not rubber-band.
 *
 * <p>Ender pearls still get through, and for free: a pearl repositions the player outright rather
 * than moving them, so it never passes through here.
 */
@Mixin(Entity.class)
public class BorderMovementMixin {

	@ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true)
	private Vec3d illegalItems$stopAtBorder(Vec3d movement) {
		Entity self = (Entity) (Object) this;

		if (BorderBarrier.isExempt(self) || (movement.x == 0.0 && movement.z == 0.0)) {
			return movement;
		}

		int fromX = MathHelper.floor(self.getX());
		int fromZ = MathHelper.floor(self.getZ());
		int toX = MathHelper.floor(self.getX() + movement.x);
		int toZ = MathHelper.floor(self.getZ() + movement.z);

		if (fromX == toX && fromZ == toZ) {
			return movement;
		}

		double x = movement.x;
		double z = movement.z;

		if (toX != fromX && BorderBarrier.columnHasBorder(self.getWorld(), toX, fromZ)) {
			x = 0.0;
		}

		if (toZ != fromZ && BorderBarrier.columnHasBorder(self.getWorld(), fromX, toZ)) {
			z = 0.0;
		}

		// The diagonal: stop a corner cut into a sealed column even when neither axis alone crosses.
		if (x != 0.0 && z != 0.0 && toX != fromX && toZ != fromZ
				&& BorderBarrier.columnHasBorder(self.getWorld(), toX, toZ)) {
			x = 0.0;
			z = 0.0;
		}

		return x == movement.x && z == movement.z ? movement : new Vec3d(x, movement.y, z);
	}
}
