package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import net.diebuddies.compat.Iris;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.mixins.GpuDeviceBackendAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.smoke.SmokeDomain;
import net.diebuddies.render.util.PhysicsRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

public class RayMarcherEffect {
   private static final int GRID_X = 96;
   private static final int GRID_Y = 96;
   private static final int GRID_Z = 96;
   private static final int BRICK = 16;
   private static final int MAX_CASCADES = 4;
   private static final int PARTICLE_STRIDE = 32;
   private static final float CASCADE_BLEND = 0.08F;
   private static final float HISTORY_BLEND = 0.9F;
   private static final float DEPTH_BILATERAL_Z = 0.01F;
   private static final Vector3f BASE_VOLUME_SIZE = new Vector3f(24.0F, 24.0F, 24.0F);
   private static final int VOLUME_UBO_SIZE = new Std140SizeCalculator()
      .putMat4f()
      .putMat4f()
      .putIVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putVec4()
      .putIVec4()
      .putIVec4()
      .putIVec4()
      .putIVec4()
      .putInt()
      .putInt()
      .get();
   private final GpuDevice device;
   private final SmokeVolumeBackend backend;
   private final boolean supported;
   private final int cascadeCount;
   private final int occX;
   private final int occY;
   private final int occZ;
   private final int occPx;
   private final int occPy;
   private final int occPz;
   private int downsampleFactor;
   private final Vector3f[] volumeSize = new Vector3f[4];
   private final Vector3f[] volumeHalf = new Vector3f[4];
   private final Vector3f[] boundsMin = new Vector3f[4];
   private final Vector3f[] boundsMax = new Vector3f[4];
   private final float[] stepBase = new float[4];
   private GpuBuffer fullscreenVertexBuffer;
   private GpuBuffer frameUniformBuffer;
   private ByteBuffer particleUploadScratch;
   private int particleUploadCapacity;
   private SmokeVolumeTargets volumeTargets;
   private GpuSampler linearSampler;
   private GpuSampler nearestSampler;
   private GpuSampler volumeLinearSampler;
   private GpuSampler volumeNearestSampler;
   private GpuTexture smokeLowTex;
   private GpuTextureView smokeLowView;
   private GpuTexture compositeTex;
   private GpuTextureView compositeView;
   private final GpuTexture[] historySmokeTex = new GpuTexture[2];
   private final GpuTextureView[] historySmokeView = new GpuTextureView[2];
   private final GpuTexture[] historyDepthTex = new GpuTexture[2];
   private final GpuTextureView[] historyDepthView = new GpuTextureView[2];
   private int fullW = -1;
   private int fullH = -1;
   private int lowW = -1;
   private int lowH = -1;
   private int historyRead = 0;
   private boolean hasHistory = false;
   private final Matrix4f prevViewProj = new Matrix4f();

   public RayMarcherEffect(int cascadeCount, int downsampleFactor) {
      this.device = RenderSystem.getDevice();
      this.cascadeCount = Math.max(1, Math.min(4, cascadeCount));
      this.downsampleFactor = Math.max(1, downsampleFactor);
      this.occX = 6;
      this.occY = 6;
      this.occZ = 6;
      this.occPx = this.occX + 1 >> 1;
      this.occPy = this.occY + 1 >> 1;
      this.occPz = this.occZ + 1 >> 1;
      this.backend = (SmokeVolumeBackend)(((GpuDeviceBackendAccessor)this.device).physicsmod$getBackend() instanceof VulkanDevice vk
         ? new VulkanSmokeVolumeBackend(this.device, vk)
         : new GlSmokeVolumeBackend(this.device));
      this.supported = this.backend.isSupported();

      for (int i = 0; i < 4; i++) {
         this.boundsMin[i] = new Vector3f();
         this.boundsMax[i] = new Vector3f();
         this.volumeSize[i] = new Vector3f(BASE_VOLUME_SIZE).mul((float)(1 << i));
         this.volumeHalf[i] = new Vector3f(this.volumeSize[i]).mul(0.5F);
         float cellX = this.volumeSize[i].x / 96.0F;
         float cellY = this.volumeSize[i].y / 96.0F;
         float cellZ = this.volumeSize[i].z / 96.0F;
         this.stepBase[i] = Math.min(cellX, Math.min(cellY, cellZ));
      }

      if (this.supported) {
         this.createBuffers();
         this.createSamplers();
      }
   }

