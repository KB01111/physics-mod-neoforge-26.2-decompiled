package net.diebuddies.mixins.betterblockentities;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"betterblockentities.client.gui.config.wrapper.GenericConfigWrapper"}
)
public class MixinConfigUpdater {
   @Inject(
      at = {@At("RETURN")},
      method = {"optimizeBanner"},
      cancellable = true
   )
   private void physicsmod$fixBanners(CallbackInfoReturnable<Boolean> info) {
      info.setReturnValue(false);
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"optimizeSign"},
      cancellable = true
   )
   private void physicsmod$fixSigns(CallbackInfoReturnable<Boolean> info) {
      info.setReturnValue(false);
   }
}
