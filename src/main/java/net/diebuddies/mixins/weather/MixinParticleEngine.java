package net.diebuddies.mixins.weather;

import net.diebuddies.minecraft.weather.WeatherEffects;
import net.minecraft.client.particle.ParticleEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ParticleEngine.class})
public class MixinParticleEngine {
   @Inject(
      at = {@At("HEAD")},
      method = {"tick"}
   )
   public void tick(CallbackInfo info) {
      WeatherEffects.aliveParticles = 0;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   public void invalidateLight(CallbackInfo info) {
      WeatherEffects.invalidateLight = false;
   }
}