   public boolean isSupported() {
      return this.supported;
   }

   private boolean usesCompositePass() {
      return this.downsampleFactor > 1;
   }

   private void createBuffers() {
      this.fullscreenVertexBuffer = this.createFullscreenTriangle();
      this.frameUniformBuffer = this.device.createBuffer(() -> "Physics Smoke Frame UBO", 136, (long)VOLUME_UBO_SIZE);
      this.particleUploadCapacity = 8192;
      this.particleUploadScratch = MemoryUtil.memAlloc(this.particleUploadCapacity);
      GpuBuffer cascadeMeta = this.device.createBuffer(() -> "Physics Smoke Cascade Meta", 264, 128L);
      GpuTexture[] densityTextures = new GpuTexture[4];
      GpuTextureView[] densityViews = new GpuTextureView[4];
      GpuTexture[] densityAccTextures = new GpuTexture[4];
      GpuTextureView[] densityAccViews = new GpuTextureView[4];
      GpuTexture[] occupancyTextures = new GpuTexture[4];
      GpuTextureView[] occupancyViews = new GpuTextureView[4];
      GpuTexture[] lightTextures = new GpuTexture[4];
      GpuTextureView[] lightViews = new GpuTextureView[4];
      GpuTexture[] lightAccTextures = new GpuTexture[4];
      GpuTextureView[] lightAccViews = new GpuTextureView[4];
      int sampledUsage = 132;
      int storageUsage = 389;

      for (int i = 0; i < 4; i++) {
         densityTextures[i] = PhysicsRenderUtil.createTexture3D("Physics Smoke Density " + i, storageUsage, GpuFormat.R8_UNORM, 96, 96, 96, 1);
         densityViews[i] = this.device.createTextureView(densityTextures[i]);
         densityAccTextures[i] = PhysicsRenderUtil.createTexture3D("Physics Smoke Density Acc " + i, storageUsage, GpuFormat.R32_UINT, 96, 96, 96, 1);
         densityAccViews[i] = this.device.createTextureView(densityAccTextures[i]);
         occupancyTextures[i] = PhysicsRenderUtil.createTexture3D(
            "Physics Smoke Occupancy " + i, storageUsage, GpuFormat.R32_UINT, this.occPx, this.occPy, this.occPz, 1
         );
         occupancyViews[i] = this.device.createTextureView(occupancyTextures[i]);
         lightTextures[i] = PhysicsRenderUtil.createTexture3D("Physics Smoke Light " + i, storageUsage, GpuFormat.RGBA8_UNORM, 96, 96, 96, 1);
         lightViews[i] = this.device.createTextureView(lightTextures[i]);
         lightAccTextures[i] = PhysicsRenderUtil.createTexture3D("Physics Smoke Light Acc " + i, storageUsage, GpuFormat.R32_UINT, 96, 96, 96, 1);
         lightAccViews[i] = this.device.createTextureView(lightAccTextures[i]);
      }

      this.volumeTargets = new SmokeVolumeTargets(
         cascadeMeta,
         densityTextures,
         densityViews,
         densityAccTextures,
         densityAccViews,
         occupancyTextures,
         occupancyViews,
         lightTextures,
         lightViews,
         lightAccTextures,
         lightAccViews
      );
   }

