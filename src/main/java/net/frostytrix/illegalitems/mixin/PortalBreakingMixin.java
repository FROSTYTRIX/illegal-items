package net.frostytrix.illegalitems.mixin;

import net.frostytrix.illegalitems.util.PortalHarvest;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets a sufficiently illegal pickaxe break the portal blocks.
 *
 * <p>Vanilla returns a delta of zero for anything with a negative hardness, which is what makes the
 * portals unbreakable — no progress accrues, so the block never gives. Substituting a real delta is
 * enough on its own: both the client's cracking animation and the server's own mining check run
 * through this same method, so they stay in agreement without touching either.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public class PortalBreakingMixin {
	@Inject(method = "calcBlockBreakingDelta", at = @At("HEAD"), cancellable = true)
	private void illegalItems$harvestPortals(PlayerEntity player, BlockView world, BlockPos pos,
			CallbackInfoReturnable<Float> info) {
		BlockState state = (BlockState) (Object) this;

		if (PortalHarvest.canHarvest(state, player)) {
			info.setReturnValue(PortalHarvest.breakingDelta(state, player));
		}
	}
}
