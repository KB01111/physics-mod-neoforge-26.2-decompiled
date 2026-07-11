package net.diebuddies.compat;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlRenderPass;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.opengl.Uniform.Sampler;
import com.mojang.blaze3d.opengl.Uniform.Ubo;
import com.mojang.blaze3d.opengl.Uniform.Utb;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.BindGroupLayout.UniformDescription;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.diebuddies.mixins.iris.MixinExtendedShaderAccessor;
import net.diebuddies.mixins.iris.MixinHandRendererAccessor;
import net.diebuddies.physics.ocean.PhysicsExtendedPipeline;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.PhysicsVertexFormatBuilder;
import net.diebuddies.util.ShaderType;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.gl.IrisRenderSystem;
import net.irisshaders.iris.pathways.HandRenderer;
import net.irisshaders.iris.pipeline.IrisPipelines;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.WorldRenderingPhase;
import net.irisshaders.iris.pipeline.programs.IrisProgram;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3f;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL31C;

public class Iris {
   public static String oceanError = "";
   public static String liquidsError = "";
   public static String smokeError = "";
   public static final ThreadLocal<Boolean> compilingLiquidShadowShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> compilingLiquidShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<ShaderType> preprocessOceanStage = ThreadLocal.withInitial(() -> null);
   public static final ThreadLocal<Boolean> injectIntoEntityOrShadowShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> compilingSmokeShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static final ThreadLocal<Boolean> compilingOceanShader = ThreadLocal.withInitial(() -> Boolean.FALSE);
   public static int ssboBindingIndex0 = 0;
   public static int ssboBindingIndex1 = 0;
   private static final VertexFormat PHYSICS_ENTITY_FORMAT = PhysicsVertexFormatBuilder.builder(0)
      .addAttribute("Position", PhysicsShaders.POSITION_FORMAT)
      .addAttribute("Color", PhysicsShaders.COLOR_FORMAT)
      .addAttribute("UV0", PhysicsShaders.UV0_FORMAT)
      .addDummy("UV1", PhysicsShaders.UV1_FORMAT)
      .addDummy("UV2", PhysicsShaders.UV2_FORMAT)
      .addAttribute("Normal", PhysicsShaders.NORMAL_FORMAT)
      .addDummy("iris_Entity", GpuFormat.RGBA16_UINT)
      .addAttribute("mc_midTexCoord", GpuFormat.RG32_FLOAT)
      .addAttribute("at_tangent", GpuFormat.RGBA8_SNORM)
      .build();
   private static final VertexFormat SNOW_FORMAT = PhysicsVertexFormatBuilder.builder(0)
      .addAttribute("Position", PhysicsShaders.POSITION_FORMAT)
      .addDummy("Color", PhysicsShaders.COLOR_FORMAT)
      .addDummy("UV0", PhysicsShaders.UV0_FORMAT)
      .addDummy("UV1", PhysicsShaders.UV1_FORMAT)
      .addAttribute("UV2", PhysicsShaders.UV2_FORMAT)
      .addAttribute("Normal", PhysicsShaders.NORMAL_FORMAT)
      .addDummy("iris_Entity", GpuFormat.RGBA16_UINT)
      .addDummy("mc_midTexCoord", GpuFormat.RG32_FLOAT)
      .addAttribute("at_tangent", GpuFormat.RGBA8_SNORM)
      .build();
   private static final VertexFormat OCEAN_FORMAT = PhysicsVertexFormatBuilder.builder(0)
      .addAttribute("Position", PhysicsShaders.POSITION_FORMAT)
      .addAttribute("Color", PhysicsShaders.COLOR_FORMAT)
      .addAttribute("UV0", PhysicsShaders.UV0_FORMAT)
      .addAttribute("UV2", PhysicsShaders.UV2_FORMAT)
      .addDummy("Normal", PhysicsShaders.NORMAL_FORMAT)
      .addDummy("mc_Entity", GpuFormat.RG16_SINT)
      .addDummy("mc_midTexCoord", GpuFormat.RG32_FLOAT)
      .addDummy("at_tangent", GpuFormat.RGBA8_SNORM)
      .addDummy("at_midBlock", GpuFormat.RGB8_SINT)
      .addAttribute("physics_waviness", PhysicsShaders.OCEAN_WAVINESS_FORMAT)
      .build();
   private static final VertexFormat SMOKE_FORMAT = PhysicsVertexFormatBuilder.builder(0)
      .addAttribute("Position", PhysicsShaders.POSITION_FORMAT)
      .addAttribute("Color", PhysicsShaders.COLOR_FORMAT)
      .addAttribute("UV0", PhysicsShaders.UV0_FORMAT)
      .addDummy("UV1", PhysicsShaders.UV1_FORMAT)
      .addDummy("UV2", PhysicsShaders.UV2_FORMAT)
      .addAttribute("Normal", PhysicsShaders.NORMAL_FORMAT)
      .addDummy("iris_Entity", GpuFormat.RGBA16_UINT)
      .addDummy("mc_midTexCoord", GpuFormat.RG32_FLOAT)
      .addDummy("at_tangent", GpuFormat.RGBA8_SNORM)
      .build();
   private static final VertexFormat LIQUID_FORMAT = PhysicsVertexFormatBuilder.builder(0)
      .addAttribute("Position", PhysicsShaders.POSITION_FORMAT)
      .addDummy("Color", PhysicsShaders.COLOR_FORMAT)
      .addDummy("UV0", PhysicsShaders.UV0_FORMAT)
      .addDummy("UV2", PhysicsShaders.UV2_FORMAT)
      .addDummy("Normal", PhysicsShaders.NORMAL_FORMAT)
      .addDummy("mc_Entity", GpuFormat.RG16_SINT)
      .addDummy("mc_midTexCoord", GpuFormat.RG32_FLOAT)
      .addDummy("at_tangent", GpuFormat.RGBA8_SNORM)
      .addDummy("at_midBlock", GpuFormat.RGB8_SINT)
      .build();
   private static IdentityHashMap<VertexFormat, VertexFormat> customFormat = new IdentityHashMap<>();
   private static Matrix4f tmp1 = new Matrix4f();
   private static Matrix3f tmp2 = new Matrix3f();
   private static float[] tmpNormal = new float[9];
   private static float[] tmpInvModelView = new float[16];

