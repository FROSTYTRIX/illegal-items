package net.frostytrix.illegalitems.client;

import net.frostytrix.illegalitems.entity.NpcEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

/** Draws an NPC with whichever of the 32 skins it has been set to. */
public class NpcEntityRenderer
		extends MobEntityRenderer<NpcEntity, NpcEntityRenderState, NpcEntityModel> {

	public static final EntityModelLayer MODEL_LAYER =
			new EntityModelLayer(Identifier.of("illegal_items", "npc"), "main");

	/** Resolved once per skin rather than rebuilt every frame. */
	private static final Identifier[] TEXTURES = new Identifier[NpcEntity.SKINS.length];

	static {
		for (int i = 0; i < NpcEntity.SKINS.length; i++) {
			TEXTURES[i] = Identifier.of("illegal_items", "textures/entity/npc/" + NpcEntity.SKINS[i] + ".png");
		}
	}

	public NpcEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new NpcEntityModel(context.getPart(MODEL_LAYER)), 0.5F);
	}

	@Override
	public NpcEntityRenderState createRenderState() {
		return new NpcEntityRenderState();
	}

	@Override
	public void updateRenderState(NpcEntity entity, NpcEntityRenderState state, float tickDelta) {
		super.updateRenderState(entity, state, tickDelta);
		state.skin = entity.getSkin();
	}

	@Override
	public Identifier getTexture(NpcEntityRenderState state) {
		return TEXTURES[Math.floorMod(state.skin, TEXTURES.length)];
	}
}
