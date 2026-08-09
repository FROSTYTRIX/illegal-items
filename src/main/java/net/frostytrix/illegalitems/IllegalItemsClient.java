package net.frostytrix.illegalitems;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.block.BlockState;
import net.frostytrix.illegalitems.client.MixedSlabModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.frostytrix.illegalitems.client.AgentEntityModel;
import net.frostytrix.illegalitems.client.AgentEntityRenderer;
import net.frostytrix.illegalitems.client.HumanEntityRenderer;
import net.frostytrix.illegalitems.client.NpcEntityModel;
import net.frostytrix.illegalitems.client.NpcEntityRenderer;
import net.frostytrix.illegalitems.client.StillFluidRenderHandler;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.frostytrix.illegalitems.registry.ModEntities;
import net.frostytrix.illegalitems.registry.ModFluids;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.BipedEntityModel;

/**
 * Client-only entrypoint. This lives in the main source set, so nothing here may be touched from
 * common code — it would crash a dedicated server.
 */
public class IllegalItemsClient implements ClientModInitializer {

	/**
	 * Vanilla's water texture is greyscale and takes its colour from the biome, so the still water
	 * has to be tinted or it renders grey. This is the default water colour. Lava needs no tint —
	 * its texture is already orange, so it renders at plain white.
	 *
	 * <p>Item tinting moved into the item model definition in 1.21.4, so the item side is the
	 * {@code minecraft:constant} tint in {@code assets/illegal_items/items/water.json}.
	 */
	private static final int WATER_COLOR = 0xFF3F76E4;
	private static final int NO_TINT = 0xFFFFFFFF;

	@Override
	public void onInitializeClient() {
		EntityModelLayerRegistry.registerModelLayer(HumanEntityRenderer.MODEL_LAYER,
				() -> TexturedModelData.of(BipedEntityModel.getModelData(Dilation.NONE, 0.0F), 64, 64));
		EntityRendererRegistry.register(ModEntities.HUMAN, HumanEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(AgentEntityRenderer.MODEL_LAYER,
				AgentEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.AGENT, AgentEntityRenderer::new);

		EntityModelLayerRegistry.registerModelLayer(NpcEntityRenderer.MODEL_LAYER,
				NpcEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(ModEntities.NPC, NpcEntityRenderer::new);

		// The mixed slab has no blockstate JSON; its model is supplied straight to the loader, and
		// reads the two halves back out of the block entity when the chunk mesh is built.
		ModelLoadingPlugin.register(plugin -> plugin.registerBlockStateResolver(ModBlocks.MIXED_SLAB, context -> {
			BlockStateModel.UnbakedGrouped model = new MixedSlabModel.Unbaked().cached();

			for (BlockState state : ModBlocks.MIXED_SLAB.getStateManager().getStates()) {
				context.setModel(state, model);
			}
		}));

		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.WATER, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.STILL_WATER, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putFluid(ModFluids.FLOWING_WATER, RenderLayer.getTranslucent());

		// Reuse the vanilla fluid sprites so these are indistinguishable from real water and lava.
		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_WATER, ModFluids.FLOWING_WATER,
				new StillFluidRenderHandler(SimpleFluidRenderHandler.WATER_STILL,
						SimpleFluidRenderHandler.WATER_FLOWING,
						SimpleFluidRenderHandler.WATER_OVERLAY,
						WATER_COLOR));

		FluidRenderHandlerRegistry.INSTANCE.register(ModFluids.STILL_LAVA, ModFluids.FLOWING_LAVA,
				new StillFluidRenderHandler(SimpleFluidRenderHandler.LAVA_STILL,
						SimpleFluidRenderHandler.LAVA_FLOWING,
						null,
						NO_TINT));
	}
}