   public static void registerProperUniforms(GlProgram program, int programId, Map<String, Uniform> uniformsByName) {
      if (program instanceof IrisProgram) {
         if (compilingOceanShader.get()) {
            registerPhysicsPipelineBindings(programId, uniformsByName, PhysicsShaders.PHYSICS_OCEAN_PIPELINE);
         } else if (compilingSmokeShader.get()) {
            registerPhysicsPipelineBindings(programId, uniformsByName, PhysicsShaders.PHYSICS_SMOKE_PIPELINE);
         } else if (compilingLiquidShadowShader.get()) {
            registerPhysicsPipelineBindings(programId, uniformsByName, PhysicsShaders.PHYSICS_LIQUID_SHADOW_PIPELINE);
         } else if (compilingLiquidShader.get()) {
            registerPhysicsPipelineBindings(programId, uniformsByName, PhysicsShaders.PHYSICS_LIQUID_PIPELINE);
         }
      }
   }

   private static void registerPhysicsPipelineBindings(int programId, Map<String, Uniform> uniformsByName, RenderPipeline pipeline) {
      registerPhysicsBindingsFromLayouts(programId, uniformsByName, pipeline.getBindGroupLayouts());
   }

   private static void registerPhysicsBindingsFromLayouts(int programId, Map<String, Uniform> uniformsByName, List<BindGroupLayout> bindGroupLayouts) {
      for (UniformDescription uniform : BindGroupLayout.flattenUniforms(bindGroupLayouts)) {
         if (isPhysicsUniform(uniform.name())) {
            if (uniform.type() == UniformType.UNIFORM_BUFFER) {
               registerUniformBlock(programId, uniformsByName, uniform.name());
            } else if (uniform.type() == UniformType.TEXEL_BUFFER) {
               registerTexelBuffer(programId, uniformsByName, uniform.name(), uniform.gpuFormat());
            }
         }
      }

      for (String sampler : BindGroupLayout.flattenSamplers(bindGroupLayouts)) {
         if (isPhysicsSampler(sampler)) {
            registerSampler(programId, uniformsByName, sampler);
         }
      }
   }

   private static boolean isPhysicsUniform(String name) {
      return name.startsWith("Physics");
   }

   private static boolean isPhysicsSampler(String name) {
      return name.startsWith("physics_");
   }

   private static void registerUniformBlock(int programId, Map<String, Uniform> uniformsByName, String blockName) {
      if (!uniformsByName.containsKey(blockName)) {
         int blockIndex = GL31C.glGetUniformBlockIndex(programId, blockName);
         if (blockIndex != -1) {
            int binding = nextFreeUboBinding(programId, uniformsByName);
            GL31C.glUniformBlockBinding(programId, blockIndex, binding);
            uniformsByName.put(blockName, new Ubo(binding));
         }
      }
   }

