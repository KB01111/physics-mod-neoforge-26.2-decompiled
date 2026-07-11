package net.diebuddies.mixins.emf;

import net.diebuddies.physics.PhysicsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import traben.entity_model_features.models.animation.EMFAnimationEntityContext;

@Pseudo
@Mixin({EMFAnimationEntityContext.class})
public class MixinAnimationEntityContext {
   @Inject(
      at = {@At("HEAD")},
      method = {"isEntityForcedToVanillaModel"},
      cancellable = true
   )
   private static void physicsmod$renderVanillaModel(CallbackInfoReturnable<Boolean> info) {
      if (PhysicsMod.disabledEMFModel) {
         info.setReturnValue(true);
      }
   }
}
