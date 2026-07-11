package net.diebuddies.mixins.snow;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"net.optifine.util.BlockUtils"}
)
public class MixinBlockUtils {
   @Inject(
      at = {@At("RETURN")},
      method = {"shouldSideBeRendered"},
      cancellable = true
   )
   private static void shouldRenderFace(
      BlockState blockStateIn,
      BlockGetter blockReaderIn,
      BlockPos blockPosIn,
      Direction facingIn,
      @Coerce Object renderEnv,
      CallbackInfoReturnable<Boolean> info
   ) {
   }
}
