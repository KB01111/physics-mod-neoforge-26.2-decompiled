package net.diebuddies.mixins.moreculling;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   targets = {"ca.fxco.moreculling.config.ConfigUpdater"}
)
public class MixinConfigUpdater {
   @Inject(
      at = {@At("RETURN")},
      method = {"updateConfig"}
   )
   private static void physicsmod$fixDynamicBlockCulling(@Coerce Object moreCullingConfig, CallbackInfo info) {
      try {
         moreCullingConfig.getClass().getField("useBlockStateCulling").setBoolean(moreCullingConfig, false);
      } catch (Exception var3) {
      }
   }
}
