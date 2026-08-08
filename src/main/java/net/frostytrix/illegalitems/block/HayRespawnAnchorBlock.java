package net.frostytrix.illegalitems.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * A respawn anchor made of hay. It charges from glowstone and lights up exactly like the real
 * thing, and that is where the resemblance stops — it will not set your spawn, and it will not
 * blow up in your face either.
 */
public class HayRespawnAnchorBlock extends Block {
	public static final IntProperty CHARGES = IntProperty.of("charges", 0, 4);
	public static final int MAX_CHARGES = 4;

	public HayRespawnAnchorBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(CHARGES, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(CHARGES);
	}

	/** Light rises with the charge, the same curve the vanilla anchor uses. */
	public static int luminance(BlockState state) {
		return state.get(CHARGES) * 15 / MAX_CHARGES;
	}

	@Override
	protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos,
			PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!stack.isOf(Items.GLOWSTONE) || state.get(CHARGES) >= MAX_CHARGES) {
			return ActionResult.PASS;
		}

		if (!world.isClient()) {
			world.setBlockState(pos, state.with(CHARGES, state.get(CHARGES) + 1));
			stack.decrementUnlessCreative(1, player);
			world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS,
					1.0F, 1.0F);
		}

		return ActionResult.SUCCESS;
	}
}
