package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.PhysicsIndex;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.NativeObject;
import physx.common.PxCudaContext;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxVec4;
import physx.particles.PxPBDMaterial;
import physx.particles.PxPBDParticleSystem;
import physx.particles.PxParticleBuffer;
import physx.particles.PxParticleBufferDesc;
import physx.particles.PxParticleBufferFlagEnum;
import physx.particles.PxParticlePhaseFlagEnum;
import physx.particles.PxParticlePhaseFlags;
import physx.physics.PxFilterData;

public class SmokeDomainCuda extends SmokeDomain {
   private static final float DESPAWN_ANIMATION_TIME = 2.0F;
   private static final float CHECK_AIR_EVERY_X_SECONDS = 1.0F;
   private static final MutableBlockPos tmpPos = new MutableBlockPos();
   private PxPBDParticleSystem smokeSystem;
   private PxPBDMaterial smokeMat;
   private int smokePhase;
   private int maxSmoke;
   private SmokeDomainCuda.ParticleInfo[] particles;
   private float[] cudaPositions;
   private float[] cudaOldPositions;
   private PxParticleBuffer particleBuffer;
   private FloatBuffer positionsBuffer;
   private FloatBuffer velocitiesBuffer;
   private IntBuffer phaseBuffer;
   private List<Vector4d> bufferedSpawns = new ObjectArrayList();
   private List<SmokeDomain.SmokeParticleStyle> bufferedSpawnStyles = new ObjectArrayList();
   private Random random;
   private int activeParticles;

   public SmokeDomainCuda(PhysicsWorld world) {
      super(world);
      this.maxSmoke = ConfigClient.smokeParticleLimitCuda;
      this.particles = new SmokeDomainCuda.ParticleInfo[this.maxSmoke];
      this.random = new Random(System.nanoTime());
   }

