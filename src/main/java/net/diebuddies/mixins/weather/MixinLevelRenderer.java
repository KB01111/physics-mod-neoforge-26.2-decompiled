package net.diebuddies.mixins.weather;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelRenderer.class})
public class MixinLevelRenderer {
   @Inject(
      at = {@At("RETURN")},
      method = {"render"}
   )
   private void physicsmod$invalidateWeatherLight(
      GraphicsResourceAllocator resourceAllocator,
      DeltaTracker deltaTracker,
      boolean renderOutline,
      CameraRenderState cameraState,
      Matrix4fc modelViewMatrix,
      GpuBufferSlice terrainFog,
      Vector4f fogColor,
      boolean shouldRenderSky,
      CallbackInfo info
   ) {
      WeatherEffects.invalidateLight = false;
   }
}
