package net.frostytrix.illegalitems.client;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.util.math.MathHelper;

/**
 * The real {@code geometry.agent}, converted from Bedrock.
 *
 * <p>Bedrock measures Y upwards from the feet while Java model space runs downwards, so every
 * coordinate is flipped on the way across: a bone pivot becomes {@code 24 - pivotY}, and a cube's
 * offset within its bone becomes {@code pivotY - originY - sizeY}. X and Z carry over unchanged as
 * offsets from the pivot. The head, arms and legs are children of the body, as in the original.
 */
public class AgentEntityModel extends EntityModel<LivingEntityRenderState> {
	private static final int TEXTURE_WIDTH = 32;
	private static final int TEXTURE_HEIGHT = 32;

	/**
	 * The hover from {@code animation.agent.move}: {@code (sin(life_time * 114.6) + 1) * 1.12}, which
	 * lifts the body between 0 and 2.24 pixels on a roughly three second cycle. Bedrock's sine takes
	 * degrees and its Y points up, so the value is converted and then negated for Java's downward Y.
	 *
	 * <p>The arm and leg swings in that same animation are driven by distance moved, and an agent
	 * never moves on its own, so they stay at rest. {@code swing_arms} and {@code shrug} are states
	 * the animation controller only enters on command, and there is nothing here to command it yet.
	 */
	private static final float HOVER_DEGREES_PER_SECOND = 114.6F;
	private static final float HOVER_AMPLITUDE = 1.12F;

	private final ModelPart body;
	private float baseBodyOriginY;

	public AgentEntityModel(ModelPart root) {
		super(root);
		this.body = root.getChild("body");
		this.baseBodyOriginY = body.originY;
	}

	@Override
	public void setAngles(LivingEntityRenderState state) {
		super.setAngles(state);

		float seconds = state.age / 20.0F;
		float hover = (MathHelper.sin(seconds * HOVER_DEGREES_PER_SECOND * MathHelper.RADIANS_PER_DEGREE)
				+ 1.0F) * HOVER_AMPLITUDE;
		body.originY = baseBodyOriginY - hover;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData data = new ModelData();
		ModelPartData root = data.getRoot();

		ModelPartData body = root.addChild("body",
				ModelPartBuilder.create()
						.uv(0, 12).cuboid(-4.0F, 15.0F, -2.0F, 8.0F, 4.0F, 4.0F)
						.uv(0, 20).cuboid(-3.0F, 19.0F, -2.0F, 6.0F, 2.0F, 4.0F),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		body.addChild("head",
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(-3.0F, -7.0F, -2.2F, 6.0F, 7.0F, 5.0F)
						.uv(17, 1).cuboid(-1.0F, -3.0F, -3.2F, 2.0F, 3.0F, 1.0F),
				ModelTransform.origin(0.0F, 15.0F, -0.8F));

		body.addChild("left_arm",
				ModelPartBuilder.create().uv(24, 11).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F),
				ModelTransform.origin(5.0F, 15.0F, 0.0F));

		body.addChild("right_arm",
				ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 9.0F, 2.0F),
				ModelTransform.origin(-5.0F, 15.0F, 0.0F));

		body.addChild("left_leg",
				ModelPartBuilder.create().uv(8, 26).cuboid(-0.9F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F),
				ModelTransform.origin(1.9F, 21.0F, 0.0F));

		body.addChild("right_leg",
				ModelPartBuilder.create().uv(0, 26).cuboid(-1.1F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F),
				ModelTransform.origin(-1.9F, 21.0F, 0.0F));

		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
}
