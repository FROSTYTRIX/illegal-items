package net.frostytrix.illegalitems;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.minecraft.client.render.RenderLayer;

/**
 * Client-only entrypoint. This lives in the main source set, so nothing here may be touched from
 * common code — it would crash a dedicated server.
 */
public class IllegalItemsClient implements ClientModInitializer {

	/**
	 * Vanilla's water texture is greyscale and takes its colour from the biome, so the still water
	 * has to be tinted or it renders grey. This is the default water colour. Lava needs no tint —
	 * its texture is already orange.
	 *
	 * <p>Item tinting moved into the item model definition in 1.21.4, so the item side is the
	 * {@code minecraft:constant} tint in {@code assets/illegal_items/items/water.json}.
	 */
	private static final int WATER_COLOR = 0x3F76E4;

	@Override
	public void onInitializeClient() {
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WATER, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.STILL_WATER, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.FLOWING_WATER, RenderLayer.getTranslucent());

		// Reuse the vanilla fluid sprites so these are indistinguishable from real water and lava.
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_WATER, ModFluids.FLOWING_WATER,
				new SimpleFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL,
						SimpleFluidRenderHandler.WATER_FLOWING,
						SimpleFluidRenderHandler.WATER_OVERLAY,
						WATER_COLOR));

		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_LAVA, ModFluids.FLOWING_LAVA,
				new SimpleFluidRenderHandler(SimpleFluidRenderHandler.LAVA_STILL,
						SimpleFluidRenderHandler.LAVA_FLOWING));
	}
}
