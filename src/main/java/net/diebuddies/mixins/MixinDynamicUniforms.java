package net.diebuddies.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import net.diebuddies.render.util.BrightnessUniform;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.GaussianDepthBlurUniform;
import net.diebuddies.render.util.LightUniform;
import net.diebuddies.render.util.LiquidUniform;
import net.diebuddies.render.util.OceanChunkUniform;
import net.diebuddies.render.util.OceanUniform;
import net.diebuddies.render.util.RippleUniform;
import net.diebuddies.render.util.SmokeUniform;
import net.minecraft.client.renderer.DynamicUniformStorage;
import net.minecraft.client.renderer.DynamicUniforms;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({DynamicUniforms.class})
public class MixinDynamicUniforms implements DynamicUniformsExtension {
   @Shadow
   @Final
   private DynamicUniformStorage<Transform> transforms;
   @Unique
   private final int physicsmod$lightUboSize = new Std140SizeCalculator().putVec3().putVec3().get();
   @Unique
   private final DynamicUniformStorage<LightUniform> physicsmod$lights = new DynamicUniformStorage("Physics Mod Lights UBO", this.physicsmod$lightUboSize, 128);
   @Unique
   private final int physicsmod$brightnessUboSize = new Std140SizeCalculator().putInt().putInt().putInt().putInt().get();
   @Unique
   private final DynamicUniformStorage<BrightnessUniform> physicsmod$brightness = new DynamicUniformStorage(
      "Physics Mod Brightness UBO", this.physicsmod$brightnessUboSize, 256
   );
   @Unique
   private final int physicsmod$oceanUboSize = new Std140SizeCalculator()
      .putFloat()
      .putFloat()
      .putInt()
      .putFloat()
      .putFloat()
      .putFloat()
      .putFloat()
      .putFloat()
      .get();
   @Unique
   private final DynamicUniformStorage<OceanUniform> physicsmod$oceans = new DynamicUniformStorage("Physics Mod Ocean UBO", this.physicsmod$oceanUboSize, 1);
   @Unique
   private final int physicsmod$oceanChunkUboSize = new Std140SizeCalculator().putFloat().putFloat().putFloat().putFloat().putFloat().get();
   @Unique
   private final DynamicUniformStorage<OceanChunkUniform> physicsmod$oceanChunks = new DynamicUniformStorage(
      "Physics Mod Ocean Chunk UBO", this.physicsmod$oceanChunkUboSize, 256
   );
   @Unique
   private final int physicsmod$rippleUboSize = new Std140SizeCalculator().putMat4f().putMat4f().putVec4().putVec4().putVec4().putVec4().get();
   @Unique
   private final DynamicUniformStorage<RippleUniform> physicsmod$ripples = new DynamicUniformStorage(
      "Physics Mod Ripple UBO", this.physicsmod$rippleUboSize, 16
   );
   @Unique
   private final int physicsmod$liquidUboSize = new Std140SizeCalculator()
      .putMat4f()
      .putMat4f()
      .putMat4f()
      .putMat4f()
      .putMat4f()
      .putVec4()
      .putVec4()
      .putVec4()
      .putInt()
      .get();
   @Unique
   private final DynamicUniformStorage<LiquidUniform> physicsmod$liquids = new DynamicUniformStorage("Physics Mod Liquid UBO", this.physicsmod$liquidUboSize, 8);
   @Unique
   private final int physicsmod$gaussianDepthBlurUboSize = new Std140SizeCalculator().putVec4().putVec4().putInt().putInt().get();
   @Unique
   private final DynamicUniformStorage<GaussianDepthBlurUniform> physicsmod$gaussianDepthBlur = new DynamicUniformStorage(
      "Physics Mod Gaussian Depth Blur UBO", this.physicsmod$gaussianDepthBlurUboSize, 8
   );
   @Unique
   private final int physicsmod$smokeUboSize = new Std140SizeCalculator().putMat4f().putMat4f().putMat4f().putVec4().putVec4().putVec4().putInt().get();
   @Unique
   private final DynamicUniformStorage<SmokeUniform> physicsmod$smokes = new DynamicUniformStorage("Physics Mod Smoke UBO", this.physicsmod$smokeUboSize, 4);

