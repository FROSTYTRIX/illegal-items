package net.frostytrix.illegalitems.client;

import net.frostytrix.illegalitems.entity.AgentEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.Identifier;

/** Draws the agent with the supplied 32x32 sheet. */
public class AgentEntityRenderer
		extends MobEntityRenderer<AgentEntity, LivingEntityRenderState, AgentEntityModel> {

	public static final EntityModelLayer MODEL_LAYER =
			new EntityModelLayer(Identifier.of("illegal_items", "agent"), "main");

	private static final Identifier TEXTURE =
			Identifier.of("illegal_items", "textures/entity/agent.png");

	public AgentEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new AgentEntityModel(context.getPart(MODEL_LAYER)), 0.3F);
	}

	@Override
	public LivingEntityRenderState createRenderState() {
		return new LivingEntityRenderState();
	}

	@Override
	public Identifier getTexture(LivingEntityRenderState state) {
		return TEXTURE;
	}
}