   @Override
   public void update(double diff) {
      if (!ConfigClient.smokePhysics) {
         this.remove();
         super.update(diff);
      } else {
         if (this.smokeSystem == null) {
            this.createSmokeSystem();
         }

         if (this.hasBufferedParticles() || this.hasParticles()) {
            PxCudaContextManager cudaMgr = StarterClient.cudaManager;
            cudaMgr.acquireContext();
            PxCudaContext cudaContext = cudaMgr.getCudaContext();
            this.fetchPositions(cudaContext, diff);
            this.processBufferedParticles(cudaContext);
            long positionsAddress = MemoryUtil.memAddress(this.positionsBuffer);
            long velocitiesAddress = MemoryUtil.memAddress(this.velocitiesBuffer);
            long phaseAddress = MemoryUtil.memAddress(this.phaseBuffer);
            cudaContext.memcpyHtoD(
               PxCudaTopLevelFunctions.pxVec4deviceptr(this.particleBuffer.getPositionInvMasses()),
               NativeObject.wrapPointer(positionsAddress),
               PxVec4.SIZEOF * this.activeParticles
            );
            cudaContext.memcpyHtoD(
               PxCudaTopLevelFunctions.pxVec4deviceptr(this.particleBuffer.getVelocities()),
               NativeObject.wrapPointer(velocitiesAddress),
               PxVec4.SIZEOF * this.activeParticles
            );
            cudaContext.memcpyHtoD(
               PxCudaTopLevelFunctions.pxU32deviceptr(this.particleBuffer.getPhases()), NativeObject.wrapPointer(phaseAddress), 4 * this.activeParticles
            );
            this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_POSITION);
            this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_VELOCITY);
            this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_PHASE);
            this.particleBuffer.setNbActiveParticles(this.activeParticles);
            cudaMgr.releaseContext();
         }

         super.update(diff);
      }
   }

   private void createSmokeSystem() {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         float r = 0.4F;
         this.smokeSystem = StarterClient.physics.createPBDParticleSystem(StarterClient.cudaManager, 96);
         float slop = r * 0.2F;
         this.smokeSystem.setRestOffset(r);
         this.smokeSystem.setContactOffset(r + slop);
         this.smokeSystem.setParticleContactOffset(r * 0.75F);
         this.smokeSystem.enableCCD(false);
         this.smokeSystem.setMaxVelocity(r * 20.0F);
         this.smokeSystem.setSolverIterationCounts(4, 2);
         PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 2, 23, 0, 0);
         this.smokeSystem.setSimulationFilterData(tmpFilterData);
         this.world.addParticleSystem(this.smokeSystem);
         this.smokeMat = StarterClient.physics.createPBDMaterial(0.0F, 0.08F, 0.0F, 0.0015F, 4.0F, 0.0F, 0.0F, 0.0F, 0.1F, 1.1F, -0.2F);
         PxParticlePhaseFlags flags = PxParticlePhaseFlags.createAt(mem, MemoryStack::nmalloc, 0);
         flags.raise(PxParticlePhaseFlagEnum.eParticlePhaseSelfCollide);
         this.smokePhase = this.smokeSystem.createPhase(this.smokeMat, flags);
         PxParticleBufferDesc bufferDesc = PxParticleBufferDesc.createAt(mem, MemoryStack::nmalloc);
         bufferDesc.setMaxParticles(this.maxSmoke);
         bufferDesc.setNumActiveParticles(0);
         this.positionsBuffer = MemoryUtil.memAllocFloat(this.maxSmoke * 4);
         this.velocitiesBuffer = MemoryUtil.memAllocFloat(this.maxSmoke * 4);
         this.phaseBuffer = MemoryUtil.memAllocInt(this.maxSmoke);
         this.cudaPositions = new float[this.maxSmoke * 4];
         this.cudaOldPositions = new float[this.maxSmoke * 4];
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         this.particleBuffer = PxCudaTopLevelFunctions.CreateAndPopulateParticleBuffer(bufferDesc, cudaMgr);
         this.smokeSystem.addParticleBuffer(this.particleBuffer);
      } catch (Throwable var10) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (mem != null) {
         mem.close();
      }
   }

   private boolean hasBufferedParticles() {
      return this.bufferedSpawns.size() != 0;
   }

   private boolean hasParticles() {
      return this.activeParticles != 0;
   }

   private void fetchPositions(PxCudaContext cudaContext, double diff) {
      if (this.hasParticles()) {
         long positionsAddress = MemoryUtil.memAddress(this.positionsBuffer);
         long velocitiesAddress = MemoryUtil.memAddress(this.velocitiesBuffer);
         long phaseAddress = MemoryUtil.memAddress(this.phaseBuffer);
         cudaContext.memcpyDtoH(
            NativeObject.wrapPointer(positionsAddress),
            PxCudaTopLevelFunctions.pxVec4deviceptr(this.particleBuffer.getPositionInvMasses()),
            PxVec4.SIZEOF * this.activeParticles
         );
         cudaContext.memcpyDtoH(
            NativeObject.wrapPointer(velocitiesAddress),
            PxCudaTopLevelFunctions.pxVec4deviceptr(this.particleBuffer.getVelocities()),
            PxVec4.SIZEOF * this.activeParticles
         );
         cudaContext.memcpyDtoH(
            NativeObject.wrapPointer(phaseAddress), PxCudaTopLevelFunctions.pxU32deviceptr(this.particleBuffer.getPhases()), 4 * this.activeParticles
         );
         System.arraycopy(this.cudaPositions, 0, this.cudaOldPositions, 0, this.activeParticles * 4);
         this.positionsBuffer.get(0, this.cudaPositions, 0, this.activeParticles * 4);
         Vector3d offset = this.world.getOffset();
         double maxSmokeDistance = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         Vec3 camPos = camera.position();

         for (int i = 0; i < this.activeParticles; i++) {
            SmokeDomainCuda.ParticleInfo particle = this.particles[i];
            int arrOffset = i * 4;
            float posX = this.cudaPositions[arrOffset];
            float posY = this.cudaPositions[arrOffset + 1];
            float posZ = this.cudaPositions[arrOffset + 2];
            particle.loadChunkPhysics(this.world, posX, posY, posZ);
            particle.despawnTime = (float)((double)particle.despawnTime - diff);
            particle.alive = (float)((double)particle.alive + diff);
            if (particle.touchedAir) {
               if (particle.despawnTime <= 0.0F) {
                  particle.unloadChunkPhysics(this.world);
                  this.removeParticleBuffer(i--);
                  continue;
               }
            } else if (particle.despawnTime <= 0.0F) {
               if (isInOpenAir(
                  this.world.getLevel(), Mth.floor((double)posX + offset.x), Mth.floor((double)posY + offset.y), Mth.floor((double)posZ + offset.z)
               )) {
                  particle.despawnTime = (float)Math.max(
                     2.0,
                     ConfigClient.particleDespawnTimeSmokeCuda + (double)net.diebuddies.math.Math.random() * ConfigClient.particleDespawnTimeVarianceSmokeCuda
                  );
                  particle.touchedAir = true;
               } else {
                  particle.despawnTime = 1.0F;
               }
            }

            if (camPos.distanceToSqr((double)posX + offset.x, (double)posY + offset.y, (double)posZ + offset.z) > maxSmokeDistance) {
               particle.unloadChunkPhysics(this.world);
               this.removeParticleBuffer(i--);
            }
         }
      }
   }

   private void removeParticleBuffer(int index) {
      this.activeParticles--;
      int currentPos = index * 4;
      int oldPos = this.activeParticles * 4;
      this.positionsBuffer.put(currentPos, this.positionsBuffer, oldPos, 4);
      this.velocitiesBuffer.put(currentPos, this.velocitiesBuffer, oldPos, 4);
      this.phaseBuffer.put(index, this.phaseBuffer, this.activeParticles, 1);
      this.particles[index] = this.particles[this.activeParticles];
      this.particles[this.activeParticles] = null;
      this.cudaPositions[currentPos] = this.cudaPositions[oldPos];
      this.cudaPositions[currentPos + 1] = this.cudaPositions[oldPos + 1];
      this.cudaPositions[currentPos + 2] = this.cudaPositions[oldPos + 2];
      this.cudaOldPositions[currentPos] = this.cudaOldPositions[oldPos];
      this.cudaOldPositions[currentPos + 1] = this.cudaOldPositions[oldPos + 1];
      this.cudaOldPositions[currentPos + 2] = this.cudaOldPositions[oldPos + 2];
   }

   @Override
   public void clearParticles() {
      PxCudaContextManager cudaMgr = StarterClient.cudaManager;
      cudaMgr.acquireContext();

      for (int i = 0; i < this.activeParticles; i++) {
         this.particles[i].unloadChunkPhysics(this.world);
      }

      this.activeParticles = 0;
      this.particleBuffer.setNbActiveParticles(this.activeParticles);
      cudaMgr.releaseContext();
   }

   @Override
   public void spawnParticle(double x, double y, double z, float scale, boolean fadeIn, SmokeDomain.SmokeParticleStyle style) {
      if (ConfigClient.smokePhysics) {
         this.bufferedSpawns
            .add(
               new Vector4d(
                  x + (double)this.random.nextFloat() * 0.1 - 0.05,
                  y + (double)this.random.nextFloat() * 0.1 - 0.05,
                  z + (double)this.random.nextFloat() * 0.1 - 0.05,
                  (double)scale
               )
            );
         this.bufferedSpawnStyles.add(style == null ? SmokeDomain.SmokeParticleStyle.FIRE : style);
      }
   }

   public void processBufferedParticles(PxCudaContext cudaContext) {
      if (this.hasBufferedParticles()) {
         Vector3d offset = this.world.getOffset();
         int needed = this.bufferedSpawns.size();
         int quickDespawn = needed - (this.maxSmoke - this.activeParticles);

         for (int i = 0; i < quickDespawn && this.activeParticles > 0; i++) {
            int index = this.random.nextInt(this.activeParticles);
            SmokeDomainCuda.ParticleInfo particle = this.particles[index];
            this.removeParticleBuffer(index);
            particle.unloadChunkPhysics(this.world);
         }

         int free = this.maxSmoke - this.activeParticles;
         if (free == 0) {
            this.bufferedSpawns.clear();
            this.bufferedSpawnStyles.clear();
         } else {
            for (int i = 0; i < this.bufferedSpawns.size() && i < free; i++) {
               int id = this.activeParticles;
               Vector4d bufferedSpawn = this.bufferedSpawns.get(i);
               SmokeDomain.SmokeParticleStyle spawnStyle = this.bufferedSpawnStyles.get(i);
               double x = bufferedSpawn.x;
               double y = bufferedSpawn.y;
               double z = bufferedSpawn.z;
               float scale = (float)bufferedSpawn.w;
               this.world.adjustOffset(x, y, z);
               x -= offset.x;
               y -= offset.y;
               z -= offset.z;
               int fillOld = id * 4;
               this.cudaPositions[fillOld] = (float)x;
               this.cudaPositions[fillOld + 1] = (float)y;
               this.cudaPositions[fillOld + 2] = (float)z;
               this.cudaOldPositions[fillOld] = (float)x;
               this.cudaOldPositions[fillOld + 1] = (float)y;
               this.cudaOldPositions[fillOld + 2] = (float)z;
               this.positionsBuffer.put(id * 4, new float[]{(float)x, (float)y, (float)z, 100.0F});
               this.velocitiesBuffer.put(id * 4, new float[4]);
               this.phaseBuffer.put(id, this.smokePhase);
               SmokeDomainCuda.ParticleInfo particle = new SmokeDomainCuda.ParticleInfo();
               particle.scale = scale;
               particle.id = SmokeDomain.encodeParticleId(spawnStyle, this.random.nextInt());
               this.particles[this.activeParticles] = particle;
               this.activeParticles++;
            }

            this.bufferedSpawns.clear();
            this.bufferedSpawnStyles.clear();
         }
      }
   }

   @Override
   public void executeExplosion(Explosion explosion) {
      MutableBlockPos blockPos = new MutableBlockPos();

      for (int i = 0; i < 300; i++) {
         double x = (double)net.diebuddies.math.Math.random() - 0.5;
         double y = (double)net.diebuddies.math.Math.random() - 0.5;
         double z = (double)net.diebuddies.math.Math.random() - 0.5;

         double vectorLength;
         for (vectorLength = Math.sqrt(x * x + y * y + z * z); vectorLength == 0.0; vectorLength = Math.sqrt(x * x + y * y + z * z)) {
            x = (double)net.diebuddies.math.Math.random() - 0.5;
            y = (double)net.diebuddies.math.Math.random() - 0.5;
            z = (double)net.diebuddies.math.Math.random() - 0.5;
         }

         x /= vectorLength;
         y /= vectorLength;
         z /= vectorLength;
         double length = (double)net.diebuddies.math.Math.random() * Math.max(1.0, (double)explosion.strength);
         x = x * length + explosion.position.x;
         y = y * length + explosion.position.y;
         z = z * length + explosion.position.z;
         blockPos.set(x, y, z);
         Level level = this.world.getLevel();
         BlockState state = level.getBlockState(blockPos);
         FluidState fluidState = state.getFluidState();
         if (fluidState.getAmount() == 0 && (!Block.isShapeFullBlock(state.getShape(level, blockPos)) || state.getCollisionShape(level, blockPos).isEmpty())) {
            this.spawnParticle(x, y, z, net.diebuddies.math.Math.random() * 2.5F + 1.0F, false);
         }
      }
   }

   @Override
   public int particleCount() {
      return this.activeParticles;
   }

   @Override
   public int fillInstances(Vec3 cameraPos, long address) {
      PhysicsWorld physics = this.getWorld();
      Vector3d physicsOffset = physics.getOffset();

      for (int i = 0; i < this.activeParticles; i++) {
         this.prepareSmokeInstances(physics, physics.getLevel(), cameraPos, i, physicsOffset.x, physicsOffset.y, physicsOffset.z, address);
         address += (long)PhysicsShaders.SMOKE_INSTANCE_FORMAT.getVertexSize();
      }

      return this.activeParticles;
   }

   private void prepareSmokeInstances(PhysicsWorld physics, Level level, Vec3 view, int particleIndex, double ox, double oy, double oz, long address) {
      SmokeDomainCuda.ParticleInfo particle = this.particles[particleIndex];
      int srcOffst = particleIndex * 4;
      float x = this.cudaPositions[srcOffst];
      float y = this.cudaPositions[srcOffst + 1];
      float z = this.cudaPositions[srcOffst + 2];
      int brightness = particle.getLight(level, tmpPos.set(ox + (double)x, oy + (double)y, oz + (double)z));
      int scale = (int)(net.diebuddies.math.Math.remapClamp((double)particle.scale, 0.25, 5.0, 0.0, 1.0) * 255.0);
      MemoryUtil.memPutFloat(address, (float)(particle.id & 0xFF));
      MemoryUtil.memPutFloat(address + 4L, (float)(particle.id >> 8 & 0xFF));
      MemoryUtil.memPutFloat(address + 8L, (float)(brightness >> 4 & 15 | brightness >> 16 & 240));
      MemoryUtil.memPutFloat(address + 12L, (float)(scale & 0xFF));
      float alpha = particle.touchedAir ? Math.min(1.0F, particle.despawnTime / 2.0F) : 1.0F;
      MemoryUtil.memPutFloat(address + 16L, this.cudaOldPositions[srcOffst]);
      MemoryUtil.memPutFloat(address + 20L, this.cudaOldPositions[srcOffst + 1]);
      MemoryUtil.memPutFloat(address + 24L, this.cudaOldPositions[srcOffst + 2]);
      MemoryUtil.memPutFloat(address + 28L, alpha);
      MemoryUtil.memPutFloat(address + 32L, x);
      MemoryUtil.memPutFloat(address + 36L, y);
      MemoryUtil.memPutFloat(address + 40L, z);
      MemoryUtil.memPutFloat(address + 44L, 1.0F);
   }

   @Override
   public int fillVolume(long address) {
      float baseAdd = 51.0F;
      PhysicsWorld physics = this.getWorld();
      Level level = physics.getLevel();
      float renderPercent = (float)physics.getRenderPercent();

      for (int i = 0; i < this.activeParticles; i++) {
         int index = i * 4;
         float x = this.cudaPositions[index];
         float y = this.cudaPositions[index + 1];
         float z = this.cudaPositions[index + 2];
         float ox = this.cudaOldPositions[index];
         float oy = this.cudaOldPositions[index + 1];
         float oz = this.cudaOldPositions[index + 2];
         SmokeDomainCuda.ParticleInfo info = this.particles[i];
         float animationScale = info.touchedAir ? Math.min(1.0F, info.despawnTime / 2.0F) : 1.0F;
         float radiusWorld = Math.min(info.alive * 0.5F, 1.0F) * 0.5F;
         int brightness = info.getLight(level, tmpPos.set((double)(ox + x), (double)(oy + y), (double)(oz + z)));
         MemoryUtil.memPutFloat(address, Mth.lerp(renderPercent, ox, x));
         MemoryUtil.memPutFloat(address + 4L, Mth.lerp(renderPercent, oy, y));
         MemoryUtil.memPutFloat(address + 8L, Mth.lerp(renderPercent, oz, z));
         MemoryUtil.memPutFloat(address + 12L, radiusWorld);
         MemoryUtil.memPutFloat(address + 16L, baseAdd * animationScale);
         MemoryUtil.memPutInt(address + 20L, info.id);
         MemoryUtil.memPutInt(address + 24L, brightness);
         address += 32L;
      }

      return this.activeParticles;
   }

   @Override
   public void invalidateBrightness(LongSet updatedLightBlocks) {
      super.invalidateBrightness(updatedLightBlocks);

      for (int i = 0; i < this.activeParticles; i++) {
         SmokeDomainCuda.ParticleInfo particle = this.particles[i];
         MutableBlockPos cached = particle.getCachedBrightnessPos();
         if (cached != null) {
            long pos = cached.asLong();
            if (updatedLightBlocks.contains(pos)) {
               particle.invalidateBrightness();
            }
         }
      }
   }

   private void remove() {
      if (this.smokeSystem != null) {
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         cudaMgr.acquireContext();
         this.smokeSystem.removeParticleBuffer(this.particleBuffer);
         this.particleBuffer.release();
         this.particleBuffer = null;
         MemoryUtil.memFree(this.positionsBuffer);
         MemoryUtil.memFree(this.velocitiesBuffer);
         MemoryUtil.memFree(this.phaseBuffer);
         this.world.removeParticleSystem(this.smokeSystem);
         this.smokeSystem.release();
         this.smokeSystem = null;
         if (this.smokeMat != null) {
            this.smokeMat.release();
            this.smokeMat = null;
         }

         this.activeParticles = 0;
         cudaMgr.releaseContext();
      }
   }

   @Override
   public void destroy() {
      this.remove();
   }

   private class ParticleInfo {
      public float scale;
      public int id;
      public float despawnTime;
      public float alive;
      public boolean touchedAir;
      private long lastChunk;
      private int cachedBrightness;
      private MutableBlockPos cachedBrightnessPos;

      private ParticleInfo() {
         Objects.requireNonNull(SmokeDomainCuda.this);
         super();
         this.lastChunk = Long.MAX_VALUE;
      }

      public void loadChunkPhysics(PhysicsWorld world, float x, float y, float z) {
         Vector3d offset = world.getOffset();
         int cx = Mth.floor((double)x + offset.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         int cy = Mth.floor((double)y + offset.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         int cz = Mth.floor((double)z + offset.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         long chunkIndex = PhysicsIndex.pack(cx, cy, cz);
         if (chunkIndex != this.lastChunk) {
            if (this.lastChunk != Long.MAX_VALUE) {
               world.removeLoadedChunkEntity(this.lastChunk);
            }

            this.lastChunk = chunkIndex;
            world.addLoadedChunkEntity(this.lastChunk);
         }
      }

      public void unloadChunkPhysics(PhysicsWorld world) {
         if (this.lastChunk != Long.MAX_VALUE) {
            world.removeLoadedChunkEntity(this.lastChunk);
            this.lastChunk = Long.MAX_VALUE;
         }
      }

      public int getLight(Level level, MutableBlockPos blockPos) {
         if (!StarterClient.disableLightingCache) {
            if (this.cachedBrightnessPos == null) {
               this.cachedBrightnessPos = new MutableBlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
            } else if (this.cachedBrightnessPos.getX() == blockPos.getX()
               && this.cachedBrightnessPos.getY() == blockPos.getY()
               && this.cachedBrightnessPos.getZ() == blockPos.getZ()) {
               return this.cachedBrightness;
            }
         }

         BlockState bState = level.getBlockState(blockPos);
         int x = blockPos.getX();
         int y = blockPos.getY();
         int z = blockPos.getZ();
         int brightness = 0;
         if (!bState.canOcclude()) {
            brightness = LightCoordsUtil.getLightCoords(level, blockPos);
         } else {
            bState = level.getBlockState(blockPos.set(x, y + 1, z));
            if (!bState.canOcclude()) {
               brightness = LightCoordsUtil.getLightCoords(level, blockPos);
            } else {
               bState = level.getBlockState(blockPos.set(x, y - 1, z));
               if (!bState.canOcclude()) {
                  brightness = LightCoordsUtil.getLightCoords(level, blockPos);
               } else {
                  bState = level.getBlockState(blockPos.set(x, y, z - 1));
                  if (!bState.canOcclude()) {
                     brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                  } else {
                     bState = level.getBlockState(blockPos.set(x + 1, y, z));
                     if (!bState.canOcclude()) {
                        brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                     } else {
                        bState = level.getBlockState(blockPos.set(x, y, z + 1));
                        if (!bState.canOcclude()) {
                           brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                        } else {
                           bState = level.getBlockState(blockPos.set(x - 1, y, z));
                           if (!bState.canOcclude()) {
                              brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                           }
                        }
                     }
                  }
               }
            }
         }

         blockPos.set(x, y, z);
         if (!StarterClient.disableLightingCache) {
            this.cachedBrightness = brightness;
            this.cachedBrightnessPos.set(x, y, z);
         }

         return brightness;
      }

      public MutableBlockPos getCachedBrightnessPos() {
         return this.cachedBrightnessPos;
      }

      public void invalidateBrightness() {
         this.cachedBrightnessPos = null;
      }
   }
}
