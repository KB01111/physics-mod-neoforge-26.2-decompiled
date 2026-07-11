package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.opengl.GlBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import net.diebuddies.compat.Iris;
import net.diebuddies.mixins.GlBufferAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.smoke.SmokeColorConfig;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL13C;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL31C;

final class GlSmokeVolumeBackend extends AbstractSmokeVolumeBackend {
   private static final int META_UINTS = 8;
   private static final long MIN_PARTICLE_BYTES = 8192L;
   private static final int BRICK_SHIFT = 4;
   private static final int NOISE_SHIFT = 2;
   private static final float NOISE_MIN_MUL = 0.2F;
   private static final float NOISE_MAX_MUL = 0.8F;
   private final boolean supported;
   private final int clearDirtyProgram;
   private final int clear_uCascadeIndex;
   private final int clear_uGridSize;
   private final int splatProgram;
   private final int splat_uCascadeIndex;
   private final int splat_uParticleCount;
   private final int splat_uBoundsMin;
   private final int splat_uBoundsSize;
   private final int splat_uGridSize;
   private final int splat_uCellSize;
   private final int splat_uBrickShift;
   private final int splat_uNoiseShift;
   private final int splat_uNoiseMinMul;
   private final int splat_uNoiseMaxMul;
   private final int splat_uLightMap;
   private final int splat_uHasLightMap;
   private final int splat_uFireSmokeColor;
   private final int splat_uSteamSmokeColor;
   private final int resolveProgram;
   private final int resolve_uGridSize;
   private final int resolve_uCascadeIndex;
   private int particleSsbo;
   private long particleCapacity;
   private int metaSsbo;
   private long metaCapacity;

   GlSmokeVolumeBackend(GpuDevice device) {
      super(device);
      GLCompat.Features features = GLCompat.features();
      this.supported = features.textureStorage && features.supportsSmokeComputePipeline;
      this.particleSsbo = 0;
      this.particleCapacity = 0L;
      this.metaSsbo = 0;
      this.metaCapacity = 0L;
      if (!this.supported) {
         this.clearDirtyProgram = 0;
         this.splatProgram = 0;
         this.resolveProgram = 0;
         this.clear_uCascadeIndex = -1;
         this.clear_uGridSize = -1;
         this.splat_uCascadeIndex = -1;
         this.splat_uParticleCount = -1;
         this.splat_uBoundsMin = -1;
         this.splat_uBoundsSize = -1;
         this.splat_uGridSize = -1;
         this.splat_uCellSize = -1;
         this.splat_uBrickShift = -1;
         this.splat_uNoiseShift = -1;
         this.splat_uNoiseMinMul = -1;
         this.splat_uNoiseMaxMul = -1;
         this.splat_uLightMap = -1;
         this.splat_uHasLightMap = -1;
         this.splat_uFireSmokeColor = -1;
         this.splat_uSteamSmokeColor = -1;
         this.resolve_uGridSize = -1;
         this.resolve_uCascadeIndex = -1;
      } else {
         String header = GLCompat.buildComputeShaderHeader(true, true).trim();
         this.clearDirtyProgram = createComputeProgram(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_clear_dirty_gl.comp").replace("#GLSL_VERSION", header),
            "smoke_volume_clear_dirty_gl.comp"
         );
         GlStateManager._glUseProgram(this.clearDirtyProgram);
         this.clear_uCascadeIndex = GL20C.glGetUniformLocation(this.clearDirtyProgram, "uCascadeIndex");
         this.clear_uGridSize = GL20C.glGetUniformLocation(this.clearDirtyProgram, "uGridSize");
         this.splatProgram = createComputeProgram(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_splat_gl.comp").replace("#GLSL_VERSION", header), "smoke_volume_splat_gl.comp"
         );
         GlStateManager._glUseProgram(this.splatProgram);
         this.splat_uCascadeIndex = GL20C.glGetUniformLocation(this.splatProgram, "uCascadeIndex");
         this.splat_uParticleCount = GL20C.glGetUniformLocation(this.splatProgram, "uParticleCount");
         this.splat_uBoundsMin = GL20C.glGetUniformLocation(this.splatProgram, "uBoundsMin");
         this.splat_uBoundsSize = GL20C.glGetUniformLocation(this.splatProgram, "uBoundsSize");
         this.splat_uGridSize = GL20C.glGetUniformLocation(this.splatProgram, "uGridSize");
         this.splat_uCellSize = GL20C.glGetUniformLocation(this.splatProgram, "uCellSize");
         this.splat_uBrickShift = GL20C.glGetUniformLocation(this.splatProgram, "uBrickShift");
         this.splat_uNoiseShift = GL20C.glGetUniformLocation(this.splatProgram, "uNoiseShift");
         this.splat_uNoiseMinMul = GL20C.glGetUniformLocation(this.splatProgram, "uNoiseMinMul");
         this.splat_uNoiseMaxMul = GL20C.glGetUniformLocation(this.splatProgram, "uNoiseMaxMul");
         this.splat_uLightMap = GL20C.glGetUniformLocation(this.splatProgram, "uLightMap");
         this.splat_uHasLightMap = GL20C.glGetUniformLocation(this.splatProgram, "uHasLightMap");
         this.splat_uFireSmokeColor = GL20C.glGetUniformLocation(this.splatProgram, "uFireSmokeColor");
         this.splat_uSteamSmokeColor = GL20C.glGetUniformLocation(this.splatProgram, "uSteamSmokeColor");
         if (this.splat_uLightMap >= 0) {
            GL20C.glUniform1i(this.splat_uLightMap, 0);
         }

         this.resolveProgram = createComputeProgram(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_resolve_gl.comp").replace("#GLSL_VERSION", header), "smoke_volume_resolve_gl.comp"
         );
         GlStateManager._glUseProgram(this.resolveProgram);
         this.resolve_uGridSize = GL20C.glGetUniformLocation(this.resolveProgram, "uGridSize");
         this.resolve_uCascadeIndex = GL20C.glGetUniformLocation(this.resolveProgram, "uCascadeIndex");
         GlStateManager._glUseProgram(0);
      }
   }