   private static void registerSampler(int programId, Map<String, Uniform> uniformsByName, String samplerName) {
      if (!uniformsByName.containsKey(samplerName)) {
         int location = GlStateManager._glGetUniformLocation(programId, samplerName);
         if (location != -1) {
            int samplerIndex = nextFreeSamplerIndex(uniformsByName);
            uniformsByName.put(samplerName, new Sampler(location, samplerIndex));
         }
      }
   }

   private static void registerTexelBuffer(int programId, Map<String, Uniform> uniformsByName, String texelBufferName, GpuFormat format) {
      if (!uniformsByName.containsKey(texelBufferName)) {
         if (format == null) {
            throw new IllegalStateException("Physics texel buffer " + texelBufferName + " is missing a GPU format");
         } else {
            int location = GlStateManager._glGetUniformLocation(programId, texelBufferName);
            if (location != -1) {
               int samplerIndex = nextFreeSamplerIndex(uniformsByName);
               uniformsByName.put(texelBufferName, new Utb(location, samplerIndex, format));
            }
         }
      }
   }

   private static int nextFreeUboBinding(int programId, Map<String, Uniform> uniformsByName) {
      int max = -1;

      for (Uniform uniform : uniformsByName.values()) {
         if (uniform instanceof Ubo ubo) {
            max = Math.max(max, ubo.blockBinding());
         }
      }

      return max + 1;
   }

   private static int nextFreeSamplerIndex(Map<String, Uniform> uniformsByName) {
      int maxCombinedTextureUnits = GL11C.glGetInteger(35661);
      int maxFragmentTextureUnits = GL11C.glGetInteger(34930);
      int irisGlStateManagerMaxUnit = 64;
      int highestAllowedSamplerIndex = Math.min(Math.min(maxCombinedTextureUnits - 1, maxFragmentTextureUnits - 1), irisGlStateManagerMaxUnit);

      for (int samplerIndex = highestAllowedSamplerIndex; samplerIndex >= 3; samplerIndex--) {
         if (!isSamplerIndexUsed(samplerIndex, uniformsByName)) {
            return samplerIndex;
         }
      }

      throw new IllegalStateException("No free sampler index left for Physics Mod Iris shader");
   }

   private static boolean isSamplerIndexUsed(int samplerIndex, Map<String, Uniform> uniformsByName) {
      for (Uniform uniform : uniformsByName.values()) {
         if (uniform instanceof Sampler sampler && sampler.samplerIndex() == samplerIndex) {
            return true;
         }

         if (uniform instanceof Utb utb && utb.samplerIndex() == samplerIndex) {
            return true;
         }
      }

      return false;
   }

   public static short getMaterialID(BlockState block) {
      try {
         Object2IntMap<BlockState> idMap = WorldRenderingSettings.INSTANCE.getBlockStateIds();
         if (idMap != null) {
            return (short)idMap.getOrDefault(block, -1);
         }
      } catch (Exception var2) {
         var2.printStackTrace();
      }

      return -1;
   }

