package net.diebuddies.render.shader;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.shaders.UniformType;

public final class PhysicsBindGroupLayouts {
   public static final BindGroupLayout DYNAMIC_TRANSFORMS_PROJECTION = BindGroupLayout.builder()
      .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
      .withUniform("Projection", UniformType.UNIFORM_BUFFER)
      .build();
   public static final BindGroupLayout BRIGHTNESS = BindGroupLayout.builder().withUniform("PhysicsBrightness", UniformType.UNIFORM_BUFFER).build();
   public static final BindGroupLayout SMOKE_DATA = BindGroupLayout.builder()
      .withUniform("PhysicsSmoke", UniformType.UNIFORM_BUFFER)
      .withUniform("PhysicsSmokeInstances", UniformType.TEXEL_BUFFER, GpuFormat.RGBA32_FLOAT)
      .build();
   public static final BindGroupLayout SMOKE_TEXTURES = BindGroupLayout.builder()
      .withSampler("physics_lightmap")
      .withSampler("physics_smokeSampler")
      .withSampler("physics_depth")
      .build();
   public static final BindGroupLayout OCEAN_DATA = BindGroupLayout.builder()
      .withUniform("PhysicsOcean", UniformType.UNIFORM_BUFFER)
      .withUniform("PhysicsOceanChunk", UniformType.UNIFORM_BUFFER)
      .build();
   public static final BindGroupLayout OCEAN_TEXTURES = BindGroupLayout.builder()
      .withSampler("physics_ripples")
      .withSampler("physics_foam")
      .withSampler("physics_lightmap")
      .build();
   public static final BindGroupLayout OCEAN_RIPPLE_DATA = BindGroupLayout.builder()
      .withUniform("PhysicsRipple", UniformType.UNIFORM_BUFFER)
      .withUniform("PhysicsRippleInstances", UniformType.TEXEL_BUFFER, GpuFormat.RGBA32_FLOAT)
      .build();
   public static final BindGroupLayout OCEAN_RIPPLE_SIMULATION = BindGroupLayout.builder()
      .withUniform("PhysicsRipple", UniformType.UNIFORM_BUFFER)
      .withSampler("physics_ripple_state")
      .withSampler("physics_ripple_impulse")
      .build();
   public static final BindGroupLayout LIQUID_INSTANCING = BindGroupLayout.builder()
      .withUniform("PhysicsLiquid", UniformType.UNIFORM_BUFFER)
      .withUniform("PhysicsLiquidInstances", UniformType.TEXEL_BUFFER, GpuFormat.RGBA32_FLOAT)
      .build();
   public static final BindGroupLayout LIQUID_COMPOSITE = BindGroupLayout.builder()
      .withUniform("PhysicsLiquid", UniformType.UNIFORM_BUFFER)
      .withUniform("Lighting", UniformType.UNIFORM_BUFFER)
      .withSampler("physics_liquidData")
      .build();
   public static final BindGroupLayout GAUSSIAN_DEPTH_BLUR = BindGroupLayout.builder()
      .withUniform("PhysicsLiquidGaussianBlur", UniformType.UNIFORM_BUFFER)
      .withSampler("imageMap")
      .build();

   private PhysicsBindGroupLayouts() {
   }
}
