package net.frostytrix.illegalitems.client;

import net.frostytrix.illegalitems.entity.HumanEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

/** Draws the Indev human with the stock Steve skin, which already ships with the game. */
public class HumanEntityRenderer
		extends BipedEntityRenderer<HumanEntity, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {

	public static final EntityModelLayer MODEL_LAYER =
			new EntityModelLayer(Identifier.of("illegal_items", "human"), "main");

	private static final Identifier TEXTURE =
			Identifier.ofVanilla("textures/entity/player/wide/steve.png");

	public HumanEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new BipedEntityModel<>(context.getPart(MODEL_LAYER)), 0.5F);
	}

	@Override
	public BipedEntityRenderState createRenderState() {
		return new BipedEntityRenderState();
	}

	@Override
	public Identifier getTexture(BipedEntityRenderState state) {
		return TEXTURE;
	}
}
