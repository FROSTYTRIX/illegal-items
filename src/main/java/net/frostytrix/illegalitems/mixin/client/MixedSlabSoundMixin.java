package net.frostytrix.illegalitems.mixin.client;

import net.frostytrix.illegalitems.client.MixedSlabSounds;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A mixed slab sounds like whichever half you are pointing at.
 *
 * <p>Breaking and hitting sounds are both chosen on the client from the block state, so this is the
 * one place that can answer for a block made of two materials. Falling back to the block's own group
 * when nothing is being looked at keeps footsteps and everything else behaving normally.
 *
 * <p>No recursion: the half is an ordinary slab and fails the check straight away.
 */
@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixedSlabSoundMixin {
	@Inject(method = "getSoundGroup", at = @At("HEAD"), cancellable = true)
	private void illegalItems$soundOfTheHalfInFront(CallbackInfoReturnable<BlockSoundGroup> info) {
		if (!((BlockState) (Object) this).isOf(ModBlocks.MIXED_SLAB)) {
			return;
		}

		BlockState half = MixedSlabSounds.lookedAtHalf();

		if (half != null) {
			info.setReturnValue(half.getSoundGroup());
		}
	}
}
