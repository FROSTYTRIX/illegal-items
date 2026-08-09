package net.frostytrix.illegalitems.client;

import java.util.List;
import java.util.function.Predicate;

import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.SlabType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.model.ResolvableModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

/**
 * Draws a mixed slab as the bottom of one slab and the top of another.
 *
 * <p>No geometry is built here. Vanilla already ships a {@code type=bottom} and a {@code type=top}
 * model for every slab there is, so this asks each of them to emit its own quads into the same mesh
 * — which means modded slabs work too, and any resource pack that retextures a slab is followed
 * without this knowing about it.
 *
 * <p>{@link BlockStateModel#addParts} is handed no world and no position, so it cannot see a block
 * entity at all. Fabric's {@code emitQuads} is the one that can, and it runs while the chunk mesh is
 * being built rather than every frame, so the result keeps normal lighting and ambient occlusion and
 * costs nothing to render.
 */
public class MixedSlabModel implements BlockStateModel {
	/** Falls back to stone rather than crashing if the block entity is missing or not yet synced. */
	private static final MixedSlabBlockEntity.Halves FALLBACK = new MixedSlabBlockEntity.Halves(
			Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.BOTTOM),
			Blocks.STONE_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP));

	@Override
	public void emitQuads(QuadEmitter emitter, BlockRenderView world, BlockPos pos, BlockState state,
			Random random, Predicate<Direction> cullTest) {
		MixedSlabBlockEntity.Halves halves = halves(world, pos);
		emitHalf(emitter, world, pos, halves.bottom(), random, cullTest);
		emitHalf(emitter, world, pos, halves.top(), random, cullTest);
	}

	private static void emitHalf(QuadEmitter emitter, BlockRenderView world, BlockPos pos,
			BlockState half, Random random, Predicate<Direction> cullTest) {
		model(half).emitQuads(emitter, world, pos, half, random, cullTest);
	}

	/**
	 * What the chunk baker caches meshes against. Returning the pair is what stops two mixed slabs
	 * with different halves from being handed each other's geometry.
	 */
	@Override
	public Object createGeometryKey(BlockRenderView world, BlockPos pos, BlockState state, Random random) {
		return halves(world, pos);
	}

	@Override
	public Sprite particleSprite(BlockRenderView world, BlockPos pos, BlockState state) {
		return model(halves(world, pos).bottom()).particleSprite();
	}

	@Override
	public Sprite particleSprite() {
		return model(FALLBACK.bottom()).particleSprite();
	}

	/** Only reached outside a world, where there is nothing to read the halves from. */
	@Override
	public void addParts(Random random, List<BlockModelPart> parts) {
	}

	private static MixedSlabBlockEntity.Halves halves(BlockRenderView world, BlockPos pos) {
		return ((FabricBlockView) world).getBlockEntityRenderData(pos) instanceof MixedSlabBlockEntity.Halves halves
				? halves
				: FALLBACK;
	}

	private static BlockStateModel model(BlockState state) {
		return MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
	}

	/** Bound to the block in {@code IllegalItemsClient}; there is no blockstate JSON behind it. */
	public record Unbaked() implements BlockStateModel.Unbaked {
		private static final MixedSlabModel BAKED = new MixedSlabModel();

		@Override
		public BlockStateModel bake(Baker baker) {
			return BAKED;
		}

		@Override
		public void resolve(ResolvableModel.Resolver resolver) {
		}
	}
}
