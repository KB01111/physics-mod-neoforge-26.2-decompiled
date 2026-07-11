package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.DynamicUniforms.Transform;

public interface DynamicUniformsExtension {
   GpuBufferSlice[] physicsmod$writeLightUniforms(LightUniform... var1);

   GpuBufferSlice[] physicsmod$writeBrightnessUniforms(BrightnessUniform... var1);

   GpuBufferSlice[] physicsmod$writeOceanUniforms(OceanUniform... var1);

   GpuBufferSlice[] physicsmod$writeOceanChunkUniforms(OceanChunkUniform... var1);

   GpuBufferSlice[] physicsmod$writeRippleUniforms(RippleUniform... var1);

   GpuBufferSlice[] physicsmod$writeLiquidUniforms(LiquidUniform... var1);

   GpuBufferSlice[] physicsmod$writeGaussianDepthBlurUniforms(GaussianDepthBlurUniform... var1);

   GpuBufferSlice[] physicsmod$writeSmokeUniforms(SmokeUniform... var1);

   DynamicUniformStorage<Transform> physicsmod$getDynamicUniformStorage();

   DynamicUniformStorage<LightUniform> physicsmod$getLightUniformStorage();

   DynamicUniformStorage<BrightnessUniform> physicsmod$getBrightnessUniformStorage();

   DynamicUniformStorage<OceanUniform> physicsmod$getOceanUniformStorage();

   DynamicUniformStorage<OceanChunkUniform> physicsmod$getOceanChunkUniformStorage();

   DynamicUniformStorage<RippleUniform> physicsmod$getRippleUniformStorage();

   DynamicUniformStorage<LiquidUniform> physicsmod$getLiquidUniformStorage();

   DynamicUniformStorage<GaussianDepthBlurUniform> physicsmod$getGaussianDepthBlurUniformStorage();

   DynamicUniformStorage<SmokeUniform> physicsmod$getSmokeUniformStorage();
}