   @Inject(
      at = {@At("TAIL")},
      method = {"reset"},
      cancellable = true
   )
   public void physicsmod$reset(CallbackInfo info) {
      this.physicsmod$lights.endFrame();
      this.physicsmod$brightness.endFrame();
      this.physicsmod$oceans.endFrame();
      this.physicsmod$oceanChunks.endFrame();
      this.physicsmod$ripples.endFrame();
      this.physicsmod$liquids.endFrame();
      this.physicsmod$gaussianDepthBlur.endFrame();
      this.physicsmod$smokes.endFrame();
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"close"},
      cancellable = true
   )
   public void physicsmod$close(CallbackInfo info) {
      this.physicsmod$lights.close();
      this.physicsmod$brightness.close();
      this.physicsmod$oceans.close();
      this.physicsmod$oceanChunks.close();
      this.physicsmod$ripples.close();
      this.physicsmod$liquids.close();
      this.physicsmod$gaussianDepthBlur.close();
      this.physicsmod$smokes.close();
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeLightUniforms(LightUniform... lightUniforms) {
      return this.physicsmod$lights.writeUniforms(lightUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeBrightnessUniforms(BrightnessUniform... brightnessUniforms) {
      return this.physicsmod$brightness.writeUniforms(brightnessUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeOceanUniforms(OceanUniform... oceanUniforms) {
      return this.physicsmod$oceans.writeUniforms(oceanUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeOceanChunkUniforms(OceanChunkUniform... oceanChunkUniforms) {
      return this.physicsmod$oceanChunks.writeUniforms(oceanChunkUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeRippleUniforms(RippleUniform... rippleUniforms) {
      return this.physicsmod$ripples.writeUniforms(rippleUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeLiquidUniforms(LiquidUniform... liquidUniforms) {
      return this.physicsmod$liquids.writeUniforms(liquidUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeGaussianDepthBlurUniforms(GaussianDepthBlurUniform... blurUniforms) {
      return this.physicsmod$gaussianDepthBlur.writeUniforms(blurUniforms);
   }

   @Override
   public GpuBufferSlice[] physicsmod$writeSmokeUniforms(SmokeUniform... smokeUniforms) {
      return this.physicsmod$smokes.writeUniforms(smokeUniforms);
   }

   @Override
   public DynamicUniformStorage<Transform> physicsmod$getDynamicUniformStorage() {
      return this.transforms;
   }

   @Override
   public DynamicUniformStorage<LightUniform> physicsmod$getLightUniformStorage() {
      return this.physicsmod$lights;
   }

   @Override
   public DynamicUniformStorage<BrightnessUniform> physicsmod$getBrightnessUniformStorage() {
      return this.physicsmod$brightness;
   }

   @Override
   public DynamicUniformStorage<OceanUniform> physicsmod$getOceanUniformStorage() {
      return this.physicsmod$oceans;
   }

   @Override
   public DynamicUniformStorage<OceanChunkUniform> physicsmod$getOceanChunkUniformStorage() {
      return this.physicsmod$oceanChunks;
   }

   @Override
   public DynamicUniformStorage<RippleUniform> physicsmod$getRippleUniformStorage() {
      return this.physicsmod$ripples;
   }

   @Override
   public DynamicUniformStorage<LiquidUniform> physicsmod$getLiquidUniformStorage() {
      return this.physicsmod$liquids;
   }

   @Override
   public DynamicUniformStorage<GaussianDepthBlurUniform> physicsmod$getGaussianDepthBlurUniformStorage() {
      return this.physicsmod$gaussianDepthBlur;
   }

   @Override
   public DynamicUniformStorage<SmokeUniform> physicsmod$getSmokeUniformStorage() {
      return this.physicsmod$smokes;
   }
}
