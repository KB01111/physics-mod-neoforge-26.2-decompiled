package net.diebuddies.mixins;

import com.mojang.blaze3d.vulkan.glsl.GlslCompiler;
import com.mojang.blaze3d.vulkan.glsl.SpvSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({GlslCompiler.class})
public abstract class MixinGlslCompiler {
   @Redirect(
      method = {"addToBindGroup"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vulkan/glsl/SpvSampler;dimensions()I",
         ordinal = 1
      )
   )
   private static int physicsmod$allow3dOnFirstSampledCheck(SpvSampler sampler) {
      return physicsmod$mapSampledDim(sampler.dimensions());
   }

   @Redirect(
      method = {"addToBindGroup"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vulkan/glsl/SpvSampler;dimensions()I",
         ordinal = 2
      )
   )
   private static int physicsmod$allow3dOnSecondSampledCheck(SpvSampler sampler) {
      return physicsmod$mapSampledDim(sampler.dimensions());
   }

   @Unique
   private static int physicsmod$mapSampledDim(int dim) {
      return dim == 2 ? 1 : dim;
   }
}
