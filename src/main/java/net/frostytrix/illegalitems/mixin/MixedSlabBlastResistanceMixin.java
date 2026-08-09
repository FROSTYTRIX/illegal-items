package net.frostytrix.illegalitems.mixin;

import java.util.Optional;

import net.frostytrix.illegalitems.block.MixedSlabBlock;
import net.frostytrix.illegalitems.block.entity.MixedSlabBlockEntity;
import net.frostytrix.illegalitems.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** The same averaging for explosions, which is the one blast-resistance lookup that knows the position. */
@Mixin(ExplosionBehavior.class)
public class MixedSlabBlastResistanceMixin {
	@Inject(method = "getBlastResistance", at = @At("HEAD"), cancellable = true)
	private void illegalItems$meanOfBothHalves(Explosion explosion, BlockView world, BlockPos pos,
			BlockState state, FluidState fluid, CallbackInfoReturnable<Optional<Float>> info) {
		if (state.isOf(ModBlocks.MIXED_SLAB)
				&& world.getBlockEntity(pos) instanceof MixedSlabBlockEntity slab) {
			info.setReturnValue(Optional.of(
					MixedSlabBlock.mean(slab, half -> half.getBlock().getBlastResistance())));
		}
	}
}
