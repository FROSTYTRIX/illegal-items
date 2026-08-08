package net.frostytrix.illegalitems.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.frostytrix.illegalitems.util.GiantBabies;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;

/**
 * Gives a small share of baby mobs the size of an adult while leaving them looking like babies.
 *
 * <p>{@code getScale} is the one value that controls how big a mob actually is. The hitbox is
 * {@code getBaseDimensions(pose).scaled(getScale())}, and the renderer feeds the same number into
 * {@code MatrixStack.scale} as {@code baseScale}, so scaling it up grows the mob and its collision
 * box together.
 *
 * <p>Note it is emphatically <em>not</em> {@code getScaleFactor}: since 1.21.4 a baby's smaller look
 * comes from a separate pre-scaled baby model layer, and {@code getScaleFactor} only survives as
 * {@code ageScale}, which models use for child head proportions and the shadow radius. Changing it
 * leaves the mob exactly the same size.
 *
 * <p>The baby model is untouched, because which model is used keys off {@code isBaby()}. The result
 * is a mob with baby proportions blown up to adult dimensions.
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Shadow
	protected abstract EntityDimensions getBaseDimensions(EntityPose pose);

	@Inject(method = "getScale", at = @At("RETURN"), cancellable = true)
	private void illegalItems$giantBabies(CallbackInfoReturnable<Float> info) {
		LivingEntity self = (LivingEntity) (Object) this;

		if (!GiantBabies.isGiantBaby(self)) {
			return;
		}

		float babyHeight = getBaseDimensions(self.getPose()).height();
		float adultHeight = self.getType().getDimensions().height();

		// Mobs whose babies are already full size have nothing to grow into.
		if (babyHeight <= 0.0F || adultHeight <= babyHeight) {
			return;
		}

		info.setReturnValue(info.getReturnValueF() * (adultHeight / babyHeight));
	}
}