   @Override
   public boolean isSupported() {
      return this.supported;
   }

   @Override
   public void buildVolumes(
      ByteBuffer particleData,
      int particleCount,
      SmokeVolumeTargets targets,
      GpuTexture lightmapTexture,
      GpuTextureView lightmapView,
      int cascadeCount,
      Vector3f[] boundsMin,
      Vector3f[] volumeSize,
      int gridX,
      int gridY,
      int gridZ
   ) {
      if (this.supported && particleCount > 0 && particleData != null) {
         this.ensureParticleBuffer(particleData.remaining());
         this.ensureMetaBuffer(targets.cascadeMetaBuffer.size());
         this.uploadParticleData(particleData);
         int publicMetaHandle = ((GlBufferAccessor)((GlBuffer)targets.cascadeMetaBuffer)).physicsmod$getHandle();
         GL30C.glBindBufferBase(37074, 0, this.particleSsbo);
         GL30C.glBindBufferBase(37074, 1, this.metaSsbo);
         if (!this.volumeDataInitialized) {
            this.clearAllVolumes(targets, cascadeCount, gridX, gridY, gridZ);
            this.clearMeta(this.metaSsbo, cascadeCount);
            this.volumeDataInitialized = true;
         } else {
            this.clearOccupancy(targets, cascadeCount, gridX, gridY, gridZ);

            for (int c = 0; c < cascadeCount; c++) {
               this.bindImage(0, targets.densityAccTextures[c], 35001, 33334);
               this.bindImage(2, targets.lightAccTextures[c], 35001, 33334);
               GlStateManager._glUseProgram(this.clearDirtyProgram);
               GL20C.glUniform1i(this.clear_uCascadeIndex, c);
               GL20C.glUniform3i(this.clear_uGridSize, gridX, gridY, gridZ);
               GLCompat.dispatchCompute((gridX + 7) / 8, (gridY + 7) / 8, (gridZ + 3) / 4);
            }

            GLCompat.memoryBarrier(8224);
            this.clearMeta(this.metaSsbo, cascadeCount);
         }

         boolean hasLightMap = lightmapTexture != null;
         if (hasLightMap) {
            GL13C.glActiveTexture(33984);
            GL11C.glBindTexture(3553, ((GlTexture)lightmapTexture).glId());
         }

         int groups = Math.max(1, (particleCount + 63) / 64);

         for (int c = 0; c < cascadeCount; c++) {
            Vector3f min = boundsMin[c];
            Vector3f size = volumeSize[c];
            float cellX = size.x / (float)gridX;
            float cellY = size.y / (float)gridY;
            float cellZ = size.z / (float)gridZ;
            this.bindImage(0, targets.densityAccTextures[c], 35002, 33334);
            this.bindImage(1, targets.occupancyTextures[c], 35002, 33334);
            this.bindImage(2, targets.lightAccTextures[c], 35002, 33334);
            GlStateManager._glUseProgram(this.splatProgram);
            GL20C.glUniform1i(this.splat_uCascadeIndex, c);
            GL20C.glUniform1i(this.splat_uParticleCount, particleCount);
            GL20C.glUniform3f(this.splat_uBoundsMin, min.x, min.y, min.z);
            GL20C.glUniform3f(this.splat_uBoundsSize, size.x, size.y, size.z);
            GL20C.glUniform3i(this.splat_uGridSize, gridX, gridY, gridZ);
            GL20C.glUniform3f(this.splat_uCellSize, cellX, cellY, cellZ);
            GL20C.glUniform1i(this.splat_uBrickShift, 4);
            GL20C.glUniform1i(this.splat_uNoiseShift, 2);
            GL20C.glUniform1f(this.splat_uNoiseMinMul, 0.2F);
            GL20C.glUniform1f(this.splat_uNoiseMaxMul, 0.8F);
            if (this.splat_uHasLightMap >= 0) {
               GL20C.glUniform1i(this.splat_uHasLightMap, hasLightMap ? 1 : 0);
            }

            if (this.splat_uFireSmokeColor >= 0) {
               GL20C.glUniform3f(
                  this.splat_uFireSmokeColor,
                  SmokeColorConfig.fireVolumeColorRed(),
                  SmokeColorConfig.fireVolumeColorGreen(),
                  SmokeColorConfig.fireVolumeColorBlue()
               );
            }

            if (this.splat_uSteamSmokeColor >= 0) {
               GL20C.glUniform3f(
                  this.splat_uSteamSmokeColor,
                  SmokeColorConfig.steamVolumeColorRed(),
                  SmokeColorConfig.steamVolumeColorGreen(),
                  SmokeColorConfig.steamVolumeColorBlue()
               );
            }

            GLCompat.dispatchCompute(groups, 1, 1);
         }

         GLCompat.memoryBarrier(8224);

         for (int c = 0; c < cascadeCount; c++) {
            this.bindImage(0, targets.densityAccTextures[c], 35000, 33334);
            this.bindImage(2, targets.lightAccTextures[c], 35000, 33334);
            this.bindImage(1, targets.densityTextures[c], 35001, 33321);
            this.bindImage(3, targets.lightTextures[c], 35001, 32856);
            GlStateManager._glUseProgram(this.resolveProgram);
            GL20C.glUniform3i(this.resolve_uGridSize, gridX, gridY, gridZ);
            GL20C.glUniform1i(this.resolve_uCascadeIndex, c);
            GLCompat.dispatchCompute((gridX + 7) / 8, (gridY + 7) / 8, (gridZ + 3) / 4);
         }

         GLCompat.memoryBarrier(8232);
         this.copyMetaToPublic(publicMetaHandle, targets.cascadeMetaBuffer.size());

         for (int i = 0; i <= 3; i++) {
            GLCompat.bindImageTexture(i, 0, 0, true, 0, 35000, 33334);
         }

         if (StarterClient.iris() && Iris.isExtending()) {
            GL30C.glBindBufferBase(37074, 0, Iris.ssboBindingIndex0);
            GL30C.glBindBufferBase(37074, 1, Iris.ssboBindingIndex1);
         } else {
            GL30C.glBindBufferBase(37074, 0, 0);
            GL30C.glBindBufferBase(37074, 1, 0);
         }

         GL15C.glBindBuffer(37074, 0);
         GlStateManager._glUseProgram(0);
      } else {
         this.volumeDataInitialized = false;
      }
   }

