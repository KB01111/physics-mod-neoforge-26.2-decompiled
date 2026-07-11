package net.diebuddies.mixins.item;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer.Submit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ItemFeatureRenderer.class},
   priority = 1900
)
public class MixinItemRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"prepareSubmit"}
   )
   private void physicsmod$grabItemBreakTransformation(Submit submit, boolean foil, CallbackInfo info) {
      if (PhysicsMod.itemBreakTransformation != null) {
         PhysicsMod.itemBreakTransformation.set(submit.pose().pose());
      }
   }
}
