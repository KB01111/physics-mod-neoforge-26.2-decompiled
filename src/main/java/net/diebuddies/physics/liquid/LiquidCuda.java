package net.diebuddies.physics.liquid;

import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Objects;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsIndex;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Random;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.NativeObject;
import physx.common.PxCudaContext;
import physx.common.PxCudaContextManager;
import physx.common.PxCudaTopLevelFunctions;
import physx.common.PxVec4;
import physx.particles.PxParticleBuffer;
import physx.particles.PxParticleBufferDesc;
import physx.particles.PxParticleBufferFlagEnum;

public class LiquidCuda extends Liquid {
   private static final float SPAWN_ANIMATION_TIME = 0.1F;
   private static final float DESPAWN_ANIMATION_TIME = 2.0F;
   private static final float SPAWN_INV_ANIMATION_TIME = 10.0F;
   private static final float DESPAWN_INV_ANIMATION_TIME = 0.5F;
   private static final MutableBlockPos tmpPos = new MutableBlockPos();
   private int maxLiquid;
   private LiquidCuda.ParticleInfo[] particles;
   private float[] cudaPositions;
   private float[] cudaOldPositions;
   private PxParticleBuffer particleBuffer;
   private FloatBuffer positionsBuffer;
   private FloatBuffer velocitiesBuffer;
   private IntBuffer phaseBuffer;
   private List<Vector3d> bufferedSpawns = new ObjectArrayList();
   private Random random;
   private int activeParticles;

   public LiquidCuda(LiquidController controller) {
      super(controller);
      this.maxLiquid = ConfigClient.liquidCudaMaxParticles;
      this.sourceAlive = true;
      this.particles = new LiquidCuda.ParticleInfo[this.maxLiquid];
      this.random = new Random(System.nanoTime());
   }

