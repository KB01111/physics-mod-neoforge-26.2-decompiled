package net.diebuddies.mixins.iris;

import net.diebuddies.compat.Iris;
import net.irisshaders.iris.gl.IrisRenderSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({IrisRenderSystem.class})
public class MixinIrisRenderSystem {
   @Inject(
      at = {@At("RETURN")},
      method = {"bindBufferBase"},
      cancellable = true
   )
   private static void physicsmod$grabSSBOBindingForVolumetricSmoke(int target, Integer index, int buffer, CallbackInfo info) {
      if (target == 37074) {
         if (index == 0) {
            Iris.ssboBindingIndex0 = buffer;
         } else if (index == 1) {
            Iris.ssboBindingIndex1 = buffer;
         }
      }
   }
}
