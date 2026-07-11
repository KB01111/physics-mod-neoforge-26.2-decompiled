package net.diebuddies.mixins;

import com.mojang.blaze3d.vulkan.glsl.IntermediaryShaderModule;
import com.mojang.blaze3d.vulkan.glsl.SpvSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({IntermediaryShaderModule.class})
public abstract class MixinIntermediaryShaderModule {
   @Redirect(
      method = {"rebind"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vulkan/glsl/SpvSampler;dimensions()I",
         ordinal = 0
      )
   )
   private int physicsmod$allow3dOnSampledImageFirstCheck(SpvSampler sampler) {
      return physicsmod$map3dTo2dForSampledImages(sampler.dimensions());
   }

   @Redirect(
      method = {"rebind"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vulkan/glsl/SpvSampler;dimensions()I",
         ordinal = 1
      )
   )
   private int physicsmod$allow3dOnSampledImageSecondCheck(SpvSampler sampler) {
      return physicsmod$map3dTo2dForSampledImages(sampler.dimensions());
   }

   @Unique
   private static int physicsmod$map3dTo2dForSampledImages(int dim) {
      return dim == 2 ? 1 : dim;
   }
}
