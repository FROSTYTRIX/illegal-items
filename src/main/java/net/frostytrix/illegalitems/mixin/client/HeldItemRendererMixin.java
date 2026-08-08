package net.frostytrix.illegalitems.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.frostytrix.illegalitems.registry.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

/**
 * Hides the illegal map while leaving both hands on screen.
 *
 * <p>{@code renderMapInBothHands} draws the arms and then calls {@code renderFirstPersonMap} for the
 * map itself — the parchment background and its contents. Cancelling only the latter keeps the arms
 * exactly as vanilla drew them and removes the map from between them.
 *
 * <p>This covers the one-handed case too, since that path calls the same method.
 */
@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

	@Inject(method = "renderFirstPersonMap(Lnet/minecraft/client/util/math/MatrixStack;"
			+ "Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/item/ItemStack;)V",
			at = @At("HEAD"), cancellable = true)
	private void illegalItems$hideMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
			int light, ItemStack stack, CallbackInfo info) {
		if (stack.isOf(ModItems.MAP)) {
			info.cancel();
		}
	}
}
