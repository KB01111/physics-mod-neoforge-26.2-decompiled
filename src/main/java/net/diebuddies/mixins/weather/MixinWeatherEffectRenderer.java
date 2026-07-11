package net.diebuddies.mixins.weather;

import net.diebuddies.config.ConfigClient;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WeatherEffectRenderer.class})
public class MixinWeatherEffectRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"render(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/client/renderer/state/level/WeatherRenderState;Lnet/minecraft/client/renderer/state/level/LevelRenderState;)V"},
      cancellable = true
   )
   public void physicsmod$cancelWeatherRendering(Vec3 vec3, WeatherRenderState weatherRenderState, LevelRenderState levelRenderState, CallbackInfo info) {
      if (ConfigClient.weatherParticles) {
         info.cancel();
      }
   }
}
