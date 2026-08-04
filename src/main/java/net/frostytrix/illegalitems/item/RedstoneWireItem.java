package net.frostytrix.illegalitems.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

/**
 * Redstone dust that places already carrying a given power level, one item per level 0-15.
 *
 * <p>Vanilla redstone recalculates power in {@code RedstoneWireBlock.onBlockAdded}, so a wire placed
 * with nothing feeding it drops straight back to 0 — the level holds only while something actually
 * powers it. Making it stick regardless would mean disabling wire updates globally.
 */
public class RedstoneWireItem extends BlockItem {
	private final int power;

	public RedstoneWireItem(int power, Settings settings) {
		super(Blocks.REDSTONE_WIRE, settings);
		this.power = power;
	}

	public int power() {
		return power;
	}

	@Override
	protected BlockState getPlacementState(ItemPlacementContext context) {
		BlockState state = super.getPlacementState(context);

		if (state == null || !state.contains(RedstoneWireBlock.POWER)) {
			return state;
		}

		return state.with(RedstoneWireBlock.POWER, power);
	}
}
