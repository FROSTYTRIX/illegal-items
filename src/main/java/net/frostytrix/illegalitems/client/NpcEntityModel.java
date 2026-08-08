package net.frostytrix.illegalitems.client;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

/**
 * {@code geometry.npc}, converted from Bedrock.
 *
 * <p>Same Y flip as the agent: a bone pivot becomes {@code 24 - pivotY} and a cube's offset within
 * its bone becomes {@code pivotY - originY - sizeY}. Bedrock's {@code inflate} maps onto
 * {@link Dilation}, which is what gives the hat, jacket and trousers their slight puff.
 *
 * <p>Villager-shaped, but with the player's layered look — and note the arms are a single bone
 * holding three cubes, not one bone per arm.
 */
public class NpcEntityModel extends EntityModel<NpcEntityRenderState> {
	private static final int TEXTURE_WIDTH = 64;
	private static final int TEXTURE_HEIGHT = 64;

	/** -42.97 degrees from animation.npc.general, which is vanilla's villager arm pitch exactly. */
	private static final float ARMS_PITCH = -0.75F;

	private final ModelPart head;

	public NpcEntityModel(ModelPart root) {
		super(root);
		this.head = root.getChild("body").getChild("head");
	}

	/** It never walks, so the only thing that moves is the head following you. */
	@Override
	public void setAngles(NpcEntityRenderState state) {
		super.setAngles(state);
		head.yaw = state.relativeHeadYaw * MathHelper.RADIANS_PER_DEGREE;
		head.pitch = state.pitch * MathHelper.RADIANS_PER_DEGREE;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData data = new ModelData();
		ModelPartData root = data.getRoot();

		ModelPartData body = root.addChild("body",
				ModelPartBuilder.create()
						.uv(16, 20).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
						.uv(0, 38).cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.5F)),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData head = body.addChild("head",
				ModelPartBuilder.create()
						.uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		head.addChild("hat",
				ModelPartBuilder.create()
						.uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		head.addChild("nose",
				ModelPartBuilder.create()
						.uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F),
				ModelTransform.origin(0.0F, -2.0F, 0.0F));

		// animation.npc.general is a constant looping pose, not motion: it folds the arms. Baked in
		// here rather than applied per frame. Its -42.97 degrees is exactly the -0.75 radians vanilla
		// uses on a villager, and its [0, -1, -1] offset flips to +1 on Y crossing into Java space.
		body.addChild("arms",
				ModelPartBuilder.create()
						.uv(40, 38).cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F)
						.uv(44, 22).cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F)
						.uv(44, 46).cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F),
				ModelTransform.of(0.0F, 3.0F, -1.0F, ARMS_PITCH, 0.0F, 0.0F));

		body.addChild("jacket",
				ModelPartBuilder.create()
						.uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.origin(0.0F, 0.0F, 0.0F));

		ModelPartData leg0 = root.addChild("leg0",
				ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				ModelTransform.origin(-2.0F, 12.0F, 0.0F));

		leg0.addChild("right_pants",
				ModelPartBuilder.create()
						.uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.origin(0.1F, 0.0F, 0.0F));

		ModelPartData leg1 = root.addChild("leg1",
				ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
				ModelTransform.origin(2.0F, 12.0F, 0.0F));

		leg1.addChild("left_pants",
				ModelPartBuilder.create()
						.uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.origin(-0.1F, 0.0F, 0.0F));

		return TexturedModelData.of(data, TEXTURE_WIDTH, TEXTURE_HEIGHT);
	}
}
