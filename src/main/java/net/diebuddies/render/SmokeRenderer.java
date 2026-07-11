package net.diebuddies.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.opengl.Data;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.smoke.SmokeDomain;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.render.util.DynamicUniformsExtension;
import net.diebuddies.render.util.SmokeUniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;

public class SmokeRenderer {
   private final MainRenderer mainRenderer;
   @Nullable
   private GpuTexture smokeDepthCopy;
   @Nullable
   private GpuTextureView smokeDepthCopyView;
   @Nullable
   private InstancedRenderer instancedRenderer;
   private boolean needsSmokeUpdate = false;

   public SmokeRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.instancedRenderer = new InstancedRenderer(
         "Physics Smoke", PhysicsShaders.SMOKE_FORMAT, PhysicsShaders.SMOKE_INSTANCE_FORMAT, new SmokeRenderer.SmokeMeshVertexWriter(PhysicsMod.smoke)
      );
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack viewMatrixStack, Vec3 cameraPos) {
   }

   private void refreshDepthCopyView() {
      if (this.smokeDepthCopyView != null) {
         this.smokeDepthCopyView.close();
         this.smokeDepthCopyView = null;
      }

      if (this.smokeDepthCopy != null) {
         this.smokeDepthCopyView = RenderSystem.getDevice().createTextureView(this.smokeDepthCopy);
      }
   }

   private RenderPass bindSmokeShader() {
      RenderPipeline renderPipeline = PhysicsShaders.PHYSICS_SMOKE_PIPELINE;
      GlRenderPipeline customPipeline = null;
      if (StarterClient.iris() && Iris.isExtending()) {
         if (Iris.isShadowPass()) {
            if (Iris.getSmokeShadowProgram() == null) {
               return null;
            }

            customPipeline = new GlRenderPipeline(renderPipeline, Iris.getSmokeShadowProgram());
         } else {
            if (Iris.getSmokeProgram() == null) {
               return null;
            }

            customPipeline = new GlRenderPipeline(renderPipeline, Iris.getSmokeProgram());
         }
      } else if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         if (Optifine.isShadowPass()) {
            if (!Optifine.useSmokeShadowShader()) {
               return null;
            }
         } else if (!Optifine.useSmokeShader()) {
            return null;
         }
      } else if (StarterClient.optifabric) {
         renderPipeline = Optifine.getPhysicsVanillaSmokePipeline();
      }

      Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.LEVEL);
      return this.mainRenderer.bindProperShader(() -> "Physics Mod Smoke", renderPipeline, customPipeline);
   }

   private void setupSmokeRendering(RenderPass renderPass, GpuTextureView smokeTexture, GpuBufferSlice transformBuffer, GpuBufferSlice smokeUniformBuffer) {
      renderPass.setUniform("DynamicTransforms", transformBuffer);
      renderPass.setUniform("PhysicsSmoke", smokeUniformBuffer);
      renderPass.bindTexture("Sampler0", smokeTexture, MainRenderer.NEAREST_SAMPLER);
      renderPass.bindTexture("physics_smokeSampler", smokeTexture, MainRenderer.NEAREST_SAMPLER);
      renderPass.bindTexture("physics_depth", this.smokeDepthCopyView, MainRenderer.NEAREST_CLAMP_SAMPLER_NO_MIPMAP);
      renderPass.bindTexture("physics_lightmap", Minecraft.getInstance().gameRenderer.levelLightmap(), MainRenderer.LINEAR_CLAMP_SAMPLER);
      if (StarterClient.iris() && Iris.isExtending()) {
         GL33C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_TERRAIN_SHADER.getAttribute(), 0.5F, 0.5F);
         GL32C.glVertexAttrib4f(Data.TANGENT_TERRAIN_SHADER.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
      } else if (StarterClient.optifabric) {
         GL33C.glVertexAttribI2ui(Data.OVERLAY.getAttribute(), 0, 10);
         GL32C.glVertexAttrib2f(Data.MID_TEX_COORD_OPTIFINE.getAttribute(), 0.5F, 0.5F);
         GL32C.glVertexAttrib4f(Data.TANGENT_OPTIFINE.getAttribute(), 0.0F, 0.0F, 1.0F, 1.0F);
      }

      if (StarterClient.optifabric && Optifine.isUsingShadersNoInternal()) {
         Optifine.setDynamicTransforms(transformBuffer);
      }
   }

   @Nullable
   private GpuBufferSlice uploadSmokeUniform(PhysicsWorld physics, Vec3 cameraPos) {
      DynamicUniformsExtension dynamicUniforms = (DynamicUniformsExtension)RenderSystem.getDynamicUniforms();
      Vector3d physicsOffset = physics.getOffset();
      boolean zZeroToOne = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
      SmokeUniform smokeUniform = new SmokeUniform(
         new Matrix4f(this.mainRenderer.viewMatrix),
         new Matrix4f(this.mainRenderer.projectionMatrix),
         new Matrix4f(this.mainRenderer.projectionMatrix).invert(),
         ConfigClient.smokeColorRed,
         ConfigClient.smokeColorGreen,
         ConfigClient.smokeColorBlue,
         ConfigClient.smokeDensity,
         ConfigClient.smokeDenseColorRed,
         ConfigClient.smokeDenseColorGreen,
         ConfigClient.smokeDenseColorBlue,
         (float)physics.getRenderPercent(),
         (float)(cameraPos.x - physicsOffset.x),
         (float)(cameraPos.y - physicsOffset.y),
         (float)(cameraPos.z - physicsOffset.z),
         zZeroToOne ? 1 : 0
      );
      GpuBufferSlice[] slices = dynamicUniforms.physicsmod$writeSmokeUniforms(smokeUniform);
      return slices.length > 0 ? slices[0] : null;
   }

   private void updateSmokeInstances(SmokeDomain smokeDomain, Vec3 cameraPos) {
      this.instancedRenderer.writeInstanceData(new SmokeRenderer.SmokeVertexWriter(smokeDomain, cameraPos), smokeDomain.particleCount());
   }

   public void destroy() {
      if (this.smokeDepthCopyView != null) {
         this.smokeDepthCopyView.close();
         this.smokeDepthCopyView = null;
      }

      if (this.smokeDepthCopy != null) {
         this.smokeDepthCopy.close();
         this.smokeDepthCopy = null;
      }

      if (this.instancedRenderer != null) {
         this.instancedRenderer.destroy();
         this.instancedRenderer = null;
      }
   }

   private static final class SmokeMeshVertexWriter implements VertexWriter {
      private final Mesh mesh;

      private SmokeMeshVertexWriter(Mesh mesh) {
         this.mesh = mesh;
      }

      @Override
      public void write(ByteBuffer buffer) {
         long address = MemoryUtil.memAddress(buffer);

         for (int i = 0; i < this.count(); i++) {
            int index = this.mesh.indices.getInt(i);
            Vector3f p = this.mesh.positions.get(index);
            Vector2f uv = this.mesh.uvs.get(index);
            Vector3f normal = this.mesh.normals.get(index);
            MemoryUtil.memPutFloat(address, p.x);
            MemoryUtil.memPutFloat(address + 4L, p.y);
            MemoryUtil.memPutFloat(address + 8L, p.z);
            MemoryUtil.memPutInt(address + 12L, -1);
            MemoryUtil.memPutFloat(address + 16L, uv.x);
            MemoryUtil.memPutFloat(address + 20L, uv.y);
            MemoryUtil.memPutInt(address + 24L, Pack.normal(normal.x, normal.y, normal.z));
            address += (long)PhysicsShaders.SMOKE_FORMAT.getVertexSize();
         }
      }

      @Override
      public int count() {
         return this.mesh.indices.size();
      }
   }

   private static final class SmokeVertexWriter implements VertexWriter {
      private final SmokeDomain smokeDomain;
      private final Vec3 cameraPos;
      private int instanceCount;

      private SmokeVertexWriter(SmokeDomain smokeDomain, Vec3 cameraPos) {
         this.smokeDomain = smokeDomain;
         this.cameraPos = cameraPos;
      }

      @Override
      public void write(ByteBuffer buffer) {
         long address = MemoryUtil.memAddress(buffer);
         this.instanceCount = this.smokeDomain.fillInstances(this.cameraPos, address);
      }

      @Override
      public int count() {
         return this.instanceCount;
      }
   }
}
