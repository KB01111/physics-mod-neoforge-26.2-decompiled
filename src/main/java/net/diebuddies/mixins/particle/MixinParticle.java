package net.diebuddies.mixins.particle;

import net.diebuddies.physics.settings.animation.ParticleExtension;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Particle.class})
public class MixinParticle implements ParticleExtension {
   @Shadow
   protected boolean hasPhysics;
   @Unique
   private boolean fakeLight;

   @Inject(
      at = {@At("HEAD")},
      method = {"getLightCoords"},
      cancellable = true
   )
   protected void getLightCoords(float f, CallbackInfoReturnable<Integer> info) {
      if (this.fakeLight) {
         info.setReturnValue(0);
      }
   }

   @Override
   public void setPhysics(boolean physics) {
      this.hasPhysics = physics;
   }

   @Override
   public void setFakeLight(boolean fakeLight) {
      this.fakeLight = fakeLight;
   }
}