   private void createSamplers() {
      this.linearSampler = this.device
         .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty());
      this.nearestSampler = this.device
         .createSampler(AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty());
      this.volumeLinearSampler = PhysicsRenderUtil.createSampler3D(
         AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty()
      );
      this.volumeNearestSampler = PhysicsRenderUtil.createSampler3D(
         AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.NEAREST, FilterMode.NEAREST, 1, OptionalDouble.empty()
      );
   }

   private GpuBuffer createFullscreenTriangle() {
      ByteBuffer data = MemoryUtil.memAlloc(36);

      GpuBuffer var2;
      try {
         data.putFloat(-1.0F).putFloat(-1.0F).putFloat(0.0F);
         data.putFloat(3.0F).putFloat(-1.0F).putFloat(0.0F);
         data.putFloat(-1.0F).putFloat(3.0F).putFloat(0.0F);
         data.flip();
         var2 = this.device.createBuffer(() -> "Physics Smoke Fullscreen Triangle", 40, data);
      } finally {
         MemoryUtil.memFree(data);
      }

      return var2;
   }

   public void setDownsampleFactor(int downsampleFactor) {
      this.downsampleFactor = Math.max(1, downsampleFactor);
   }

   public void render(SmokeDomain smokeDomain, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
      if (this.supported && smokeDomain != null && smokeDomain.particleCount() > 0) {
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         Vector3d physicsOffset = smokeDomain.getWorld().getOffset();
         float camX = (float)(camera.position().x - physicsOffset.x);
         float camY = (float)(camera.position().y - physicsOffset.y);
         float camZ = (float)(camera.position().z - physicsOffset.z);
         RenderTarget target = Minecraft.getInstance().gameRenderer.mainRenderTarget();
         this.ensureTargets(target.width, target.height);
         Matrix4f view = new Matrix4f(viewMatrix).translate(-camX, -camY, -camZ);
         Matrix4f viewProj = new Matrix4f(projectionMatrix).mul(view);
         Matrix4f invViewProj = new Matrix4f(viewProj).invert();
         this.updateBoundsFromCamera(camX, camY, camZ);
         ByteBuffer particleData = this.gatherParticleData(smokeDomain);
         if (particleData == null) {
            this.backend.resetFrameState();
            this.hasHistory = false;
         } else {
            int particleCount = particleData.remaining() / 32;
            this.updateFrameUniform(invViewProj, particleCount, camX, camY, camZ);
            GpuTexture lightmapTexture = Minecraft.getInstance().gameRenderer.lightmap().texture();
            GpuTextureView lightmapView = this.device.createTextureView(lightmapTexture);

            try {
               this.backend
                  .buildVolumes(
                     particleData,
                     particleCount,
                     this.volumeTargets,
                     lightmapTexture,
                     lightmapView,
                     this.cascadeCount,
                     this.boundsMin,
                     this.volumeSize,
                     96,
                     96,
                     96
                  );
            } finally {
               lightmapView.close();
            }

            CommandEncoder encoder = this.device.createCommandEncoder();
            this.renderRaymarch(encoder, target);
            if (this.usesCompositePass()) {
               this.renderComposite(encoder, target);
               this.blendToMainTarget(encoder, this.compositeView);
               this.writeHistory(encoder, target);
               this.historyRead = 1 - this.historyRead;
               this.hasHistory = true;
            } else {
               this.blendToMainTarget(encoder, this.smokeLowView);
               this.hasHistory = false;
            }

            this.prevViewProj.set(viewProj);
         }
      } else {
         this.backend.resetFrameState();
         this.hasHistory = false;
      }
   }

   private ByteBuffer gatherParticleData(SmokeDomain smokeDomain) {
      int count = Math.max(0, smokeDomain.particleCount());
      int required = (int)Math.max(32L, (long)count * 32L);
      if (required > this.particleUploadCapacity) {
         if (this.particleUploadScratch != null) {
            MemoryUtil.memFree(this.particleUploadScratch);
         }

         this.particleUploadCapacity = Math.max(required, Math.max(8192, this.particleUploadCapacity * 2));
         this.particleUploadScratch = MemoryUtil.memAlloc(this.particleUploadCapacity);
      }

      this.particleUploadScratch.clear();
      this.particleUploadScratch.position(0);
      int written = smokeDomain.fillVolume(MemoryUtil.memAddress(this.particleUploadScratch));
      if (written <= 0) {
         this.particleUploadScratch.limit(this.particleUploadScratch.capacity());
         return null;
      } else {
         this.particleUploadScratch.limit(written * 32);
         this.particleUploadScratch.position(0);
         return this.particleUploadScratch;
      }
   }

   private void updateFrameUniform(Matrix4f invViewProj, int particleCount, float camX, float camY, float camZ) {
      ByteBuffer buffer = MemoryUtil.memAlloc(VOLUME_UBO_SIZE);

      try {
         Std140Builder builder = Std140Builder.intoBuffer(buffer)
            .putMat4f(invViewProj)
            .putMat4f(this.prevViewProj)
            .putIVec4(this.cascadeCount, particleCount, 884736, this.occPx * this.occPy * this.occPz)
            .putVec4(96.0F, 96.0F, 96.0F, 0.08F)
            .putVec4(Math.max(1.0F, ConfigClient.smokeDensity * 10.0F), 0.75F, 0.005F, 0.9F)
            .putVec4(0.35F, ConfigClient.smokeVolumeShadowStrength, 1.0F, 0.01F)
            .putVec4(1.0F, 1.0F, 1.0F, 1.0F)
            .putVec4(0.4F, -0.5F, 0.2F, 1.0F)
            .putVec4(1.1F, 1.1F, 1.1F, 0.9F)
            .putVec4(0.78F, 0.78F, 0.78F, 0.4F)
            .putVec4(camX, camY, camZ, 0.0F);

         for (int i = 0; i < 4; i++) {
            builder.putVec4(this.volumeSize[i].x, this.volumeSize[i].y, this.volumeSize[i].z, this.stepBase[i]);
         }

         for (int i = 0; i < 4; i++) {
            builder.putVec4(this.boundsMin[i].x, this.boundsMin[i].y, this.boundsMin[i].z, 0.0F);
         }

         for (int i = 0; i < 4; i++) {
            builder.putVec4(this.boundsMax[i].x, this.boundsMax[i].y, this.boundsMax[i].z, 0.0F);
         }

         for (int i = 0; i < 4; i++) {
            builder.putIVec4(0, 0, 0, 0);
         }

         boolean zZeroToOne = RenderSystem.getDevice().getDeviceInfo().isZZeroToOne();
         boolean reverseZ = !StarterClient.iris() || Iris.isUsingReverseZ();
         builder.putInt(zZeroToOne ? 1 : 0);
         builder.putInt(reverseZ ? 1 : 0);
         ByteBuffer out = builder.get();
         this.device.createCommandEncoder().writeToBuffer(this.frameUniformBuffer.slice(0L, (long)out.remaining()), out);
      } finally {
         MemoryUtil.memFree(buffer);
      }
   }

   private void updateBoundsFromCamera(float camX, float camY, float camZ) {
      float vx0 = this.volumeSize[0].x / 96.0F;
      float vy0 = this.volumeSize[0].y / 96.0F;
      float vz0 = this.volumeSize[0].z / 96.0F;
      float min0x = (float)Math.floor((double)((camX - this.volumeHalf[0].x) / vx0)) * vx0;
      float min0y = (float)Math.floor((double)((camY - this.volumeHalf[0].y) / vy0)) * vy0;
      float min0z = (float)Math.floor((double)((camZ - this.volumeHalf[0].z) / vz0)) * vz0;
      this.boundsMin[0].set(min0x, min0y, min0z);
      this.boundsMax[0].set(this.boundsMin[0]).add(this.volumeSize[0]);

      for (int c = 1; c < this.cascadeCount; c++) {
         float vx = this.volumeSize[c].x / 96.0F;
         float vy = this.volumeSize[c].y / 96.0F;
         float vz = this.volumeSize[c].z / 96.0F;
         float minx = (float)Math.floor((double)((camX - this.volumeHalf[c].x) / vx)) * vx;
         float miny = (float)Math.floor((double)((camY - this.volumeHalf[c].y) / vy)) * vy;
         float minz = (float)Math.floor((double)((camZ - this.volumeHalf[c].z) / vz)) * vz;
         float maxx = minx + this.volumeSize[c].x;
         float maxy = miny + this.volumeSize[c].y;
         float maxz = minz + this.volumeSize[c].z;
         if (this.boundsMin[c - 1].x < minx) {
            minx -= vx;
            maxx -= vx;
         }

         if (this.boundsMax[c - 1].x > maxx) {
            minx += vx;
            maxx += vx;
         }

         if (this.boundsMin[c - 1].y < miny) {
            miny -= vy;
            maxy -= vy;
         }

         if (this.boundsMax[c - 1].y > maxy) {
            miny += vy;
            maxy += vy;
         }

         if (this.boundsMin[c - 1].z < minz) {
            minz -= vz;
            maxz -= vz;
         }

         if (this.boundsMax[c - 1].z > maxz) {
            minz += vz;
            maxz += vz;
         }

         this.boundsMin[c].set(minx, miny, minz);
         this.boundsMax[c].set(maxx, maxy, maxz);
      }
   }

   private void ensureTargets(int renderW, int renderH) {
      int desiredLowW = Math.max(1, renderW / this.downsampleFactor);
      int desiredLowH = Math.max(1, renderH / this.downsampleFactor);
      if (renderW != this.fullW || renderH != this.fullH || desiredLowW != this.lowW || desiredLowH != this.lowH) {
         this.destroyTargets();
         this.fullW = renderW;
         this.fullH = renderH;
         this.lowW = desiredLowW;
         this.lowH = desiredLowH;
         int usage = 15;
         this.smokeLowTex = this.device.createTexture("Physics Smoke Low", usage, GpuFormat.RGBA16_FLOAT, this.lowW, this.lowH, 1, 1);
         this.smokeLowView = this.device.createTextureView(this.smokeLowTex);
         if (!this.usesCompositePass()) {
            this.compositeTex = null;
            this.compositeView = null;
            this.hasHistory = false;
         } else {
            this.compositeTex = this.device.createTexture("Physics Smoke Composite", usage, GpuFormat.RGBA16_FLOAT, this.fullW, this.fullH, 1, 1);
            this.compositeView = this.device.createTextureView(this.compositeTex);

            for (int i = 0; i < 2; i++) {
               this.historySmokeTex[i] = this.device.createTexture("Physics Smoke History " + i, usage, GpuFormat.RGBA16_FLOAT, this.fullW, this.fullH, 1, 1);
               this.historySmokeView[i] = this.device.createTextureView(this.historySmokeTex[i]);
               this.historyDepthTex[i] = this.device
                  .createTexture("Physics Smoke History Depth " + i, usage, GpuFormat.R16_FLOAT, this.fullW, this.fullH, 1, 1);
               this.historyDepthView[i] = this.device.createTextureView(this.historyDepthTex[i]);
            }
         }
      }
   }

   private void renderRaymarch(CommandEncoder encoder, RenderTarget target) {
      RenderPass pass = encoder.createRenderPass(() -> "Physics Smoke Raymarch", this.smokeLowView, Optional.of(new Vector4f(0.0F)));

      try {
         pass.setPipeline(SmokeVolumePipelines.RAYMARCH);
         RenderSystem.bindDefaultUniforms(pass);
         pass.setUniform("PhysicsSmokeVolumeFrame", this.frameUniformBuffer);
         pass.setUniform("PhysicsSmokeMeta", this.volumeTargets.cascadeMetaBuffer);
         pass.bindTexture("physics_depth", target.getDepthTextureView(), this.nearestSampler);
         pass.bindTexture("physics_density0", this.volumeTargets.densityViews[0], this.volumeLinearSampler);
         pass.bindTexture("physics_density1", this.volumeTargets.densityViews[1], this.volumeLinearSampler);
         pass.bindTexture("physics_density2", this.volumeTargets.densityViews[2], this.volumeLinearSampler);
         pass.bindTexture("physics_density3", this.volumeTargets.densityViews[3], this.volumeLinearSampler);
         pass.bindTexture("physics_occupancy0", this.volumeTargets.occupancyViews[0], this.volumeNearestSampler);
         pass.bindTexture("physics_occupancy1", this.volumeTargets.occupancyViews[1], this.volumeNearestSampler);
         pass.bindTexture("physics_occupancy2", this.volumeTargets.occupancyViews[2], this.volumeNearestSampler);
         pass.bindTexture("physics_occupancy3", this.volumeTargets.occupancyViews[3], this.volumeNearestSampler);
         pass.bindTexture("physics_light0", this.volumeTargets.lightViews[0], this.volumeLinearSampler);
         pass.bindTexture("physics_light1", this.volumeTargets.lightViews[1], this.volumeLinearSampler);
         pass.bindTexture("physics_light2", this.volumeTargets.lightViews[2], this.volumeLinearSampler);
         pass.bindTexture("physics_light3", this.volumeTargets.lightViews[3], this.volumeLinearSampler);
         pass.setVertexBuffer(0, this.fullscreenVertexBuffer.slice());
         pass.draw(3, 1, 0, 0);
      } catch (Throwable var7) {
         if (pass != null) {
            try {
               pass.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (pass != null) {
         pass.close();
      }
   }

   private void renderComposite(CommandEncoder encoder, RenderTarget target) {
      int readIdx = this.historyRead;
      RenderPass pass = encoder.createRenderPass(() -> "Physics Smoke Composite", this.compositeView, Optional.of(new Vector4f(0.0F)));

      try {
         pass.setPipeline(SmokeVolumePipelines.COMPOSITE);
         pass.setUniform("PhysicsSmokeVolumeFrame", this.frameUniformBuffer);
         pass.bindTexture("physics_sceneColor", target.getColorTextureView(), this.linearSampler);
         pass.bindTexture("physics_sceneDepth", target.getDepthTextureView(), this.nearestSampler);
         pass.bindTexture("physics_smokeLow", this.smokeLowView, this.linearSampler);
         pass.bindTexture("physics_historySmoke", this.hasHistory ? this.historySmokeView[readIdx] : this.smokeLowView, this.linearSampler);
         pass.bindTexture("physics_historyDepth", this.hasHistory ? this.historyDepthView[readIdx] : target.getDepthTextureView(), this.linearSampler);
         pass.setVertexBuffer(0, this.fullscreenVertexBuffer.slice());
         pass.draw(3, 1, 0, 0);
      } catch (Throwable var8) {
         if (pass != null) {
            try {
               pass.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (pass != null) {
         pass.close();
      }
   }

   private void blendToMainTarget(CommandEncoder encoder, GpuTextureView sourceView) {
      RenderPass pass = encoder.createRenderPass(
         () -> "Physics Smoke Blend",
         Minecraft.getInstance().gameRenderer.mainRenderTarget().getColorTextureView(),
         Optional.empty(),
         Minecraft.getInstance().gameRenderer.mainRenderTarget().getDepthTextureView(),
         OptionalDouble.empty()
      );

      try {
         pass.setPipeline(SmokeVolumePipelines.COPY_BLEND);
         pass.bindTexture("physics_texture", sourceView, this.linearSampler);
         pass.setVertexBuffer(0, this.fullscreenVertexBuffer.slice());
         pass.draw(3, 1, 0, 0);
      } catch (Throwable var7) {
         if (pass != null) {
            try {
               pass.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (pass != null) {
         pass.close();
      }
   }

   private void writeHistory(CommandEncoder encoder, RenderTarget target) {
      int writeIdx = 1 - this.historyRead;
      encoder.copyTextureToTexture(this.compositeTex, this.historySmokeTex[writeIdx], 0, 0, 0, 0, 0, this.fullW, this.fullH);
      RenderPass pass = encoder.createRenderPass(() -> "Physics Smoke Depth Copy", this.historyDepthView[writeIdx], Optional.of(new Vector4f(0.0F)));

      try {
         pass.setPipeline(SmokeVolumePipelines.DEPTH_COPY);
         pass.bindTexture("physics_texture", target.getDepthTextureView(), this.nearestSampler);
         pass.setVertexBuffer(0, this.fullscreenVertexBuffer.slice());
         pass.draw(3, 1, 0, 0);
      } catch (Throwable var8) {
         if (pass != null) {
            try {
               pass.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (pass != null) {
         pass.close();
      }
   }

   private void destroyTargets() {
      if (this.smokeLowView != null) {
         this.smokeLowView.close();
      }

      if (this.smokeLowTex != null) {
         this.smokeLowTex.close();
      }

      if (this.compositeView != null) {
         this.compositeView.close();
      }

      if (this.compositeTex != null) {
         this.compositeTex.close();
      }

      for (int i = 0; i < 2; i++) {
         if (this.historySmokeView[i] != null) {
            this.historySmokeView[i].close();
         }

         if (this.historySmokeTex[i] != null) {
            this.historySmokeTex[i].close();
         }

         if (this.historyDepthView[i] != null) {
            this.historyDepthView[i].close();
         }

         if (this.historyDepthTex[i] != null) {
            this.historyDepthTex[i].close();
         }

         this.historySmokeView[i] = null;
         this.historySmokeTex[i] = null;
         this.historyDepthView[i] = null;
         this.historyDepthTex[i] = null;
      }

      this.smokeLowView = null;
      this.smokeLowTex = null;
      this.compositeView = null;
      this.compositeTex = null;
   }

   public void close() {
      this.destroyTargets();
      if (this.volumeLinearSampler != null) {
         this.volumeLinearSampler.close();
      }

      if (this.volumeNearestSampler != null) {
         this.volumeNearestSampler.close();
      }

      if (this.linearSampler != null) {
         this.linearSampler.close();
      }

      if (this.nearestSampler != null) {
         this.nearestSampler.close();
      }

      if (this.fullscreenVertexBuffer != null) {
         this.fullscreenVertexBuffer.close();
      }

      if (this.frameUniformBuffer != null) {
         this.frameUniformBuffer.close();
      }

      if (this.particleUploadScratch != null) {
         MemoryUtil.memFree(this.particleUploadScratch);
         this.particleUploadScratch = null;
      }

      if (this.volumeTargets != null) {
         if (this.volumeTargets.cascadeMetaBuffer != null) {
            this.volumeTargets.cascadeMetaBuffer.close();
         }

         for (int i = 0; i < 4; i++) {
            if (this.volumeTargets.densityViews[i] != null) {
               this.volumeTargets.densityViews[i].close();
            }

            if (this.volumeTargets.densityTextures[i] != null) {
               this.volumeTargets.densityTextures[i].close();
            }

            if (this.volumeTargets.densityAccViews[i] != null) {
               this.volumeTargets.densityAccViews[i].close();
            }

            if (this.volumeTargets.densityAccTextures[i] != null) {
               this.volumeTargets.densityAccTextures[i].close();
            }

            if (this.volumeTargets.occupancyViews[i] != null) {
               this.volumeTargets.occupancyViews[i].close();
            }

            if (this.volumeTargets.occupancyTextures[i] != null) {
               this.volumeTargets.occupancyTextures[i].close();
            }

            if (this.volumeTargets.lightViews[i] != null) {
               this.volumeTargets.lightViews[i].close();
            }

            if (this.volumeTargets.lightTextures[i] != null) {
               this.volumeTargets.lightTextures[i].close();
            }

            if (this.volumeTargets.lightAccViews[i] != null) {
               this.volumeTargets.lightAccViews[i].close();
            }

            if (this.volumeTargets.lightAccTextures[i] != null) {
               this.volumeTargets.lightAccTextures[i].close();
            }
         }
      }

      this.backend.close();
   }
}