   private void ensureParticleBuffer(int requiredBytes) {
      long required = Math.max(8192L, (long)requiredBytes);
      if (this.particleSsbo == 0) {
         this.particleSsbo = GL15C.glGenBuffers();
      }

      if (required > this.particleCapacity) {
         this.particleCapacity = Math.max(required, Math.max(8192L, this.particleCapacity * 2L));
         GL15C.glBindBuffer(37074, this.particleSsbo);
         GL15C.glBufferData(37074, this.particleCapacity, 35040);
         GL15C.glBindBuffer(37074, 0);
      }
   }

   private void ensureMetaBuffer(long requiredBytes) {
      if (this.metaSsbo == 0) {
         this.metaSsbo = GL15C.glGenBuffers();
      }

      if (requiredBytes > this.metaCapacity) {
         this.metaCapacity = requiredBytes;
         GL15C.glBindBuffer(37074, this.metaSsbo);
         GL15C.glBufferData(37074, this.metaCapacity, 35050);
         GL15C.glBindBuffer(37074, 0);
         this.volumeDataInitialized = false;
      }
   }

   private void uploadParticleData(ByteBuffer particleData) {
      ByteBuffer src = particleData.duplicate();
      src.position(0);
      GL15C.glBindBuffer(37074, this.particleSsbo);
      GL15C.glBufferData(37074, this.particleCapacity, 35040);
      GL15C.glBufferSubData(37074, 0L, src);
      GL15C.glBindBuffer(37074, 0);
   }