   @Override
   public boolean update(PhysicsWorld physicsWorld, double diff) {
      this.controller.update(this, diff);
      if (this.hasBufferedParticles() || this.hasParticles()) {
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         cudaMgr.acquireContext();
         PxCudaContext cudaContext = cudaMgr.getCudaContext();
         if (this.particleBuffer == null) {
            this.createParticleBuffers(physicsWorld, cudaContext);
         }

         this.fetchPositions(cudaContext, diff);
         this.processBufferedParticles(physicsWorld, cudaContext);
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
         this.particleBuffer.setNbActiveParticles(this.activeParticles);
         this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_POSITION);
         this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_VELOCITY);
         this.particleBuffer.raiseFlags(PxParticleBufferFlagEnum.eUPDATE_PHASE);
         cudaMgr.releaseContext();
      }

      return !this.sourceAlive && this.particleCount() == 0;
   }

   private void createParticleBuffers(PhysicsWorld physicsWorld, PxCudaContext cudaContext) {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxParticleBufferDesc bufferDesc = PxParticleBufferDesc.createAt(mem, MemoryStack::nmalloc);
         bufferDesc.setMaxParticles(this.maxLiquid);
         bufferDesc.setNumActiveParticles(0);
         this.positionsBuffer = MemoryUtil.memAllocFloat(this.maxLiquid * 4);
         this.velocitiesBuffer = MemoryUtil.memAllocFloat(this.maxLiquid * 4);
         this.phaseBuffer = MemoryUtil.memAllocInt(this.maxLiquid);
         this.cudaPositions = new float[this.maxLiquid * 4];
         this.cudaOldPositions = new float[this.maxLiquid * 4];
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         this.particleBuffer = PxCudaTopLevelFunctions.CreateAndPopulateParticleBuffer(bufferDesc, cudaMgr);
         physicsWorld.getFluidSystem().addParticleBuffer(this.particleBuffer);
      } catch (Throwable var7) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
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

         for (int i = 0; i < this.activeParticles; i++) {
            LiquidCuda.ParticleInfo particle = this.particles[i];
            int arrOffset = i * 4;
            float posX = this.cudaPositions[arrOffset];
            float posY = this.cudaPositions[arrOffset + 1];
            float posZ = this.cudaPositions[arrOffset + 2];
            particle.loadChunkPhysics(this.world, posX, posY, posZ);
            particle.lifetime = (float)((double)particle.lifetime - diff);
            if (particle.lifetime <= 0.0F) {
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
   public void spawnParticle(double x, double y, double z) {
      this.bufferedSpawns
         .add(
            new Vector3d(
               x + (double)this.random.nextFloat() * 0.1 - 0.05,
               y + (double)this.random.nextFloat() * 0.1 - 0.05,
               z + (double)this.random.nextFloat() * 0.1 - 0.05
            )
         );
   }

   public void processBufferedParticles(PhysicsWorld physicsWorld, PxCudaContext cudaContext) {
      if (this.hasBufferedParticles()) {
         Vector3d offset = this.world.getOffset();
         int needed = this.bufferedSpawns.size();
         int quickDespawn = needed - (this.maxLiquid - this.activeParticles);

         for (int i = 0; i < quickDespawn && this.activeParticles > 0; i++) {
            int index = this.random.nextInt(this.activeParticles);
            LiquidCuda.ParticleInfo particle = this.particles[index];
            this.removeParticleBuffer(index);
            particle.unloadChunkPhysics(this.world);
         }

         int free = this.maxLiquid - this.activeParticles;
         if (free == 0) {
            this.bufferedSpawns.clear();
         } else {
            int fluidPhase = physicsWorld.getFluidPhase();

            for (int i = 0; i < this.bufferedSpawns.size() && i < free; i++) {
               int id = this.activeParticles;
               Vector3d bufferedSpawn = this.bufferedSpawns.get(i);
               double x = bufferedSpawn.x;
               double y = bufferedSpawn.y;
               double z = bufferedSpawn.z;
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
               this.positionsBuffer.put(id * 4, new float[]{(float)x, (float)y, (float)z, 10.0F});
               this.velocitiesBuffer.put(id * 4, new float[4]);
               this.phaseBuffer.put(id, fluidPhase);
               LiquidCuda.ParticleInfo particle = new LiquidCuda.ParticleInfo();
               particle.lifetime = (float)Math.max(
                  2.1F, ConfigClient.particleLifetimeLiquidsCuda + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceLiquidsCuda
               );
               particle.lifetimeTotal = particle.lifetime;
               this.particles[this.activeParticles] = particle;
               this.activeParticles++;
            }

            this.bufferedSpawns.clear();
         }
      }
   }

   @Override
   public int particleCount() {
      return this.activeParticles;
   }

   @Override
   public int fillInstances(PhysicsWorld physicsWorld, long address) {
      Vector3d physicsOffset = physicsWorld.getOffset();

      for (int i = 0; i < this.activeParticles; i++) {
         this.prepareFluidInstances(physicsWorld, physicsWorld.getLevel(), i, physicsOffset.x, physicsOffset.y, physicsOffset.z, address);
         address += (long)PhysicsShaders.LIQUID_INSTANCE_FORMAT.getVertexSize();
      }

      return this.activeParticles;
   }

   private void prepareFluidInstances(PhysicsWorld physics, Level level, int particleIndex, double ox, double oy, double oz, long address) {
      LiquidCuda.ParticleInfo particle = this.particles[particleIndex];
      int srcOffset = particleIndex * 4;
      float x = this.cudaPositions[srcOffset];
      float y = this.cudaPositions[srcOffset + 1];
      float z = this.cudaPositions[srcOffset + 2];
      int brightness = particle.getLight(level, tmpPos.set(ox + (double)x, oy + (double)y, oz + (double)z));
      float scale = Math.min(1.0F, particle.lifetime * 0.5F);
      float spawnAnimation = particle.lifetimeTotal - particle.lifetime;
      if (spawnAnimation <= 0.1F) {
         scale = spawnAnimation * 10.0F;
      }

      float packedLight = (float)(brightness >> 4 & 15 | brightness >> 16 & 240);
      MemoryUtil.memPutFloat(address, this.cudaOldPositions[srcOffset]);
      MemoryUtil.memPutFloat(address + 4L, this.cudaOldPositions[srcOffset + 1]);
      MemoryUtil.memPutFloat(address + 8L, this.cudaOldPositions[srcOffset + 2]);
      MemoryUtil.memPutFloat(address + 12L, packedLight);
      MemoryUtil.memPutFloat(address + 16L, this.cudaPositions[srcOffset]);
      MemoryUtil.memPutFloat(address + 20L, this.cudaPositions[srcOffset + 1]);
      MemoryUtil.memPutFloat(address + 24L, this.cudaPositions[srcOffset + 2]);
      MemoryUtil.memPutFloat(address + 28L, physics.fluidParticleSize * scale * 1.2F);
   }

   @Override
   public void invalidateBrightness(LongSet updatedLightBlocks) {
      super.invalidateBrightness(updatedLightBlocks);

      for (int i = 0; i < this.activeParticles; i++) {
         LiquidCuda.ParticleInfo particle = this.particles[i];
         MutableBlockPos cached = particle.getCachedBrightnessPos();
         if (cached != null) {
            long pos = cached.asLong();
            if (updatedLightBlocks.contains(pos)) {
               particle.invalidateBrightness();
            }
         }
      }
   }

   private void remove(PhysicsWorld world) {
      if (this.particleBuffer != null) {
         PxCudaContextManager cudaMgr = StarterClient.cudaManager;
         cudaMgr.acquireContext();
         world.getFluidSystem().removeParticleBuffer(this.particleBuffer);
         this.particleBuffer.release();
         this.particleBuffer = null;
         MemoryUtil.memFree(this.positionsBuffer);
         MemoryUtil.memFree(this.velocitiesBuffer);
         MemoryUtil.memFree(this.phaseBuffer);
         this.activeParticles = 0;
         cudaMgr.releaseContext();
      }
   }

   @Override
   public void destroy(PhysicsWorld world) {
      this.remove(world);
   }

   private class ParticleInfo {
      public float lifetime;
      public float lifetimeTotal;
      private long lastChunk;
      private int cachedBrightness;
      private MutableBlockPos cachedBrightnessPos;

      private ParticleInfo() {
         Objects.requireNonNull(LiquidCuda.this);
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
