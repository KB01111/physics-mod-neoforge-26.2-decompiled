package net.diebuddies.mixins.voxy;

import net.diebuddies.compat.Voxy;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"me.cortex.voxy.client.core.model.bakery.SoftwareModelTextureBakery"}
)
public class MixinSoftwareModelTextureBakery {
   @Inject(
      at = {@At("HEAD")},
      method = {"renderToOutput"},
      cancellable = true
   )
   private void physicsmod$startRender(BlockState state, long outputBuffer, CallbackInfoReturnable<Integer> info) {
      Voxy.enableVoxyBlock();
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"renderToOutput"},
      cancellable = true
   )
   private void physicsmod$endRender(BlockState state, long outputBuffer, CallbackInfoReturnable<Integer> info) {
      Voxy.disableVoxyBlock();
   }
}