   private void copyMetaToPublic(int publicMetaHandle, long byteCount) {
      GL15C.glBindBuffer(36662, this.metaSsbo);
      GL15C.glBindBuffer(36663, publicMetaHandle);
      GL31C.glCopyBufferSubData(36662, 36663, 0L, 0L, byteCount);
      GL15C.glBindBuffer(36662, 0);
      GL15C.glBindBuffer(36663, 0);
      GLCompat.memoryBarrier(8712);
   }

   private void bindImage(int unit, GpuTexture texture, int access, int format) {
      GLCompat.bindImageTexture(unit, ((GlTexture)texture).glId(), 0, true, 0, access, format);
   }

   private void clearAllVolumes(SmokeVolumeTargets targets, int cascadeCount, int gridX, int gridY, int gridZ) {
      for (int c = 0; c < cascadeCount; c++) {
         GLCompat.clearR32UITexture3D(((GlTexture)targets.densityAccTextures[c]).glId(), gridX, gridY, gridZ);
         GLCompat.clearR32UITexture3D(((GlTexture)targets.lightAccTextures[c]).glId(), gridX, gridY, gridZ);
         GLCompat.clearR32UITexture3D(((GlTexture)targets.occupancyTextures[c]).glId(), (gridX + 16 - 1) / 32, (gridY + 16 - 1) / 32, (gridZ + 16 - 1) / 32);
      }
   }

   private void clearOccupancy(SmokeVolumeTargets targets, int cascadeCount, int gridX, int gridY, int gridZ) {
      int occPx = (gridX + 32 - 1) / 32;
      int occPy = (gridY + 32 - 1) / 32;
      int occPz = (gridZ + 32 - 1) / 32;

      for (int c = 0; c < cascadeCount; c++) {
         GLCompat.clearR32UITexture3D(((GlTexture)targets.occupancyTextures[c]).glId(), occPx, occPy, occPz);
      }
   }

   private void clearMeta(int metaHandle, int cascadeCount) {
      GLCompat.clearR32UIBuffer(37074, metaHandle, cascadeCount * 8);
   }

   @Override
   public void close() {
      if (this.particleSsbo != 0) {
         GL15C.glDeleteBuffers(this.particleSsbo);
         this.particleSsbo = 0;
      }

      if (this.metaSsbo != 0) {
         GL15C.glDeleteBuffers(this.metaSsbo);
         this.metaSsbo = 0;
      }

      if (this.clearDirtyProgram != 0) {
         GL20C.glDeleteProgram(this.clearDirtyProgram);
      }

      if (this.splatProgram != 0) {
         GL20C.glDeleteProgram(this.splatProgram);
      }

      if (this.resolveProgram != 0) {
         GL20C.glDeleteProgram(this.resolveProgram);
      }
   }

   private static String load(String path) {
      try {
         return new String(GlSmokeVolumeBackend.class.getResourceAsStream(path).readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException var2) {
         throw new RuntimeException("Failed to load shader: " + path, var2);
      }
   }

   private static int createComputeProgram(String src, String debugName) {
      int shader = GL20C.glCreateShader(37305);
      GL20C.glShaderSource(shader, src);
      GL20C.glCompileShader(shader);
      if (GL20C.glGetShaderi(shader, 35713) == 0) {
         String log = GL20C.glGetShaderInfoLog(shader);
         GL20C.glDeleteShader(shader);
         throw new IllegalStateException("Compute shader compile failed (" + debugName + "):\n" + log);
      } else {
         int program = GL20C.glCreateProgram();
         GL20C.glAttachShader(program, shader);
         GL20C.glLinkProgram(program);
         GL20C.glDetachShader(program, shader);
         GL20C.glDeleteShader(shader);
         if (GL20C.glGetProgrami(program, 35714) == 0) {
            String log = GL20C.glGetProgramInfoLog(program);
            GL20C.glDeleteProgram(program);
            throw new IllegalStateException("Compute program link failed (" + debugName + "):\n" + log);
         } else {
            GL20C.glValidateProgram(program);
            return program;
         }
      }
   }
}
