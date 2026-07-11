package net.diebuddies.mixins.immediatelyfast;

import net.minecraft.world.level.block.entity.SignText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {SignText.class},
   priority = 1100
)
public class MixinSignText {
   @Inject(
      at = {@At("RETURN")},
      method = {"immediatelyFast$shouldCache"},
      cancellable = true
   )
   private void physicsmod$fixSignTextAfterReload(CallbackInfoReturnable<Boolean> info) {
      info.setReturnValue(false);
   }
}
