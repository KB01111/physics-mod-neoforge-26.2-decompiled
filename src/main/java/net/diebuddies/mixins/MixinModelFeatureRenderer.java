package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.render.util.PhysicsSubmitExtension;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelFeatureRenderer.class})
public class MixinModelFeatureRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"prepareModel"}
   )
   private <S> void physicsmod$applyRenderLayer(Submit<S> submit, CallbackInfo info) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify) {
         mod.blockifyFeature = ((PhysicsSubmitExtension)(Object)submit).physicsmod$getRenderLayer();
      }
   }
}
