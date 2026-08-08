package net.frostytrix.illegalitems.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.frostytrix.illegalitems.client.DyedBodyState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.WolfCollarFeatureRenderer;
import net.minecraft.client.render.entity.state.WolfEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Hides the collar on a dog whose coat took the dye — see {@link WolfEntityRendererMixin}. Wolves
 * that were dyed normally keep their collar, so this only fires for the illegal ones.
 */
@Mixin(WolfCollarFeatureRenderer.class)
public class WolfCollarFeatureRendererMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void illegalItems$hideCollar(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, WolfEntityRenderState state, float limbAngle, float limbDistance, CallbackInfo info) {
		if (((DyedBodyState) state).illegalItems$isDyedBody()) {
			info.cancel();
		}
	}
}
