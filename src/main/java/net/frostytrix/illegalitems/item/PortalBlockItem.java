package net.frostytrix.illegalitems.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.Direction;

/**
 * Places a portal block turned to face the player.
 *
 * <p>Nothing in vanilla ever places a nether portal from an item, so {@code NetherPortalBlock} has
 * no {@code getPlacementState} of its own and a plain {@link BlockItem} would always drop the
 * default {@code axis=x} state no matter which way you were looking.
 *
 * <p>{@code axis=x} maps to the north/south-facing model, so the portal has to take the axis
 * perpendicular to the player's facing for its face to point at them.
 */
public class PortalBlockItem extends BlockItem {
	public PortalBlockItem(Block block, Settings settings) {
		super(block, settings);
	}

	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = super.getPlacementState(context);

		if (state == null || !state.contains(NetherPortalBlock.AXIS)) {
			return state;
		}

		Direction.Axis facing = context.getHorizontalPlayerFacing().rotateYClockwise().getAxis();
		return state.with(NetherPortalBlock.AXIS, facing);
	}
}
