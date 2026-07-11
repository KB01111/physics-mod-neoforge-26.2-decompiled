package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Optional;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class SmokeVolumePipelines {
   public static final String FRAME_UNIFORM = "PhysicsSmokeVolumeFrame";
   public static final String META_BUFFER = "PhysicsSmokeMeta";
   public static final BindGroupLayout FRAME = BindGroupLayout.builder().withUniform("PhysicsSmokeVolumeFrame", UniformType.UNIFORM_BUFFER).build();
   public static final BindGroupLayout VOLUME_META = BindGroupLayout.builder()
      .withUniform("PhysicsSmokeMeta", UniformType.TEXEL_BUFFER, GpuFormat.R32_UINT)
      .build();
   public static final BindGroupLayout MINECRAFT_FOG = BindGroupLayout.builder().withUniform("Fog", UniformType.UNIFORM_BUFFER).build();
   public static final BindGroupLayout RAYMARCH_TEXTURES = BindGroupLayout.builder()
      .withSampler("physics_depth")
      .withSampler("physics_density0")
      .withSampler("physics_density1")
      .withSampler("physics_density2")
      .withSampler("physics_density3")
      .withSampler("physics_occupancy0")
      .withSampler("physics_occupancy1")
      .withSampler("physics_occupancy2")
      .withSampler("physics_occupancy3")
      .withSampler("physics_light0")
      .withSampler("physics_light1")
      .withSampler("physics_light2")
      .withSampler("physics_light3")
      .build();
   public static final BindGroupLayout COMPOSITE_TEXTURES = BindGroupLayout.builder()
      .withSampler("physics_sceneColor")
      .withSampler("physics_sceneDepth")
      .withSampler("physics_smokeLow")
      .withSampler("physics_historySmoke")
      .withSampler("physics_historyDepth")
      .build();
   public static final BindGroupLayout SINGLE_TEXTURE = BindGroupLayout.builder().withSampler("physics_texture").build();
   public static final RenderPipeline RAYMARCH = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withLocation("pipeline/physics_smoke_volume_raymarch")
         .withBindGroupLayout(FRAME)
         .withBindGroupLayout(MINECRAFT_FOG)
         .withBindGroupLayout(VOLUME_META)
         .withBindGroupLayout(RAYMARCH_TEXTURES)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_fullscreen"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_raymarch"))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(0, new ColorTargetState(Optional.empty(), GpuFormat.RGBA16_FLOAT, 15))
         .build()
   );
   public static final RenderPipeline COMPOSITE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withLocation("pipeline/physics_smoke_volume_composite")
         .withBindGroupLayout(FRAME)
         .withBindGroupLayout(COMPOSITE_TEXTURES)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_fullscreen"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_composite"))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(0, new ColorTargetState(Optional.empty(), GpuFormat.RGBA16_FLOAT, 15))
         .build()
   );
   public static final RenderPipeline COPY_BLEND = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withLocation("pipeline/physics_smoke_volume_copy")
         .withBindGroupLayout(SINGLE_TEXTURE)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_fullscreen"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_copy"))
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline DEPTH_COPY = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withLocation("pipeline/physics_smoke_volume_depth_copy")
         .withBindGroupLayout(SINGLE_TEXTURE)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_fullscreen"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke_volume_depth_copy"))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.POSITION)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(0, new ColorTargetState(Optional.empty(), GpuFormat.R16_FLOAT, 15))
         .build()
   );

   private SmokeVolumePipelines() {
   }
}
