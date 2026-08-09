package net.frostytrix.illegalitems.mixin;

import net.frostytrix.illegalitems.block.MixedSlabBlock;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A mixed slab takes as long to break as the average of its two halves.
 *
 * <p>It has to be done here rather than on the block: hardness is a settings field read straight off
 * the state, and {@code AbstractBlock.getHardness()} takes no position, so a block cannot vary it.
 * This method is the only one on the way through that knows where it is being asked about, which is
 * what makes reading the block entity possible.
 *
 * <p>No recursion risk: the halves are ordinary slabs, so they fail the check and return their own
 * field as usual.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixedSlabHardnessMixin {
	@Inject(method = "getHardness", at = @At("HEAD"), cancellable = true)
	private void illegalItems$meanOfBothHalves(BlockView world, BlockPos pos,
			CallbackInfoReturnable<Float> info) {
		BlockState state = (BlockState) (Object) this;

		if (state.isOf(ModBlocks.MIXED_SLAB)
				&& world.getBlockEntity(pos) instanceof MixedSlabBlockEntity slab) {
			info.setReturnValue(MixedSlabBlock.mean(slab, half -> half.getHardness(world, pos)));
		}
	}
}
