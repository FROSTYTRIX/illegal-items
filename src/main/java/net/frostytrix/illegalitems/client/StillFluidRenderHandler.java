package net.frostytrix.illegalitems.client;

import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

/**
 * Draws the still fluids with the vanilla water and lava sprites.
 *
 * <p>{@code SimpleFluidRenderHandler} would be the obvious choice, but it caches its sprites in
 * {@code reloadTextures}, which is driven off a mixin on vanilla's {@code FluidRenderer} and never
 * fired for these fluids — leaving the sprite array full of nulls and the fluid invisible. Looking
 * the sprites up from the block atlas on demand sidesteps that entirely; it is a map lookup, and
 * the fluid renderer only calls this once per fluid per chunk section rebuild.
 */
public class StillFluidRenderHandler implements FluidRenderHandler {
	private final Identifier stillTexture;
	private final Identifier flowingTexture;
	@Nullable
	private final Identifier overlayTexture;
	private final int tint;

	public StillFluidRenderHandler(Identifier stillTexture, Identifier flowingTexture,
			@Nullable Identifier overlayTexture, int tint) {
		this.stillTexture = stillTexture;
		this.flowingTexture = flowingTexture;
		this.overlayTexture = overlayTexture;
		this.tint = tint;
	}

	@Override
	public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		Function<Identifier, Sprite> atlas =
				MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);

		if (overlayTexture == null) {
			return new Sprite[] { atlas.apply(stillTexture), atlas.apply(flowingTexture) };
		}

		return new Sprite[] {
				atlas.apply(stillTexture),
				atlas.apply(flowingTexture),
				atlas.apply(overlayTexture)
		};
	}

	@Override
	public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return tint;
	}
}
