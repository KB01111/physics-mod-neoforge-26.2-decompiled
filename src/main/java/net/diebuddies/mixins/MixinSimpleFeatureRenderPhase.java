package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.render.util.PhysicsSubmitExtension;
import net.minecraft.client.renderer.feature.phase.SimpleFeatureRenderPhase;
import net.minecraft.client.renderer.feature.submit.SubmitNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({SimpleFeatureRenderPhase.class})
public class MixinSimpleFeatureRenderPhase {
   @Inject(
      at = {@At("HEAD")},
      method = {"submit"}
   )
   private void physicsmod$submit(SubmitNode submit, CallbackInfo info) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify && submit instanceof PhysicsSubmitExtension extension) {
         extension.physicsmod$setRenderLayer(mod.blockifyFeature);
      }
   }
}