   @Nullable
   public static GlProgram getOceanProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getOceanShader()
         : null;
   }

   @Nullable
   public static GlProgram getOceanShadowProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getOceanShadowShader()
         : null;
   }

   @Nullable
   public static GlProgram getLiquidProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getLiquidShader()
         : null;
   }

   @Nullable
   public static GlProgram getLiquidShadowProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getLiquidShadowShader()
         : null;
   }

   @Nullable
   public static GlProgram getSmokeProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getSmokeShader()
         : null;
   }

   @Nullable
   public static GlProgram getSmokeShadowProgram() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$getSmokeShadowShader()
         : null;
   }

   public static boolean renderOceanShadow() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$renderOceanShadow()
         : false;
   }

   public static boolean renderLiquidShadow() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$renderLiquidShadow()
         : false;
   }

   public static boolean renderSmokeShadow() {
      return net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof PhysicsExtendedPipeline extended
         ? extended.physicsmod$renderSmokeShadow()
         : false;
   }

   public static void enableHandRendering() {
      ((MixinHandRendererAccessor)HandRenderer.INSTANCE).setRenderingSolid(true);
   }

   public static void disableHandRendering() {
      ((MixinHandRendererAccessor)HandRenderer.INSTANCE).setRenderingSolid(false);
   }

   public static boolean isExtending() {
      return IrisApi.getInstance().isShaderPackInUse();
   }

   public static boolean isShadowPass() {
      return IrisApi.getInstance().isRenderingShadowPass();
   }

   public static void setNormalMatrix(RenderPass renderPass, Matrix4fc modelViewMatrix) {
      if (((GlRenderPass)renderPass.backend).pipeline.program() instanceof MixinExtendedShaderAccessor extended) {
         int mvi = extended.getModelViewInverse();
         int normal = extended.getNormalMat();
         if (mvi > -1) {
            modelViewMatrix.invert(tmp1).get(tmpInvModelView);
            IrisRenderSystem.uniformMatrix4fv(mvi, false, tmpInvModelView);
         }

         if (normal > -1) {
            if (mvi > -1) {
               tmp1.transpose3x3(tmp2);
            } else {
               modelViewMatrix.normal(tmp2);
            }

            tmp2.get(tmpNormal);
            IrisRenderSystem.uniformMatrix3fv(normal, false, tmpNormal);
         }
      }
   }

   public static void setNormalMatrix(RenderPass renderPass, Matrix4fc modelViewMatrix, Matrix3fc normalMatrix) {
      if (((GlRenderPass)renderPass.backend).pipeline.program() instanceof MixinExtendedShaderAccessor extended) {
         int mvi = extended.getModelViewInverse();
         int normal = extended.getNormalMat();
         if (mvi > -1) {
            modelViewMatrix.invert(tmp1).get(tmpInvModelView);
            IrisRenderSystem.uniformMatrix4fv(mvi, false, tmpInvModelView);
         }

         if (normal > -1) {
            normalMatrix.get(tmpNormal);
            IrisRenderSystem.uniformMatrix3fv(normal, false, tmpNormal);
         }
      }
   }

   public static void setShadowMatrices(MainRenderer mainRenderer) {
      mainRenderer.setViewAndProjectionMatrix(ShadowRenderer.MODELVIEW, ShadowRenderer.PROJECTION);
   }

   public static void setRenderingPipelineToTerrainTranslucent(MainRenderer mainRenderer) {
      if (net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline iris) {
         iris.setPhase(WorldRenderingPhase.TERRAIN_TRANSLUCENT);
      }
   }

   public static void setRenderingPipelineToEntities(MainRenderer mainRenderer) {
      if (net.irisshaders.iris.Iris.getPipelineManager().getPipelineNullable() instanceof IrisRenderingPipeline iris) {
         iris.setPhase(WorldRenderingPhase.ENTITIES);
      }
   }

   public static VertexFormat remapFormat(VertexFormat format) {
      if (customFormat == null) {
         return format;
      } else {
         return customFormat.containsKey(format) ? customFormat.get(format) : format;
      }
   }

   public static boolean isUsingReverseZ() {
      return !net.irisshaders.iris.Iris.isPackInUseQuick();
   }

   public static void init() {
      IrisPipelines.copyPipeline(RenderPipelines.ARMOR_CUTOUT_NO_CULL, PhysicsShaders.PHYSICS_ENTITY_CULL_PIPELINE);
      IrisPipelines.copyPipeline(RenderPipelines.ARMOR_CUTOUT_NO_CULL, PhysicsShaders.PHYSICS_ENTITY_PIPELINE);
      IrisPipelines.copyPipeline(RenderPipelines.ENTITY_TRANSLUCENT, PhysicsShaders.PHYSICS_ENTITY_TRANSPARENT_CULL_PIPELINE);
      IrisPipelines.copyPipeline(RenderPipelines.ENTITY_TRANSLUCENT, PhysicsShaders.PHYSICS_ENTITY_TRANSPARENT_PIPELINE);
      IrisPipelines.copyPipeline(RenderPipelines.ARMOR_CUTOUT_NO_CULL, PhysicsShaders.PHYSICS_SNOW_CULL_PIPELINE);
      IrisPipelines.copyPipeline(RenderPipelines.ARMOR_CUTOUT_NO_CULL, PhysicsShaders.PHYSICS_SNOW_PIPELINE);
   }

   static {
      customFormat.put(PhysicsShaders.PHYSICS_ENTITY_FORMAT, PHYSICS_ENTITY_FORMAT);
      customFormat.put(PhysicsShaders.SNOW_FORMAT, SNOW_FORMAT);
      customFormat.put(PhysicsShaders.OCEAN_FORMAT, OCEAN_FORMAT);
      customFormat.put(PhysicsShaders.SMOKE_FORMAT, SMOKE_FORMAT);
      customFormat.put(PhysicsShaders.LIQUID_FORMAT, LIQUID_FORMAT);
   }
}
