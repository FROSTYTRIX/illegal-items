package net.frostytrix.illegalitems.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.frostytrix.illegalitems.client.DyedBodyState;
import net.frostytrix.illegalitems.registry.ModAttachments;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.ColorHelper;

/**
 * Dyes the dog rather than its collar, for wolves whose coat has taken the dye.
 *
 * <p>{@code getMixColor} is the tint the renderer multiplies the whole wolf texture by — vanilla
 * only uses it to darken a wolf that is shaking off water. Returning the collar dye there colours
 * the entire animal, and {@link WolfCollarFeatureRendererMixin} hides the collar to match.
 */
@Mixin(WolfEntityRenderer.class)
public class WolfEntityRendererMixin {

	/** The renderer only sees the render state, so the flag is copied over from the entity here. */
	@Inject(method = "updateRenderState(Lnet/minecraft/entity/passive/WolfEntity;"
			+ "Lnet/minecraft/client/render/entity/state/WolfEntityRenderState;F)V", at = @At("TAIL"))
	private void illegalItems$copyDyedBody(WolfEntity wolf, WolfEntityRenderState state, float tickDelta,
			CallbackInfo info) {
		((DyedBodyState) state).illegalItems$setDyedBody(
				wolf.getAttachedOrElse(ModAttachments.DYED_BODY, false));
	}

	@Inject(method = "getMixColor(Lnet/minecraft/client/render/entity/state/WolfEntityRenderState;)I",
			at = @At("RETURN"), cancellable = true)
	private void illegalItems$dyeTheDog(WolfEntityRenderState state, CallbackInfoReturnable<Integer> info) {
		if (state.collarColor == null || !((DyedBodyState) state).illegalItems$isDyedBody()) {
			return;
		}

		int rgb = state.collarColor.getEntityColor();
		// Keep vanilla's wet-fur darkening so a soaked dog still dims.
		float wet = state.furWetBrightnessMultiplier;

		info.setReturnValue(ColorHelper.fromFloats(1.0F,
				((rgb >> 16) & 0xFF) / 255.0F * wet,
				((rgb >> 8) & 0xFF) / 255.0F * wet,
				(rgb & 0xFF) / 255.0F * wet));
	}
}
