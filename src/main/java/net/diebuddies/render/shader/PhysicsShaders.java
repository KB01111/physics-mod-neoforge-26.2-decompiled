package net.diebuddies.render.shader;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline.Snippet;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.LayeringTransform;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

public class PhysicsShaders {
   public static final String OCEAN_UNIFORM_BLOCK = "PhysicsOcean";
   public static final String OCEAN_CHUNK_UNIFORM_BLOCK = "PhysicsOceanChunk";
   public static final String OCEAN_RIPPLE_UNIFORM_BLOCK = "PhysicsRipple";
   public static final String OCEAN_RIPPLE_INSTANCE_BLOCK = "PhysicsRippleInstances";
   public static final String SMOKE_UNIFORM_BLOCK = "PhysicsSmoke";
   public static final String BRIGHTNESS_UNIFORM_BLOCK = "PhysicsBrightness";
   public static final String SMOKE_INSTANCE_BLOCK = "PhysicsSmokeInstances";
   public static final String LIQUID_UNIFORM_BLOCK = "PhysicsLiquid";
   public static final String LIQUID_INSTANCE_BLOCK = "PhysicsLiquidInstances";
   public static final String GAUSSIAN_DEPTH_BLUR_UNIFORM_BLOCK = "PhysicsLiquidGaussianBlur";
   public static final String OCEAN_WAVINESS_SEMANTIC_NAME = "physics_waviness";
   public static final GpuFormat OCEAN_WAVINESS_FORMAT = GpuFormat.R32_FLOAT;
   public static final GpuFormat OCEAN_RIPPLE_IMPULSE_FORMAT = GpuFormat.R16_FLOAT;
   public static final GpuFormat OCEAN_RIPPLE_SIMULATION_FORMAT = GpuFormat.RG16_FLOAT;
   public static final GpuFormat POSITION_FORMAT = GpuFormat.RGB32_FLOAT;
   public static final GpuFormat COLOR_FORMAT = GpuFormat.RGBA8_UNORM;
   public static final GpuFormat UV0_FORMAT = GpuFormat.RG32_FLOAT;
   public static final GpuFormat UV1_FORMAT = GpuFormat.RG16_SINT;
   public static final GpuFormat UV2_FORMAT = GpuFormat.RG16_SINT;
   public static final GpuFormat NORMAL_FORMAT = GpuFormat.RGBA8_SNORM;
   public static final GpuFormat LINE_WIDTH_FORMAT = GpuFormat.R32_FLOAT;
   public static final VertexFormat OCEAN_FORMAT = VertexFormat.builder(0)
      .addAttribute("Position", POSITION_FORMAT)
      .addAttribute("Color", COLOR_FORMAT)
      .addAttribute("UV0", UV0_FORMAT)
      .addAttribute("UV2", UV2_FORMAT)
      .addAttribute("physics_waviness", OCEAN_WAVINESS_FORMAT)
      .build();
   public static final VertexFormat OCEAN_RIPPLE_FORMAT = VertexFormat.builder(0)
      .addAttribute("Position", POSITION_FORMAT)
      .addAttribute("UV0", UV0_FORMAT)
      .build();
   public static final VertexFormat OCEAN_RIPPLE_INSTANCE_FORMAT = VertexFormat.builder(0)
      .addAttribute("ImpulsePosition", GpuFormat.RGBA32_FLOAT)
      .addAttribute("ImpulseShape", GpuFormat.RGBA32_FLOAT)
      .build();
   public static final VertexFormat PHYSICS_ENTITY_FORMAT = VertexFormat.builder(0)
      .addAttribute("Position", POSITION_FORMAT)
      .addAttribute("Color", COLOR_FORMAT)
      .addAttribute("UV0", UV0_FORMAT)
      .addAttribute("Normal", NORMAL_FORMAT)
      .build();
   public static final VertexFormat SMOKE_FORMAT = VertexFormat.builder(0)
      .addAttribute("Position", POSITION_FORMAT)
      .addAttribute("Color", COLOR_FORMAT)
      .addAttribute("UV0", UV0_FORMAT)
      .addAttribute("Normal", NORMAL_FORMAT)
      .build();
   public static final VertexFormat SMOKE_INSTANCE_FORMAT = VertexFormat.builder(0)
      .addAttribute("Light", GpuFormat.RGBA32_FLOAT)
      .addAttribute("OffsetOld", GpuFormat.RGBA32_FLOAT)
      .addAttribute("OffsetNew", GpuFormat.RGBA32_FLOAT)
      .build();
   public static final VertexFormat LIQUID_FORMAT = VertexFormat.builder(0).addAttribute("Position", POSITION_FORMAT).build();
   public static final VertexFormat LIQUID_INSTANCE_FORMAT = VertexFormat.builder(0)
      .addAttribute("OffsetOld", GpuFormat.RGBA32_FLOAT)
      .addAttribute("OffsetNew", GpuFormat.RGBA32_FLOAT)
      .build();
   public static final VertexFormat SNOW_FORMAT = VertexFormat.builder(0)
      .addAttribute("Position", POSITION_FORMAT)
      .addAttribute("UV2", UV2_FORMAT)
      .addAttribute("Normal", NORMAL_FORMAT)
      .build();
   public static final RenderPipeline PHYSICS_ENTITY_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.BRIGHTNESS)
         .withLocation("pipeline/physics_entity")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(false)
         .withVertexBinding(0, PHYSICS_ENTITY_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_ENTITY_CULL_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.BRIGHTNESS)
         .withLocation("pipeline/physics_entity_cull")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(true)
         .withVertexBinding(0, PHYSICS_ENTITY_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_SMOKE_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/smoke"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.SMOKE_DATA)
         .withBindGroupLayout(PhysicsBindGroupLayouts.SMOKE_TEXTURES)
         .withLocation("pipeline/physics_smoke")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(false)
         .withVertexBinding(0, SMOKE_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .build()
   );
   public static final RenderPipeline PHYSICS_ENTITY_TRANSPARENT_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/transparent_physics"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/transparent_physics"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.BRIGHTNESS)
         .withLocation("pipeline/physics_entity_transparent")
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withCull(false)
         .withVertexBinding(0, PHYSICS_ENTITY_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_ENTITY_TRANSPARENT_CULL_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/transparent_physics"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/transparent_physics"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.BRIGHTNESS)
         .withLocation("pipeline/physics_entity_transparent_cull")
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withCull(true)
         .withVertexBinding(0, PHYSICS_ENTITY_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_SNOW_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics_snow"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withLocation("pipeline/physics_snow")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(false)
         .withVertexBinding(0, SNOW_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_SNOW_CULL_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics_snow"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/solid_physics"))
         .withLocation("pipeline/physics_snow_cull")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(true)
         .withVertexBinding(0, SNOW_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final Snippet TERRAIN_SNIPPET = RenderPipeline.builder(new Snippet[]{RenderPipelines.GENERIC_BLOCKS_SNIPPET})
      .withBindGroupLayout(BindGroupLayouts.MATRICES_PROJECTION)
      .withVertexShader("core/terrain")
      .withFragmentShader("core/terrain")
      .buildSnippet();
   public static final RenderPipeline PHYSICS_OCEAN_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{TERRAIN_SNIPPET})
         .withBindGroupLayout(PhysicsBindGroupLayouts.OCEAN_DATA)
         .withBindGroupLayout(PhysicsBindGroupLayouts.OCEAN_TEXTURES)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean"))
         .withCull(false)
         .withLocation("pipeline/physics_ocean")
         .withVertexBinding(0, OCEAN_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .build()
   );
   public static final RenderPipeline PHYSICS_OCEAN_RIPPLE_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withBindGroupLayout(PhysicsBindGroupLayouts.OCEAN_RIPPLE_DATA)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean_ripple"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean_ripple"))
         .withLocation("pipeline/physics_ocean_ripple_splat")
         .withCull(false)
         .withVertexBinding(0, OCEAN_RIPPLE_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(new ColorTargetState(Optional.of(BlendFunction.ADDITIVE), OCEAN_RIPPLE_IMPULSE_FORMAT, 15))
         .build()
   );
   public static final RenderPipeline PHYSICS_OCEAN_RIPPLE_SIMULATION_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withBindGroupLayout(PhysicsBindGroupLayouts.OCEAN_RIPPLE_SIMULATION)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean_ripple_simulate"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/ocean_ripple_simulate"))
         .withLocation("pipeline/physics_ocean_ripple_simulate")
         .withCull(false)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .withColorTargetState(new ColorTargetState(Optional.empty(), OCEAN_RIPPLE_SIMULATION_FORMAT, 15))
         .build()
   );
   public static final RenderPipeline PHYSICS_LIQUID_DATA_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[0])
         .withBindGroupLayout(PhysicsBindGroupLayouts.LIQUID_INSTANCING)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid"))
         .withLocation("pipeline/physics_liquid_data")
         .withCull(false)
         .withDepthStencilState(DepthStencilState.DEFAULT)
         .withColorTargetState(0, new ColorTargetState(Optional.empty(), GpuFormat.RGBA32_FLOAT, 15))
         .withVertexBinding(0, LIQUID_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_LIQUID_SHADOW_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TERRAIN_SNIPPET})
         .withBindGroupLayout(PhysicsBindGroupLayouts.LIQUID_INSTANCING)
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid"))
         .withLocation("pipeline/physics_liquid_shadow")
         .withCull(false)
         .withDepthStencilState(DepthStencilState.DEFAULT)
         .withVertexBinding(0, LIQUID_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   public static final RenderPipeline PHYSICS_LIQUID_PIPELINE = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.TERRAIN_SNIPPET})
         .withVertexShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid_composite"))
         .withFragmentShader(Identifier.fromNamespaceAndPath("physicsmod", "core/liquid_composite"))
         .withBindGroupLayout(PhysicsBindGroupLayouts.LIQUID_COMPOSITE)
         .withLocation("pipeline/physics_liquid")
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, false))
         .withVertexBinding(0, LIQUID_FORMAT)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   private static final RenderPipeline PHYSICS_ENTITY = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withLocation("pipeline/physics_cloth")
         .withShaderDefine("ALPHA_CUTOUT", 0.1F)
         .withShaderDefine("NO_OVERLAY")
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.ENTITY)
         .withPrimitiveTopology(PrimitiveTopology.TRIANGLES)
         .build()
   );
   private static final RenderPipeline PHYSICS_BANNER = RenderPipelines.register(
      RenderPipeline.builder(new Snippet[]{RenderPipelines.ENTITY_SNIPPET})
         .withLocation("pipeline/physics_banner")
         .withShaderDefine("NO_OVERLAY")
         .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
         .withCull(false)
         .withVertexBinding(0, DefaultVertexFormat.ENTITY)
         .withPrimitiveTopology(PrimitiveTopology.QUADS)
         .build()
   );
   public static final Function<Identifier, RenderType> PHYSICS_CLOTH_RENDER_IRIS = Util.memoize(
      image -> {
         RenderSetup renderSetup = RenderSetup.builder(RenderPipelines.ARMOR_CUTOUT_NO_CULL)
            .withTexture("Sampler0", image)
            .useLightmap()
            .useOverlay()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup();
         return RenderType.create("armor_cutout_no_cull", renderSetup);
      }
   );
   public static final Function<Identifier, RenderType> PHYSICS_BANNER_RENDER_IRIS = Util.memoize(
      image -> {
         RenderSetup renderSetup = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
            .withTexture("Sampler0", image)
            .useLightmap()
            .useOverlay()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup();
         return RenderType.create("armor_cutout_no_cull", renderSetup);
      }
   );
   public static final Function<Identifier, RenderType> PHYSICS_CLOTH_RENDER = Util.memoize(
      image -> {
         RenderSetup renderSetup = RenderSetup.builder(PHYSICS_ENTITY)
            .withTexture("Sampler0", image)
            .useLightmap()
            .useOverlay()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup();
         return RenderType.create("physics_cloth", renderSetup);
      }
   );
   public static final Function<Identifier, RenderType> PHYSICS_BANNER_RENDER = Util.memoize(
      image -> {
         RenderSetup renderSetup = RenderSetup.builder(PHYSICS_BANNER)
            .withTexture("Sampler0", image)
            .useLightmap()
            .useOverlay()
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .createRenderSetup();
         return RenderType.create("physics_banner", renderSetup);
      }
   );

   public static void init() {
   }
}
