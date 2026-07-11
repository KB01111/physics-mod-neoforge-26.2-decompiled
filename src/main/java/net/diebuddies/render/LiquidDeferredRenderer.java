package net.diebuddies.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.render.shader.GaussianDepthBlurEffect;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.LiquidUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.DynamicUniforms.Transform;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.system.MemoryUtil;

public class LiquidDeferredRenderer {
   private static final GpuFormat LIQUID_DATA_FORMAT = GpuFormat.RGBA32_FLOAT;
   private final MainRenderer mainRenderer;
   private InstancedRenderer instancedRenderer;
   private SimpleColorDepthRenderTarget liquidDataTarget;
   private SimpleColorRenderTarget liquidBlurScratchTarget;
   private GaussianDepthBlurEffect blurEffect;
   private final Vector4f waterBounds = new Vector4f();
   private final Vector2f waterMidCoord = new Vector2f();
   @Nullable
   private GpuTextureView waterTextureView;
   private boolean needsInstanceUpdate = true;
   private GpuBuffer fullscreenVertexBuffer;

   public LiquidDeferredRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.instancedRenderer = new InstancedRenderer(
         "Physics Liquids",
         PhysicsShaders.LIQUID_FORMAT,
         PhysicsShaders.LIQUID_INSTANCE_FORMAT,
         new LiquidDeferredRenderer.LiquidMeshVertexWriter(PhysicsMod.liquid)
      );
      this.blurEffect = new GaussianDepthBlurEffect();
      this.fullscreenVertexBuffer = this.createFullscreenTriangle();
   }

   public void render(PhysicsWorld physics, ChunkSectionLayer sectionLayer, ClientLevel level, Matrix4fStack modelView, Vec3 cameraPos) {
   }

   private void renderLiquidDataIntoTargets(PhysicsWorld physics, Vec3 cameraPos) {
      GpuTexture sceneDepthTexture = this.resolveSceneDepthTexture();
      this.ensureRenderTargets(sceneDepthTexture);
      GpuBufferSlice liquidUniform = this.uploadLiquidUniform(physics, this.mainRenderer.viewMatrix, this.mainRenderer.projectionMatrix, cameraPos);
      if (liquidUniform != null && this.liquidDataTarget != null) {
         CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
         encoder.copyTextureToTexture(
            sceneDepthTexture, this.liquidDataTarget.depthTexture(), 0, 0, 0, 0, 0, this.liquidDataTarget.getWidth(), this.liquidDataTarget.getHeight()
         );
         RenderPass pass = encoder.createRenderPass(
            () -> "Physics Mod Liquid Data",
            this.liquidDataTarget.colorView(),
            Optional.of(new Vector4f(0.0F)),
            this.liquidDataTarget.depthView(),
            OptionalDouble.empty()
         );

         try {
            pass.setUniform("PhysicsLiquid", liquidUniform);
            pass.setPipeline(PhysicsShaders.PHYSICS_LIQUID_DATA_PIPELINE);
            this.instancedRenderer.render(pass, "PhysicsLiquidInstances");
         } catch (Throwable var10) {
            if (pass != null) {
               try {
                  pass.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (pass != null) {
            pass.close();
         }

         if (this.liquidBlurScratchTarget != null) {
            this.blurEffect.render(this.mainRenderer.projectionMatrix, physics.fluidParticleSize * 6.0F, this.liquidDataTarget, this.liquidBlurScratchTarget);
         }
      }
   }

   private void renderLiquidComposite(PhysicsWorld physics, Vec3 cameraPos) {
      if (this.liquidDataTarget != null && this.waterTextureView != null) {
         GpuBufferSlice liquidUniform = this.uploadLiquidUniform(physics, this.mainRenderer.viewMatrix, this.mainRenderer.projectionMatrix, cameraPos);
         GpuBufferSlice transformBuffer = RenderSystem.getDynamicUniforms()
            .writeTransforms(new Transform[]{MainRenderer.createTransformUniform(this.mainRenderer.viewMatrix)})[0];
         if (liquidUniform != null) {
            this.initShaderModStates(physics);
            RenderPass renderPass = this.bindLiquidsShader(() -> "Physics Mod Liquid Composite");
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("PhysicsLiquid", liquidUniform);
            renderPass.setUniform("DynamicTransforms", transformBuffer);
            renderPass.bindTexture("Sampler0", this.waterTextureView, MainRenderer.NEAREST_SAMPLER);
            renderPass.bindTexture(
               "Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)
            );
            renderPass.bindTexture("physics_liquidData", this.liquidDataTarget.colorView(), MainRenderer.NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
            renderPass.setVertexBuffer(0, this.fullscreenVertexBuffer.slice());
            renderPass.draw(3, 1, 0, 0);
            renderPass.close();
         }
      }
   }

   private void initShaderModStates(PhysicsWorld physics) {
      boolean iris = StarterClient.iris() && Iris.isExtending();
      boolean optifine = StarterClient.optifabric;
      if (iris || optifine) {
         int color = BiomeColors.getAverageWaterColor(physics.getLevel(), Minecraft.getInstance().player.blockPosition());
         GL32C.glVertexAttrib4f(Data.COLOR_SHADER.getAttribute(), Pack.getRed(color), Pack.getGreen(color), Pack.getBlue(color), 1.0F);
         GL32C.glVertexAttribI2i(Data.LIGHT_SHADER.getAttribute(), 240, 240);
         GL32C.glVertexAttrib2f(Data.TEX_COORD_SHADER.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
      }

      if (iris) {
         GL32C.glVertexAttrib3f(Data.NORMAL_SHADER.getAttribute(), 0.0F, 1.0F, 0.0F);
         int mcEntityLocation = 5;
         GL32C.glVertexAttrib2s(mcEntityLocation, Iris.getMaterialID(Blocks.WATER.defaultBlockState()), (short)1);
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_TERRAIN_SHADER.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         int mcMidBlockLocation = 8;
         GL32C.glVertexAttrib4Nub(mcMidBlockLocation, (byte)32, (byte)32, (byte)32, (byte)-1);
      } else if (optifine) {
         GL32C.glVertexAttrib3f(Data.NORMAL.getAttribute(), 0.0F, 1.0F, 0.0F);
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), this.waterMidCoord.x, this.waterMidCoord.y);
         GL32C.glVertexAttrib4f(Data.TANGENT_OPTIFINE.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
         if (Optifine.isUsingShadersNoInternal()) {
            int mcEntityLocation = 11;
            GL32C.glVertexAttrib4f(
               mcEntityLocation,
               (float)Optifine.getMaterialID(Blocks.WATER.defaultBlockState()),
               (float)Optifine.getRenderType(Blocks.WATER.defaultBlockState()),
               -1.0F,
               -1.0F
            );
            int mcMidBlockLocation = 15;
            GL32C.glVertexAttrib4Nub(mcMidBlockLocation, (byte)32, (byte)32, (byte)32, (byte)-1);
         }
      }
   }

   private GpuBuffer createFullscreenTriangle() {
      ByteBuffer data = MemoryUtil.memAlloc(36);

      GpuBuffer var2;
      try {
         data.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         data.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         data.putFloat(0.0F).putFloat(0.0F).putFloat(0.0F);
         data.flip();
         var2 = RenderSystem.getDevice().createBuffer(() -> "Physics Liquid Fullscreen Triangle", 40, data);
      } finally {
         MemoryUtil.memFree(data);
      }

      return var2;
   }

   private void renderLiquidShadow(PhysicsWorld physics, Matrix4fStack modelView, Vec3 cameraPos) {
      GpuBufferSlice liquidUniform = this.uploadLiquidUniform(
         physics, new Matrix4f(this.mainRenderer.viewMatrix), this.mainRenderer.projectionMatrix, cameraPos
      );
      GpuBufferSlice transformBuffer = RenderSystem.getDynamicUniforms()
         .writeTransforms(new Transform[]{MainRenderer.createTransformUniform(this.mainRenderer.viewMatrix)})[0];
      if (liquidUniform != null) {
         this.initShaderModStates(physics);
         RenderPass renderPass = this.bindLiquidsShader(() -> "Physics Mod Liquid Shadow");
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.setUniform("DynamicTransforms", transformBuffer);
         renderPass.bindTexture("Sampler0", this.waterTextureView, MainRenderer.NEAREST_SAMPLER);
         renderPass.bindTexture("Sampler2", Minecraft.getInstance().gameRenderer.lightmap(), RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR));
         renderPass.setUniform("PhysicsLiquid", liquidUniform);
         this.instancedRenderer.render(renderPass, "PhysicsLiquidInstances");
         renderPass.close();
      }
   }

   private RenderPass bindLiquidsShader(Supplier<String> label) {
      RenderPipeline renderPipeline = PhysicsShaders.PHYSICS_LIQUID_PIPELINE;
      GlRenderPipeline customPipeline = null;
      if (StarterClient.iris() && Iris.isExtending()) {
         if (Iris.isShadowPass()) {
            renderPipeline = PhysicsShaders.PHYSICS_LIQUID_SHADOW_PIPELINE;
            if (Iris.getLiquidShadowProgram() != null) {
               customPipeline = new GlRenderPipeline(renderPipeline, Iris.getLiquidShadowProgram());
            }
         } else if (Iris.getLiquidProgram() != null) {
            customPipeline = new GlRenderPipeline(renderPipeline, Iris.getLiquidProgram());
         }
      } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         if (Optifine.isShadowPass()) {
            if (!Optifine.useLiquidShadowShader()) {
               renderPipeline = RenderPipelines.SOLID_TERRAIN;
            }
         } else if (!Optifine.useLiquidShader()) {
            renderPipeline = RenderPipelines.TRANSLUCENT_TERRAIN;
         }
      }

      Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.LEVEL);
      return this.mainRenderer.bindProperShader(label, renderPipeline, customPipeline);
   }

   private boolean supportsLiquidsRendering() {
      if (StarterClient.iris() && Iris.isExtending()) {
         return Iris.getLiquidProgram() != null;
      } else {
         return StarterClient.optifabric && Optifine.isUsingShadersNoInternal() ? Optifine.supportsLiquidShader() : true;
      }
   }

   private GpuTexture resolveSceneDepthTexture() {
      return StarterClient.optifabric && Optifine.isUsingShadersNoInternal()
         ? Optifine.getActiveDepthTexture()
         : Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTexture();
   }

   private void ensureRenderTargets(GpuTexture sceneDepthTexture) {
      int width = sceneDepthTexture.getWidth(0);
      int height = sceneDepthTexture.getHeight(0);
      if (this.liquidDataTarget == null) {
         this.liquidDataTarget = new SimpleColorDepthRenderTarget("Physics Liquid Data Target", LIQUID_DATA_FORMAT);
      }

      if (this.liquidBlurScratchTarget == null) {
         this.liquidBlurScratchTarget = new SimpleColorRenderTarget("Physics Liquid Blur Scratch Target", LIQUID_DATA_FORMAT);
      }

      this.liquidDataTarget.ensureSize(width, height, sceneDepthTexture.getFormat());
      this.liquidBlurScratchTarget.ensureSize(width, height);
   }

   @Nullable
   private GpuBufferSlice uploadLiquidUniform(PhysicsWorld physics, Matrix4f modelView, Matrix4f projectionMatrix, Vec3 cameraPos) {
      Vector3d physicsOffset = physics.getOffset();
      float cameraOffsetX = (float)(cameraPos.x - physicsOffset.x);
      float cameraOffsetY = (float)(cameraPos.y - physicsOffset.y);
      float cameraOffsetZ = (float)(cameraPos.z - physicsOffset.z);
      boolean zZeroToOne = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
      LiquidUniform liquidUniform = new LiquidUniform(
         new Matrix4f(modelView),
         new Matrix4f(projectionMatrix),
         new Matrix4f(projectionMatrix).invert(),
         new Matrix4f(this.mainRenderer.viewMatrix).invert(),
         new Matrix4f(this.mainRenderer.viewMatrix),
         cameraOffsetX,
         cameraOffsetY,
         cameraOffsetZ,
         (float)physics.getRenderPercent(),
         this.waterBounds.x,
         this.waterBounds.y,
         this.waterBounds.z,
         this.waterBounds.w,
         cameraOffsetX,
         cameraOffsetY,
         cameraOffsetZ,
         0.0F,
         zZeroToOne ? 1 : 0
      );
      DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
      GpuBufferSlice[] slices = dynamicUniforms.physicsmod$writeLiquidUniforms(liquidUniform);
      return slices.length > 0 ? slices[0] : null;
   }

   public void updateLiquidInstances(PhysicsWorld physics) {
      if (this.instancedRenderer != null) {
         int size = 0;

         for (Liquid liquid : physics.getLiquids()) {
            size += liquid.particleCount();
         }

         this.instancedRenderer.writeInstanceData(new LiquidDeferredRenderer.LiquidVertexWriter(physics), size);
      }
   }

   public void destroy() {
      if (this.liquidDataTarget != null) {
         this.liquidDataTarget.destroy();
         this.liquidDataTarget = null;
      }

      if (this.liquidBlurScratchTarget != null) {
         this.liquidBlurScratchTarget.destroy();
         this.liquidBlurScratchTarget = null;
      }

      this.blurEffect.destroy();
      this.blurEffect = null;
      this.instancedRenderer.destroy();
      this.instancedRenderer = null;
      this.fullscreenVertexBuffer.close();
   }

   private static final class LiquidMeshVertexWriter implements VertexWriter {
      private final Mesh mesh;

      private LiquidMeshVertexWriter(Mesh mesh) {
         this.mesh = mesh;
      }

      @Override
      public void write(ByteBuffer buffer) {
         long address = MemoryUtil.memAddress(buffer);

         for (int i = 0; i < this.count(); i++) {
            int index = this.mesh.indices.getInt(i);
            Vector3f p = this.mesh.positions.get(index);
            MemoryUtil.memPutFloat(address, p.x);
            MemoryUtil.memPutFloat(address + 4L, p.y);
            MemoryUtil.memPutFloat(address + 8L, p.z);
            address += (long)PhysicsShaders.LIQUID_FORMAT.getVertexSize();
         }
      }

      @Override
      public int count() {
         return this.mesh.indices.size();
      }
   }

   private static final class LiquidVertexWriter implements VertexWriter {
      private final PhysicsWorld physics;
      private int instanceCount;

      private LiquidVertexWriter(PhysicsWorld physics) {
         this.physics = physics;
      }

      @Override
      public void write(ByteBuffer buffer) {
         this.instanceCount = 0;
         long address = MemoryUtil.memAddress(buffer);

         for (Liquid liquid : this.physics.getLiquids()) {
            int count = liquid.fillInstances(this.physics, address);
            this.instanceCount += count;
            address += (long)count * (long)PhysicsShaders.LIQUID_INSTANCE_FORMAT.getVertexSize();
         }
      }

      @Override
      public int count() {
         return this.instanceCount;
      }
   }
}
