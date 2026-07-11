package net.diebuddies.physics;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.StagingBuffer;
import com.mojang.blaze3d.vertex.UberGpuBuffer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.StagingBuffer.Uploader;
import com.mojang.blaze3d.vertex.TlsfAllocator.Allocation;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.diebuddies.bridge.KeyBindingsRegistry;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Vivecraft;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Vector3i;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.ocean.OceanWorld;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.smoke.InstanceUpdateCallback;
import net.diebuddies.physics.smoke.SmokeDomain;
import net.diebuddies.physics.smoke.SmokeDomainBasic;
import net.diebuddies.physics.smoke.SmokeDomainCuda;
import net.diebuddies.physics.smoke.SmokeDomainVolumetric;
import net.diebuddies.physics.snow.SnowWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.vines.DynamicLoader;
import net.diebuddies.physics.vines.VineHelper;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.render.shader.PhysicsShaders;
import net.diebuddies.util.DoublyLinkedList;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.PxTopLevelFunctions;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxD6AxisEnum;
import physx.extensions.PxD6DriveEnum;
import physx.extensions.PxD6Joint;
import physx.extensions.PxD6JointDrive;
import physx.extensions.PxD6MotionEnum;
import physx.extensions.PxJoint;
import physx.particles.PxPBDMaterial;
import physx.particles.PxPBDParticleSystem;
import physx.particles.PxParticlePhaseFlagEnum;
import physx.particles.PxParticlePhaseFlags;
import physx.physics.PxActor;
import physx.physics.PxFilterData;
import physx.physics.PxHitFlagEnum;
import physx.physics.PxHitFlags;
import physx.physics.PxQueryFilterData;
import physx.physics.PxQueryFlagEnum;
import physx.physics.PxQueryFlags;
import physx.physics.PxRaycastHit;
import physx.physics.PxRaycastResult;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;

public class PhysicsWorld implements PhysicsUpdate {
   public static final byte TERRAIN = 1;
   public static final byte DYNAMIC_OBJECT = 2;
   public static final byte KINEMATIC_MOB = 4;
   public static final byte ANCHOR = 8;
   public static final byte PARTICLES = 16;
   public static final byte DYNAMIC_BLOCKS_NO_COLLISION = 32;
   public static final byte COLLIDE_NOTHING = 0;
   public static final byte COLLIDE_ALL = 23;
   public static final byte COLLIDE_ALL_MINUS_ENTITIES = 19;
   public static final byte COLLIDE_ALL_MINUS_PARTICLES = 7;
   public static final byte COLLIDE_ALL_MINUS_DYANMIC_OBJECTS = 21;
   private static final float FIXED_TIME_STEP = 0.025F;
   public static final int CHUNK_SIZE = 4;
   public static final int CHUNK_SIZE_ONE_BITS = 3;
   public static final int CHUNK_SIZE_NUM_BITS = Integer.bitCount(3);
   public static final int CHUNK_SIZE_RELATIVE_NUM_BITS = Integer.bitCount(3);
   private static final double LIQUID_REMOVAL_DISTANCE = 128.0;
   private static final double LIQUID_REMOVAL_DISTANCE_SQUARED = 16384.0;
   public static final int FREEZE_UPDATE_RAGDOLLS_EVERY_X_TICKS = 20;
   private static final int PLAYER_GRAB_ID = Integer.MIN_VALUE;
   public float fluidParticleSize = 0.1F;
   private List<DynamicRagdoll> freezeRagdolls = new ObjectArrayList();
   private Comparator<DynamicRagdoll> freezeComparator = new Comparator<DynamicRagdoll>() {
      {
         Objects.requireNonNull(PhysicsWorld.this);
      }

      public int compare(DynamicRagdoll o1, DynamicRagdoll o2) {
         return Double.compare(o1.distanceToCamera, o2.distanceToCamera);
      }
   };
   private int ragdollFreezeRate = 20;
   private DynamicsWorld dynamicsWorld;
   private SnowWorld snowWorld;
   private OceanWorld oceanWorld;
   private SmokeDomain smokeDomain;
   private WeatherDomain weatherDomain;
   private PxPBDParticleSystem fluidSystem;
   private PxPBDMaterial fluidMat;
   private int fluidPhase;
   private List<Liquid> liquids;
   private InstanceUpdateCallback liquidInstanceUpdateCallback;
   private DoublyLinkedList<IRigidBody> bodies;
   private DoublyLinkedList<Ragdoll> ragdolls;
   private Set<PhysicsRenderable> queueForModelCreation;
   private Map<PxJoint, PhysicsWorld.Tuple<IRigidBody, IRigidBody>> jointParents;
   private List<VerletSimulation> verletSimulations;
   private ClientLevel level;
   private Map<PxActor, IRigidBody> bodyLinks;
   private LongSet loadedChunks;
   private Long2IntOpenHashMap loadedChunkEntities;
   private Long2ObjectMap<ChunkRigidBody> chunkBodies;
   private Int2ObjectMap<IRigidBody> worldEntities = new Int2ObjectOpenHashMap();
   private IntSet lastEntityUpdates = new IntOpenHashSet();
   private IntSet tmpSet = new IntOpenHashSet();
   private double renderPercent;
   private List<Explosion> explosions = new ObjectArrayList();
   private LongSet chunkUpdates = new LongLinkedOpenHashSet();
   private Vector3d offset;
   private long lastSeen;
   private boolean blocksChanged;
   private boolean loadedChunkCheck = false;
   private boolean unloadedChunkCheck = false;
   private Vivecraft vivecraft;
   private static final int MODEL_INDEX_ALIGNMENT = 16;
   @Nullable
   private ModelUberBuffers modelBuffers;
   @Nullable
   private StagingBuffer modelStagingBuffer;
   private int modelVertexHeapSize;
   private int modelIndexHeapSize;
   private final Object2IntOpenHashMap<Model> uploadedModelIndexCounts = new Object2IntOpenHashMap();
   private Vector3d center = new Vector3d();
   private GrabHand grabHand = new GrabHand();

   public PhysicsWorld(ClientLevel level) {
      this.smokeDomain = (SmokeDomain)(ConfigClient.areVolumetricSmokePhysicsEnabled()
         ? new SmokeDomainVolumetric(this)
         : (ConfigClient.cudaSmoke() ? new SmokeDomainCuda(this) : new SmokeDomainBasic(this)));
      this.dynamicsWorld = new DynamicsWorld(this, level, 0.025F);
      this.snowWorld = new SnowWorld(level);
      this.oceanWorld = new OceanWorld(this, level);
      this.weatherDomain = new WeatherDomain(this);
      this.jointParents = new Object2ObjectOpenHashMap();
      this.level = level;
      this.ragdolls = new DoublyLinkedList<>();
      this.liquids = new ObjectArrayList();
      this.bodies = new DoublyLinkedList<>();
      this.queueForModelCreation = new ObjectLinkedOpenHashSet();
      this.bodyLinks = new Object2ObjectOpenHashMap();
      this.loadedChunks = new LongLinkedOpenHashSet();
      this.loadedChunkEntities = new Long2IntOpenHashMap();
      this.loadedChunkEntities.defaultReturnValue(0);
      this.chunkBodies = new Long2ObjectOpenHashMap();
      this.verletSimulations = new ObjectArrayList();
      this.offset = new Vector3d();
      this.lastSeen = System.nanoTime();
      this.fluidParticleSize = ConfigClient.cudaLiquidsParticleSize;
   }

   public void createFluidSystem() {
      MemoryStack mem = MemoryStack.stackPush();

      try {
         this.fluidSystem = StarterClient.physics.createPBDParticleSystem(StarterClient.cudaManager, 96);
         float restOffset = this.fluidParticleSize;
         float fluidRestOffset = restOffset * 0.6F;
         this.fluidSystem.setRestOffset(restOffset);
         this.fluidSystem.setContactOffset(restOffset + 0.01F);
         this.fluidSystem.setParticleContactOffset(fluidRestOffset / 0.6F);
         this.fluidSystem.setSolidRestOffset(restOffset);
         this.fluidSystem.setFluidRestOffset(fluidRestOffset);
         this.fluidSystem.enableCCD(false);
         this.fluidSystem.setMaxVelocity(restOffset * 60.0F);
         PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 2, 23, 0, 0);
         this.fluidSystem.setSimulationFilterData(tmpFilterData);
         this.addParticleSystem(this.fluidSystem);
         PxParticlePhaseFlags flags = PxParticlePhaseFlags.createAt(mem, MemoryStack::nmalloc, 0);
         flags.raise(PxParticlePhaseFlagEnum.eParticlePhaseFluid);
         flags.raise(PxParticlePhaseFlagEnum.eParticlePhaseSelfCollide);
         this.fluidMat = StarterClient.physics.createPBDMaterial(0.0F, 0.02F, 0.02F, 0.001F, 8.0F, 0.04F, 0.05F, 0.02F, 0.1F, 1.0F, 1.0F);
         this.fluidPhase = this.fluidSystem.createPhase(this.fluidMat, flags);
      } catch (Throwable var8) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (mem != null) {
         mem.close();
      }
   }

   private void createRenderObjects() {
      if (this.modelBuffers == null && this.modelStagingBuffer == null) {
         VertexFormat format = this.getModelVertexFormat();
         this.modelVertexHeapSize = 262144 * format.getVertexSize();
         this.modelIndexHeapSize = 1048576;
         int stagingBufferSize = Math.min(this.modelVertexHeapSize + this.modelIndexHeapSize, 6291456);
         GpuDevice device = RenderSystem.getDevice();
         this.modelStagingBuffer = StagingBuffer.create("Physics Models", device, stagingBufferSize);
         this.modelBuffers = new PhysicsWorld.ModelUberBuffers(
            new UberGpuBuffer("Physics Model Vertex", 32, this.modelVertexHeapSize, format.getVertexSize(), this.modelStagingBuffer),
            new UberGpuBuffer("Physics Model Index", 64, this.modelIndexHeapSize, 16, this.modelStagingBuffer)
         );
      }
   }

   public VertexFormat getModelVertexFormat() {
      return StarterClient.iris() ? Iris.remapFormat(PhysicsShaders.PHYSICS_ENTITY_FORMAT) : PhysicsShaders.PHYSICS_ENTITY_FORMAT;
   }

   public boolean stageModelUpload(Model model, ByteBuffer vertexData, ByteBuffer indexData, int indexCount) {
      this.createRenderObjects();
      PhysicsWorld.ModelUberBuffers buffers = this.modelBuffers;
      boolean success = true;
      if (!buffers.vertexBuffer.addAllocation(model, allocation -> {
      }, vertexData)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.vertexBuffer.addAllocation(model, allocation -> {
         }, vertexData);
      }

      if (!buffers.indexBuffer.addAllocation(model, allocation -> {
      }, indexData)) {
         this.uploadGlobalGeomBuffersToGPU();
         success &= buffers.indexBuffer.addAllocation(model, allocation -> {
         }, indexData);
      }

      if (success) {
         this.uploadedModelIndexCounts.put(model, indexCount);
         this.uploadGlobalGeomBuffersToGPU();
         return true;
      } else {
         this.removeModelRenderBuffers(model);
         return false;
      }
   }

   public void uploadGlobalGeomBuffersToGPU() {
      if (this.modelBuffers != null && this.modelStagingBuffer != null) {
         GpuDevice device = RenderSystem.getDevice();
         Uploader uploader = this.modelStagingBuffer.startUploading(device.createCommandEncoder());

         try {
            this.modelBuffers.vertexBuffer.uploadStagedAllocations(device, uploader);
            this.modelBuffers.indexBuffer.uploadStagedAllocations(device, uploader);
         } catch (Throwable var6) {
            if (uploader != null) {
               try {
                  uploader.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (uploader != null) {
            uploader.close();
         }
      }
   }

   @Nullable
   public ModelRenderBufferSlice getRenderSlice(Model model) {
      if (this.modelBuffers == null) {
         return null;
      } else {
         Allocation vertexSlice = this.modelBuffers.vertexBuffer.getAllocation(model);
         Allocation indexSlice = this.modelBuffers.indexBuffer.getAllocation(model);
         return vertexSlice != null && indexSlice != null
            ? new PhysicsWorld.ModelRenderBufferSlice(
               this.modelBuffers.vertexBuffer.getGpuBuffer(vertexSlice),
               vertexSlice.getOffsetFromHeap(),
               this.modelBuffers.indexBuffer.getGpuBuffer(indexSlice),
               indexSlice.getOffsetFromHeap(),
               this.uploadedModelIndexCounts.getInt(model)
            )
            : null;
      }
   }

   public void removeModelRenderBuffers(Model model) {
      this.uploadedModelIndexCounts.removeInt(model);
      if (this.modelBuffers != null) {
         this.modelBuffers.vertexBuffer.removeAllocation(model);
         this.modelBuffers.indexBuffer.removeAllocation(model);
      }
   }

   public void update(double diff) {
      this.snowWorld.update(diff);
      this.oceanWorld.update(diff);
      this.dynamicsWorld.update(this, diff);
      this.renderPercent = this.dynamicsWorld.getTime() / (double)this.dynamicsWorld.getFixedTimeStep();
      this.chunkUpdates.clear();
   }

   @Override
   public void physicsUpdate(double diff) {
      this.checkChunksToUnload();
      this.updateMinecraftEntities(diff);
      PerformanceTracker.start("physics_tick_smoke");
      this.smokeDomain.update(diff);
      PerformanceTracker.end("physics_tick_smoke");
      PerformanceTracker.start("physics_tick");
      this.weatherDomain.update(diff);

      for (IRigidBody body : this.bodies) {
         if (!body.isKinematicOrFrozen() && !body.isDestroyed()) {
            body.updatePhysics(this, diff, this.blocksChanged);
         }
      }

      this.updatePhysicsObjects(diff);
      this.blocksChanged = false;
      this.checkLoadedChunks();
      this.emptyFluidSystem();
      PerformanceTracker.end("physics_tick");
   }

   private void updatePhysicsObjects(double diff) {
      Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
      Iterator<IRigidBody> it = this.bodies.iterator();

      while (it.hasNext()) {
         IRigidBody body = it.next();
         if (body.isDestroyed()) {
            it.remove();
         } else if (!body.isKinematicOrFrozen()) {
            body.updateTransformations(this, diff);
            PhysicsRenderable entity = body.getEntity();
            if (entity.type != PhysicsEntity.Type.VINE) {
               entity.time = (float)((double)entity.time - diff);
            }

            if (!body.separateController && (double)entity.time <= 0.0) {
               this.dynamicsWorld.removeActor(body.getRigidBody());
               body.destroy();
               if (body.getLastChunk() != Long.MAX_VALUE && !body.isKinematicOrFrozen()) {
                  this.removeLoadedChunkEntity(body.getLastChunk());
               }

               entity.spawnDeathAnimation(this, true);
               this.bodyLinks.remove(body.getRigidBody());
               it.remove();
            }
         }
      }

      if (this.ragdollFreezeRate <= 0) {
         this.freezeUpdate();
         this.ragdollFreezeRate = 20;
      }

      this.ragdollFreezeRate--;
      Iterator<Liquid> itL = this.liquids.iterator();

      while (itL.hasNext()) {
         Liquid liquid = itL.next();
         if (liquid.update(this, (double)this.dynamicsWorld.getFixedTimeStep())) {
            liquid.destroy(this);
            itL.remove();
         } else if (cameraPos.distanceToSqr((double)liquid.blockPos.getX(), (double)liquid.blockPos.getY(), (double)liquid.blockPos.getZ()) > 16384.0) {
            liquid.destroy(this);
            itL.remove();
         }
      }

      if (this.liquidInstanceUpdateCallback != null) {
         this.liquidInstanceUpdateCallback.instanceUpdate();
      }

      Iterator<Explosion> itE = this.explosions.iterator();

      while (itE.hasNext()) {
         Explosion explosion = itE.next();
         if (explosion.tickDelay == 0) {
            this.executeExplosion(explosion);
            itE.remove();
         }

         explosion.tickDelay--;
      }

      Iterator<Ragdoll> itR = this.ragdolls.iterator();
      int ragdollSize = this.ragdolls.size();
      int mobRagdollCount = 0;

      for (int i = 0; i < ragdollSize; i++) {
         Ragdoll ragdoll = itR.next();
         if (!ragdoll.isKinematic()) {
            boolean destroyRagdoll = true;
            boolean isDespawning = false;
            List<Ragdoll.LinkedBody> bodies = ragdoll.btBodies;
            int size = bodies.size();

            for (int j = 0; j < size; j++) {
               Ragdoll.LinkedBody body = bodies.get(j);
               PhysicsRenderable entityx = body.rigid().getEntity();
               isDespawning |= entityx.isDespawning();
               if ((double)entityx.time >= 0.0) {
                  destroyRagdoll = false;
                  break;
               }

               entityx.spawnDeathAnimation(this, j == 0);
            }

            if (destroyRagdoll) {
               itR.remove();
               ragdoll.remove(this);
               ragdoll.destroy();
            } else if (!(ragdoll instanceof DynamicRagdoll) && !isDespawning) {
               mobRagdollCount++;
            }
         }
      }

      if (mobRagdollCount > ConfigClient.mobRagdollLimit) {
         int amountToRemove = mobRagdollCount - ConfigClient.mobRagdollLimit;
         itR = this.ragdolls.iterator();

         for (int ix = 0; ix < amountToRemove; ix++) {
            Ragdoll ragdoll = itR.next();
            if (ragdoll instanceof DynamicRagdoll) {
               ix--;
            } else {
               List<Ragdoll.LinkedBody> bodies = ragdoll.btBodies;
               int size = bodies.size();

               for (int j = 0; j < size; j++) {
                  IRigidBody body = bodies.get(j).rigid();
                  PhysicsRenderable entityx = body.getEntity();
                  entityx.startDespawnAnimation(this.level);
               }
            }
         }
      }
   }

   private void updateMinecraftEntities(double diff) {
      PerformanceTracker.start("physics_tick_entities");
      this.tmpSet.clear();
      Iterator it = this.level.entitiesForRendering().iterator();

      while (true) {
         Entity entity;
         AABB boundingBox;
         LivingEntity living = null;
         while (true) {
            if (!it.hasNext()) {
               if (StarterClient.vivecraft) {
                  if (this.vivecraft == null) {
                     this.vivecraft = new Vivecraft();
                  }

                  this.vivecraft.performVrHandsSupport(this, this.tmpSet, this.lastEntityUpdates, this.worldEntities);
               }

               if (!((KeyMapping)KeyBindingsRegistry.GRAB_PHYSICS.get()).isUnbound()) {
                  this.addGrabBody(this, this.tmpSet, this.lastEntityUpdates, this.worldEntities);
                  IRigidBody grabBody = (IRigidBody)this.worldEntities.get(Integer.MIN_VALUE);
                  if (grabBody != null && !grabBody.isDestroyed()) {
                     this.grabObject(grabBody);
                  }
               }

               this.lastEntityUpdates.removeAll(this.tmpSet);
               IntIterator itx = this.lastEntityUpdates.iterator();

               while (itx.hasNext()) {
                  int id = itx.nextInt();
                  IRigidBody body = (IRigidBody)this.worldEntities.remove(id);
                  this.dynamicsWorld.removeActor(body.getRigidBody());
                  body.destroy();
               }

               IntSet tmp = this.lastEntityUpdates;
               this.lastEntityUpdates = this.tmpSet;
               this.tmpSet = tmp;
               PerformanceTracker.end("physics_tick_entities");
               return;
            }

            entity = (Entity)it.next();
            if (entity instanceof LivingEntity livingEntity) {
               living = livingEntity;
               boundingBox = living.getBoundingBox();

               try {
                  if (boundingBox != null && !boundingBox.hasNaN() && !living.isSpectator()) {
                     break;
                  }
               } catch (Exception var20) {
                  break;
               }
            }
         }

         double width = boundingBox.maxX - boundingBox.minX;
         double height = boundingBox.maxY - boundingBox.minY;
         double depth = boundingBox.maxZ - boundingBox.minZ;
         if (!(width <= 0.0) && !(height <= 0.0) && !(depth <= 0.0)) {
            this.center.set(boundingBox.maxX + boundingBox.minX, boundingBox.maxY + boundingBox.minY, boundingBox.maxZ + boundingBox.minZ).mul(0.5);
            if (!this.lastEntityUpdates.contains(living.getId())) {
               PhysicsEntity physicsEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
               physicsEntity.physicsGroup = 4;
               physicsEntity.physicsMask = 7;
               physicsEntity.getTransformation().translate(this.center);
               IRigidBody body = null;
               BoxRigidBody var27;
               if (entity instanceof AbstractClientPlayer) {
                  var27 = BoxRigidBody.createPlayer(physicsEntity, (float)width, (float)height, (float)depth, true);
               } else {
                  var27 = BoxRigidBody.create(physicsEntity, (float)width, (float)height, (float)depth, 0.0F, 0.0F, 0.0F, true);
               }

               var27.setKinematic(true);
               var27.setGravity(false);
               this.dynamicsWorld.addActor(var27.getRigidBody());
               this.worldEntities.put(living.getId(), var27);
            }

            IRigidBody ibody = (IRigidBody)this.worldEntities.get(living.getId());
            if (!ibody.isDestroyed()) {
               PxRigidDynamic body = (PxRigidDynamic)ibody.getRigidBody();
               MemoryStack mem = MemoryStack.stackPush();

               try {
                  body.setKinematicTarget(
                     PxTransform.createAt(
                        mem,
                        MemoryStack::nmalloc,
                        PxVec3.createAt(
                           mem,
                           MemoryStack::nmalloc,
                           (float)(this.center.x - this.offset.x),
                           (float)(this.center.y - this.offset.y),
                           (float)(this.center.z - this.offset.z)
                        ),
                        PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
                     )
                  );
               } catch (Throwable var19) {
                  if (mem != null) {
                     try {
                        mem.close();
                     } catch (Throwable var18) {
                        var19.addSuppressed(var18);
                     }
                  }

                  throw var19;
               }

               if (mem != null) {
                  mem.close();
               }
            }

            this.tmpSet.add(living.getId());
         }
      }
   }

   public void addGrabBody(PhysicsWorld physicsWorld, IntSet active, IntSet activeLastFrame, Int2ObjectMap<IRigidBody> worldEntities) {
      Vector3d offset = physicsWorld.getOffset();
      double width = 0.2;
      double height = 0.2;
      double depth = 0.2;
      Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
      Vec3 translation = camera.position();
      Vector3f lookDir = new Vector3f(camera.forwardVector()).normalize();
      Vector3f upDir = new Vector3f(camera.upVector()).normalize();
      Quaternionf rotation = new Quaternionf().lookAlong(lookDir, upDir).invert();
      if (!activeLastFrame.contains(Integer.MIN_VALUE)) {
         PhysicsEntity physicsEntity = new PhysicsEntity(PhysicsEntity.Type.MOB, null);
         physicsEntity.physicsGroup = 4;
         physicsEntity.physicsMask = 7;
         physicsEntity.getTransformation()
            .translationRotate(translation.x, translation.y, translation.z, (double)rotation.x, (double)rotation.y, (double)rotation.z, (double)rotation.w);
         IRigidBody body = null;
         IRigidBody var25 = BoxRigidBody.create(physicsEntity, (float)width, (float)height, (float)depth, 0.0F, 0.0F, 0.0F, true);
         var25.setKinematic(true);
         var25.setGravity(false);
         physicsWorld.getDynamicsWorld().addActor(var25.getRigidBody());
         worldEntities.put(Integer.MIN_VALUE, var25);
      }

      IRigidBody ibody = (IRigidBody)worldEntities.get(Integer.MIN_VALUE);
      if (!ibody.isDestroyed()) {
         PxRigidDynamic body = (PxRigidDynamic)ibody.getRigidBody();
         MemoryStack mem = MemoryStack.stackPush();

         try {
            body.setKinematicTarget(
               PxTransform.createAt(
                  mem,
                  MemoryStack::nmalloc,
                  PxVec3.createAt(
                     mem, MemoryStack::nmalloc, (float)(translation.x - offset.x), (float)(translation.y - offset.y), (float)(translation.z - offset.z)
                  ),
                  PxQuat.createAt(mem, MemoryStack::nmalloc, rotation.x, rotation.y, rotation.z, rotation.w)
               )
            );
         } catch (Throwable var23) {
            if (mem != null) {
               try {
                  mem.close();
               } catch (Throwable var22) {
                  var23.addSuppressed(var22);
               }
            }

            throw var23;
         }

         if (mem != null) {
            mem.close();
         }
      }

      active.add(Integer.MIN_VALUE);
   }

   public void grabObject(IRigidBody handBody) {
      boolean triggered = ((KeyMapping)KeyBindingsRegistry.GRAB_PHYSICS.get()).isDown();
      if (triggered && !this.grabHand.isPreviousTriggered()) {
         this.getDynamicsWorld().getScene().sceneQueriesUpdate();
         this.getDynamicsWorld().getScene().fetchQueries(true);
         if (this.grabHand.getJoint() != null) {
            this.grabHand.getJoint().release();
            this.grabHand.setJoint(null);
         }

         try {
            MemoryStack mem = MemoryStack.stackPush();

            try {
               Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
               Vec3 pos = camera.position();
               Vector3f dir = new Vector3f(camera.forwardVector()).normalize();
               Vector3d offset = this.getOffset();
               PxVec3 origin = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)(pos.x - offset.x), (float)(pos.y - offset.y), (float)(pos.z - offset.z));
               PxVec3 unitDir = PxVec3.createAt(mem, MemoryStack::nmalloc, dir.x, dir.y, dir.z);
               float distance = 5.0F;
               float closestTerrainHit = Float.MAX_VALUE;
               PxRaycastResult terrainResult = new PxRaycastResult();
               PxHitFlags terrainHitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags terrainQueryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eSTATIC.value | PxQueryFlagEnum.eANY_HIT.value))
               );
               PxQueryFilterData terrainQueryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, terrainQueryFlags);
               if (this.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, terrainResult, terrainHitFlags, terrainQueryFilter)) {
                  for (int i = 0; i < terrainResult.getNbAnyHits(); i++) {
                     PxRaycastHit hit = terrainResult.getAnyHit(i);
                     float curDistance = hit.getDistance();
                     if (curDistance < closestTerrainHit) {
                        closestTerrainHit = curDistance;
                     }
                  }
               }

               PxRaycastResult result = new PxRaycastResult();
               PxHitFlags hitFlags = PxHitFlags.createAt(mem, MemoryStack::nmalloc, (short)((byte)PxHitFlagEnum.eDEFAULT.value));
               PxQueryFlags queryFlags = PxQueryFlags.createAt(
                  mem, MemoryStack::nmalloc, (short)((byte)(PxQueryFlagEnum.eDYNAMIC.value | PxQueryFlagEnum.eNO_BLOCK.value))
               );
               PxQueryFilterData queryFilter = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, queryFlags);
               if (this.getDynamicsWorld().getScene().raycast(origin, unitDir, distance, result, hitFlags, queryFilter)) {
                  float closest = Float.MAX_VALUE;
                  PxRigidDynamic closestActor = null;
                  PxVec3 closestPos = null;

                  for (int ix = 0; ix < result.getNbAnyHits(); ix++) {
                     PxRaycastHit hit = result.getAnyHit(ix);
                     PxRigidDynamic dynamic = PxRigidDynamic.wrapPointer(hit.getActor().getAddress());
                     if (!dynamic.getRigidBodyFlags().isSet(PxRigidBodyFlagEnum.eKINEMATIC)) {
                        float curDistance = hit.getDistance();
                        if (curDistance < closest && curDistance < closestTerrainHit) {
                           closest = curDistance;
                           closestActor = dynamic;
                           closestPos = hit.getPosition();
                        }
                     }
                  }

                  if (closestActor != null) {
                     this.grabHand.setJoint(this.createGrabJoint(handBody.getRigidBody(), closestActor, closestPos));
                  }
               }

               result.destroy();
            } catch (Throwable var28) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var27) {
                     var28.addSuppressed(var27);
                  }
               }

               throw var28;
            }

            if (mem != null) {
               mem.close();
            }
         } catch (Exception var29) {
            var29.printStackTrace();
         }
      } else if (!triggered && this.grabHand.isPreviousTriggered() && this.grabHand.getJoint() != null) {
         this.grabHand.getJoint().release();
         this.grabHand.setJoint(null);
      }

      this.grabHand.setPreviousTriggered(triggered);
   }

   private void freezeUpdate() {
      Vec3 cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().position();
      this.freezeRagdolls.clear();

      for (Ragdoll r : this.ragdolls) {
         r.updatePhysics(this);
         if (r instanceof DynamicRagdoll) {
            DynamicRagdoll dynamicRagdoll = (DynamicRagdoll)r;
            if (dynamicRagdoll.aabb != null && !dynamicRagdoll.initFreeze) {
               dynamicRagdoll.updateCameraDistance(cameraPos);
               this.freezeRagdolls.add(dynamicRagdoll);
            }
         }
      }

      Collections.sort(this.freezeRagdolls, this.freezeComparator);

      for (int i = 0; i < this.freezeRagdolls.size(); i++) {
         DynamicRagdoll ragdoll = this.freezeRagdolls.get(i);
         if (i < ConfigClient.maxLoadedDynamicBlocks) {
            if (ragdoll.isFrozen()) {
               ragdoll.wakeUp();
            }

            ragdoll.setFrozen(false);
         } else {
            ragdoll.setFrozen(true);
         }
      }
   }

   private void checkLoadedChunks() {
      if (this.loadedChunkCheck) {
         ObjectIterator<Entry> it = this.loadedChunkEntities.long2IntEntrySet().iterator();

         while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            long chunk = entry.getLongKey();
            int amount = entry.getIntValue();
            if (amount != 0 && !this.loadedChunks.contains(chunk)) {
               boolean wasLoaded = this.loadChunk(chunk);
               if (wasLoaded) {
                  this.loadedChunks.add(chunk);
               }
            }
         }

         this.loadedChunkCheck = false;
      }
   }

   private void checkChunksToUnload() {
      if (this.unloadedChunkCheck) {
         LongIterator it = this.loadedChunks.iterator();

         while (it.hasNext()) {
            long chunk = it.nextLong();
            if (this.loadedChunkEntities.get(chunk) <= 0) {
               this.unloadChunk(chunk);
               it.remove();
            }
         }

         this.unloadedChunkCheck = false;
      }
   }

   public int loadedChunksAmount() {
      return this.loadedChunks.size();
   }

   public void addLoadedChunkEntity(int chunkX, int chunkY, int chunkZ) {
      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               this.increaseLoadedChunkCounter(PhysicsIndex.pack(chunkX + x, chunkY + y, chunkZ + z));
            }
         }
      }
   }

   public void addLoadedChunkEntity(Vector3i chunk) {
      this.addLoadedChunkEntity(chunk.x, chunk.y, chunk.z);
   }

   public void addLoadedChunkEntity(long chunkIndex) {
      this.addLoadedChunkEntity(PhysicsIndex.unpackX(chunkIndex), PhysicsIndex.unpackY(chunkIndex), PhysicsIndex.unpackZ(chunkIndex));
   }

   public void increaseLoadedChunkCounter(long loaded) {
      this.loadedChunkEntities.addTo(loaded, 1);
      this.loadedChunkCheck = true;
      this.unloadedChunkCheck = true;
   }

   public void removeLoadedChunkEntity(int chunkX, int chunkY, int chunkZ) {
      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               this.decreaseLoadedChunkCounter(PhysicsIndex.pack(chunkX + x, chunkY + y, chunkZ + z));
            }
         }
      }
   }

   public void removeLoadedChunkEntity(Vector3i chunk) {
      this.removeLoadedChunkEntity(chunk.x, chunk.y, chunk.z);
   }

   public void removeLoadedChunkEntity(long chunkIndex) {
      this.removeLoadedChunkEntity(PhysicsIndex.unpackX(chunkIndex), PhysicsIndex.unpackY(chunkIndex), PhysicsIndex.unpackZ(chunkIndex));
   }

   public void decreaseLoadedChunkCounter(long chunk) {
      int count = this.loadedChunkEntities.addTo(chunk, -1);
      if (count == 1) {
         this.loadedChunkEntities.remove(chunk);
      }

      this.loadedChunkCheck = true;
      this.unloadedChunkCheck = true;
   }

   private void unloadChunk(long chunkIndex) {
      ChunkRigidBody chunkBody = (ChunkRigidBody)this.chunkBodies.remove(chunkIndex);
      if (chunkBody != null) {
         this.dynamicsWorld.removeActor(chunkBody.getActor());
         chunkBody.destroy();
      }
   }

   public void blockUpdate(BlockPos pos) {
      this.blocksChanged = true;
      BlockState state = this.level.getBlockState(pos);
      this.weatherDomain.blockUpdate(pos);

      for (int i = 0; i < this.liquids.size(); i++) {
         this.liquids.get(i).blockUpdate(this, pos, state);
      }

      Ragdoll changed = null;

      for (Ragdoll ragdoll : this.ragdolls) {
         if ((!(ragdoll instanceof DynamicRagdoll dynamic) || !dynamic.markedForRemoval) && ragdoll.blockUpdate(this, pos, state)) {
            changed = ragdoll;
            break;
         }
      }

      this.updateDynamicBlockState(changed, pos, state);
      int cx = pos.getX() >> CHUNK_SIZE_NUM_BITS;
      int cy = pos.getY() >> CHUNK_SIZE_NUM_BITS;
      int cz = pos.getZ() >> CHUNK_SIZE_NUM_BITS;
      int ax = pos.getX() & 3;
      int ay = pos.getY() & 3;
      int az = pos.getZ() & 3;
      this.updateChunk(cx, cy, cz);
      if (ax == 0) {
         this.updateChunk(cx - 1, cy, cz);
      }

      if (ay == 0) {
         this.updateChunk(cx, cy - 1, cz);
      }

      if (az == 0) {
         this.updateChunk(cx, cy, cz - 1);
      }

      if (ax == 3) {
         this.updateChunk(cx + 1, cy, cz);
      }

      if (ay == 3) {
         this.updateChunk(cx, cy + 1, cz);
      }

      if (az == 3) {
         this.updateChunk(cx, cy, cz + 1);
      }
   }

   private void updateDynamicBlockState(Ragdoll changed, BlockPos pos, BlockState state) {
   }

   private void updateChunk(int cx, int cy, int cz) {
      long chunkPos = PhysicsIndex.pack(cx, cy, cz);
      if (!this.chunkUpdates.contains(chunkPos)) {
         this.chunkUpdates.add(chunkPos);
         if (this.loadedChunks.contains(chunkPos)) {
            this.unloadChunk(chunkPos);
            this.loadChunk(chunkPos);
         }
      }
   }

   private boolean loadChunk(long chunkIndex) {
      int chunkPosX = PhysicsIndex.unpackX(chunkIndex);
      int chunkPosY = PhysicsIndex.unpackY(chunkIndex);
      int chunkPosZ = PhysicsIndex.unpackZ(chunkIndex);
      if (chunkPosY >= this.level.getMinY() && chunkPosY < this.level.getMaxY() >> CHUNK_SIZE_NUM_BITS) {
         int chunkX = chunkPosX >> CHUNK_SIZE_RELATIVE_NUM_BITS;
         int chunkZ = chunkPosZ >> CHUNK_SIZE_RELATIVE_NUM_BITS;
         if (this.level.getChunk(chunkX, chunkZ, ChunkStatus.FULL, false) == null) {
            return false;
         } else {
            ChunkRigidBody chunkBody = null;
            int cWorldX = chunkPosX * 4;
            int cWorldY = chunkPosY * 4;
            int cWorldZ = chunkPosZ * 4;

            for (int x = 0; x < 4; x++) {
               for (int y = 0; y < 4; y++) {
                  for (int z = 0; z < 4; z++) {
                     BlockPos pos = new BlockPos(cWorldX + x, cWorldY + y, cWorldZ + z);
                     BlockState state = this.level.getBlockState(pos);
                     VoxelShape voxelShape;
                     if (state.getBlock() instanceof FenceBlock) {
                        voxelShape = state.getVisualShape(this.level, pos, CollisionContext.empty());
                     } else {
                        voxelShape = state.getCollisionShape(this.level, pos);
                     }

                     if (!voxelShape.isEmpty() && VineHelper.getSetting(state) == null && this.areNeighboursEmpty(this.level, pos)) {
                        boolean fullBlock = Block.isShapeFullBlock(voxelShape);

                        for (AABB aabb : voxelShape.toAabbs()) {
                           double width = aabb.maxX - aabb.minX;
                           double height = aabb.maxY - aabb.minY;
                           double depth = aabb.maxZ - aabb.minZ;
                           if (chunkBody == null) {
                              chunkBody = new ChunkRigidBody((double)cWorldX - this.offset.x, (double)cWorldY - this.offset.y, (double)cWorldZ - this.offset.z);
                           }

                           if (fullBlock) {
                              chunkBody.attachFullBlock(x, y, z);
                           } else {
                              chunkBody.attachBox(
                                 (float)((double)x + aabb.minX + width / 2.0),
                                 (float)((double)y + aabb.minY + height / 2.0),
                                 (float)((double)z + aabb.minZ + depth / 2.0),
                                 (float)width,
                                 (float)height,
                                 (float)depth
                              );
                           }
                        }
                     }
                  }
               }
            }

            if (chunkBody != null) {
               chunkBody.compileChunk();
               this.chunkBodies.put(chunkIndex, chunkBody);
               this.dynamicsWorld.addActor(chunkBody.getActor());
            }

            return true;
         }
      } else {
         return true;
      }
   }

   private boolean areNeighboursEmpty(Level level, BlockPos pos) {
      return pos.getY() >= level.getMaxY()
         || pos.getY() <= level.getMinY()
         || pos.getY() < level.getMaxY() - 1 && this.isTranslucent(level, pos.above())
         || pos.getY() > level.getMinY() && this.isTranslucent(level, pos.below())
         || this.isTranslucent(level, pos.north())
         || this.isTranslucent(level, pos.east())
         || this.isTranslucent(level, pos.south())
         || this.isTranslucent(level, pos.west());
   }

   private boolean isTranslucent(Level level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      return !Block.isShapeFullBlock(state.getShape(level, pos)) || state.getCollisionShape(level, pos).isEmpty();
   }

   private void emptyFluidSystem() {
      if (this.liquids.isEmpty() && this.fluidSystem != null) {
         this.removeParticleSystem(this.fluidSystem);
         this.fluidSystem.release();
         this.fluidMat.release();
         this.fluidSystem = null;
         this.fluidMat = null;
      }
   }

   public void addBlockParticle(
      List<Mesh> brokenBlock, PhysicsEntity particle, @Nullable List<Mesh> brokenPhysicsBlock, @Nullable List<IRigidBody> result, boolean enforcePhysicsBoxes
   ) {
      if (!particle.noVolume) {
         this.adjustOffset(particle.getTransformation());

         for (int i = 0; i < brokenBlock.size(); i++) {
            Mesh mesh = brokenBlock.get(i);
            if (!mesh.indices.isEmpty() || !mesh.indicesQuads.isEmpty()) {
               PhysicsEntity broken = new PhysicsEntity(particle.type, particle.info);
               Model model = broken.models.get(0);
               model.texture = particle.models.get(0).texture;
               model.textureID = particle.models.get(0).textureID;
               model.backfaceCulling = particle.models.get(0).backfaceCulling;
               model.shade = particle.models.get(0).shade;
               broken.setColor(particle.getBGRA());
               if (particle.rescale == null) {
                  broken.models.get(0).mesh = mesh;
                  if (brokenPhysicsBlock != null) {
                     broken.models.get(0).physicsMesh = brokenPhysicsBlock.get(i);
                  }
               } else {
                  broken.models.get(0).mesh = this.scale(mesh, particle.rescale.start, particle.rescale.end, mesh.offset);
                  if (brokenPhysicsBlock != null) {
                     broken.models.get(0).physicsMesh = this.scalePositionOnly(
                        brokenPhysicsBlock.get(i), particle.rescale.start, particle.rescale.end, mesh.offset, broken.models.get(0).mesh.offset
                     );
                  }
               }

               broken.getTransformation()
                  .set(particle.getTransformation())
                  .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
                  .translate(broken.models.get(0).mesh.offset);
               broken.time = calculateLifetime(particle);
               IRigidBody body = null;
               if (enforcePhysicsBoxes) {
                  body = BoxRigidBody.create(broken, true);
               } else {
                  body = ConvexRigidBody.create(broken, true);
               }

               this.addBody(body);
               if (result != null) {
                  result.add(body);
               }

               this.dynamicsWorld.addActor(body.getRigidBody());
               body.applyRandomSpawnForces();
            }
         }
      }
   }

   public void addBlockParticle(List<Mesh> brokenBlock, PhysicsEntity particle, List<IRigidBody> result) {
      this.addBlockParticle(brokenBlock, particle, null, result, false);
   }

   public void addBlockParticle(List<Mesh> brokenBlock, @Nullable List<Mesh> brokenPhysicsBlock, PhysicsEntity particle) {
      this.addBlockParticle(brokenBlock, particle, brokenPhysicsBlock, null, false);
   }

   public static float calculateLifetime(PhysicsEntity particle) {
      double time = 0.0;

      return (float)Math.max(
         particle.getDespawnSpeed(),
         switch (particle.type) {
            case MOB -> (double)(particle.lifetime + net.diebuddies.math.Math.random() * particle.lifetimeVariance);
            case BLOCK -> (double)(particle.lifetime + net.diebuddies.math.Math.random() * particle.lifetimeVariance);
            case VINE -> ConfigClient.particleLifetimeVines + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceVines;
            case ITEM -> ConfigClient.particleLifetimeItems + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceItems;
            case PARTICLE -> ConfigClient.particleLifetimeParticles
            + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceParticles;
            case LIQUID -> ConfigClient.particleLifetimeLiquids + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceLiquids;
            case SMOKE -> ConfigClient.particleLifetimeSmoke + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceSmoke;
            case SMOKE_CUDA -> ConfigClient.particleLifetimeSmokeCuda
            + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceSmokeCuda;
            case SMOKE_VOLUMETRIC -> ConfigClient.particleLifetimeSmokeVolumetric
            + (double)net.diebuddies.math.Math.random() * ConfigClient.particleLifetimeVarianceSmokeVolumetric;
            default -> 4.0 + (double)net.diebuddies.math.Math.random() * 3.0;
         }
      );
   }

   private Mesh scale(Mesh mesh, Vector3f min, Vector3f max, Vector3f offset) {
      Mesh scaled = new Mesh();
      List<Integer> sides = mesh.calculateFaceDirections();
      int count = 0;

      for (int i = 0; i < mesh.indices.size(); i++) {
         int index = mesh.indices.getInt(i);
         Vector3f pos = mesh.positions.get(index);
         Vector2f uv = new Vector2f((Vector2fc)mesh.uvs.get(index));
         Vector3f normal = mesh.normals.get(index);
         Integer side = sides.get(index);
         double posX = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.x + offset.x), -0.5, 0.5, (double)min.x, (double)max.x), 0.0, 1.0
         );
         double posY = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.y + offset.y), -0.5, 0.5, (double)min.y, (double)max.y), 0.0, 1.0
         );
         double posZ = net.diebuddies.math.Math.clamp(
            net.diebuddies.math.Math.remapClamp((double)(pos.z + offset.z), -0.5, 0.5, (double)min.z, (double)max.z), 0.0, 1.0
         );
         if (side == 4 || side == 5) {
            uv.set(posX, posZ);
         } else if (side == 1 || side == 3) {
            uv.set(1.0 - posZ, 1.0 - posY);
         } else if (side == 0 || side == 2) {
            uv.set(posX, 1.0 - posY);
         }

         if (mesh.colors.size() > 0) {
            scaled.colors.add(mesh.colors.getInt(index));
         }

         scaled.indices.add(count);
         scaled.uvs.add(uv);
         scaled.normals.add(new Vector3f(normal));
         scaled.positions
            .add(
               new Vector3f(
                  net.diebuddies.math.Math.remap(pos.x + 0.5F + offset.x, 0.0F, 1.0F, min.x, max.x) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.y + 0.5F + offset.y, 0.0F, 1.0F, min.y, max.y) - 0.5F,
                  net.diebuddies.math.Math.remap(pos.z + 0.5F + offset.z, 0.0F, 1.0F, min.z, max.z) - 0.5F
               )
            );
         count++;
      }

      if (mesh.tangents != null && (StarterClient.iris() || StarterClient.optifabric)) {
         scaled.calculatePBRData(false);
      }

      scaled.calculateOffset(false);
      return scaled;
   }

   private Mesh scalePositionOnly(Mesh mesh, Vector3f min, Vector3f max, Vector3f offset, Vector3f referenceOffset) {
      Mesh scaled = new Mesh();

      for (int i = 0; i < mesh.positions.size(); i++) {
         Vector3f pos = mesh.positions.get(i);
         scaled.positions
            .add(
               new Vector3f(
                     net.diebuddies.math.Math.remap(pos.x + 0.5F + offset.x, 0.0F, 1.0F, min.x, max.x) - 0.5F,
                     net.diebuddies.math.Math.remap(pos.y + 0.5F + offset.y, 0.0F, 1.0F, min.y, max.y) - 0.5F,
                     net.diebuddies.math.Math.remap(pos.z + 0.5F + offset.z, 0.0F, 1.0F, min.z, max.z) - 0.5F
                  )
                  .sub(referenceOffset)
            );
      }

      return scaled;
   }

   public void addRagdoll(Ragdoll ragdoll) {
      ragdoll.add(this);
      this.queue(() -> this.ragdolls.add(ragdoll));
   }

   public void removeRagdoll(Ragdoll ragdoll) {
      this.queue(() -> {
         boolean removed = this.ragdolls.remove(ragdoll);
         if (removed) {
            this.queue(() -> {
               ragdoll.remove(this);
               ragdoll.destroy();
            });
         }
      });
   }

   public void addLiquid(Liquid liquid) {
      this.queue(() -> {
         liquid.add(this);
         this.liquids.add(liquid);
      });
   }

   public void removeLiquid(Liquid liquid) {
      this.queue(() -> {
         this.liquids.remove(liquid);
         liquid.destroy(this);
      });
   }

   public void clearLiquids() {
      this.queue(() -> {
         for (Liquid liquid : this.liquids) {
            liquid.destroy(this);
         }

         this.liquids.clear();
      });
   }

   public IRigidBody addBlockParticle(PhysicsEntity particle, PxRigidActor actor) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation()
         .set(particle.getTransformation())
         .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
         .translate(particle.models.get(0).mesh.offset);
      particle.time = calculateLifetime(particle);
      IRigidBody body = null;
      if (particle.models.get(0).mesh == PhysicsMod.brokenBlock.get(0) && actor == null) {
         body = BoxRigidBody.create(particle, 1.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, !particle.staticPhysics);
      } else {
         body = ConvexRigidBody.create(particle, actor, !particle.staticPhysics);
      }

      this.addBody(body);
      if (actor == null) {
         this.dynamicsWorld.addActor(body.getRigidBody());
      }

      return body;
   }

   public IRigidBody addPhysicsSphere(PhysicsEntity particle, float radius) {
      this.prepareParticle(particle);
      IRigidBody body = SphereRigidBody.create(particle, radius, true, StarterClient.throwableMaterial);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public LiquidRigidBody addLiquidsSphere(PhysicsEntity particle, float radius) {
      this.prepareParticle(particle);
      LiquidRigidBody body = LiquidRigidBody.create(particle, radius, StarterClient.throwableMaterial);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public SmokeRigidBody addSmokeSphere(PhysicsEntity particle, float radius) {
      this.prepareParticle(particle);
      SmokeRigidBody body = SmokeRigidBody.createSmoke(particle, radius);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   private void prepareParticle(PhysicsEntity particle) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation().set(particle.getTransformation()).translateLocal(-this.offset.x, -this.offset.y, -this.offset.z);
      if (particle.models != null) {
         particle.getTransformation().translate(particle.models.get(0).mesh.offset);
      }

      particle.time = calculateLifetime(particle);
   }

   public IRigidBody addBlockParticle(PhysicsEntity particle) {
      for (PhysicsEntity child : particle.children) {
         this.addBlockParticle(child, null);
      }

      return this.addBlockParticle(particle, null);
   }

   private IRigidBody addSingleBlockParticleBox(PhysicsEntity particle) {
      this.adjustOffset(particle.getTransformation());
      particle.getTransformation()
         .set(particle.getTransformation())
         .translateLocal(-this.offset.x, -this.offset.y, -this.offset.z)
         .translate(particle.models.get(0).mesh.offset);
      particle.time = calculateLifetime(particle);
      IRigidBody body = BoxRigidBody.createFromConvexWithOffset(particle, true);
      this.addBody(body);
      this.dynamicsWorld.addActor(body.getRigidBody());
      return body;
   }

   public IRigidBody addBlockParticleBox(PhysicsEntity particle) {
      for (PhysicsEntity child : particle.children) {
         this.addSingleBlockParticleBox(child);
      }

      return this.addSingleBlockParticleBox(particle);
   }

   public DoublyLinkedList<IRigidBody> getBodies() {
      return this.bodies;
   }

   public void addBody(IRigidBody body) {
      this.queueForModelCreation.add(body.getEntity());
      this.bodies.add(body);
      body.setPhysicsWorld(this);
      this.bodyLinks.put(body.getRigidBody(), body);
   }

   public void removeBody(IRigidBody body) {
      this.bodies.remove(body);
      this.queueForModelCreation.remove(body.getEntity());
      this.bodyLinks.remove(body.getRigidBody());
   }

   public IRigidBody getBody(PxActor actor) {
      return this.bodyLinks.get(actor);
   }

   public Long2ObjectMap<ChunkRigidBody> getChunkBodies() {
      return this.chunkBodies;
   }

   public double getRenderPercent() {
      return this.renderPercent;
   }

   public Set<PhysicsRenderable> getQueueForModelCreation() {
      return this.queueForModelCreation;
   }

   public void applyExplosion(Explosion explosion) {
      this.explosions.add(explosion);
   }

   public Vector3d getOffset() {
      return this.offset;
   }

   public void executeExplosion(Explosion explosion) {
      Vector3d tmp = new Vector3d();
      double explosionStrengthSquared = (double)explosion.strength * 2.0 * (double)explosion.strength * 2.0;
      this.queue(() -> {
         for (IRigidBody body : this.bodies) {
            double distanceSquared = explosion.position.distanceSquared(tmp.set(body.getEntity().position).add(this.offset));
            if (distanceSquared <= explosionStrengthSquared) {
               double distance = Math.sqrt(distanceSquared);
               Vector3d direction = tmp.set(body.getEntity().position).add(this.offset).sub(explosion.position).normalize();
               direction.y += 2.0;
               direction.normalize();
               double realStrength = (1.0 - net.diebuddies.math.Math.clamp(distance / ((double)explosion.strength * 2.0), 0.0, 1.0)) * 15.0;
               if (body.getRigidBody() instanceof PxRigidDynamic rigidBody) {
                  rigidBody.wakeUp();
                  PxVec3 v = rigidBody.getLinearVelocity();
                  float vx = MemoryUtil.memGetFloat(v.getAddress());
                  float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
                  float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
                  v.setX(vx + (float)(direction.x * realStrength));
                  v.setY(vy + (float)(direction.y * realStrength));
                  v.setZ(vz + (float)(direction.z * realStrength));
                  rigidBody.setLinearVelocity(v);
               }
            }
         }
      });
   }

   public void updateLastSeen() {
      this.lastSeen = System.nanoTime();
   }

   public boolean isActive() {
      return System.nanoTime() - this.lastSeen <= 5000000000L;
   }

   public ClientLevel getWorld() {
      return this.level;
   }

   public DynamicsWorld getDynamicsWorld() {
      return this.dynamicsWorld;
   }

   public DoublyLinkedList<Ragdoll> getRagdolls() {
      return this.ragdolls;
   }

   public void addVerletSimulation(int index, VerletSimulation simulation) {
      this.verletSimulations.add(index, simulation);
   }

   public void addVerletSimulation(VerletSimulation simulation) {
      this.verletSimulations.add(simulation);
   }

   public void removeVerletSimulation(VerletSimulation simulation) {
      this.verletSimulations.remove(simulation);
   }

   public List<VerletSimulation> getVerletSimulations() {
      return this.verletSimulations;
   }

   public List<Liquid> getLiquids() {
      return this.liquids;
   }

   public SnowWorld getSnowWorld() {
      return this.snowWorld;
   }

   public void setSnowWorld(SnowWorld snowWorld) {
      this.snowWorld = snowWorld;
   }

   public OceanWorld getOceanWorld() {
      return this.oceanWorld;
   }

   public void setOceanWorld(OceanWorld oceanWorld) {
      this.oceanWorld = oceanWorld;
   }

   public SmokeDomain getSmokeDomain() {
      return this.smokeDomain;
   }

   public ClientLevel getLevel() {
      return this.level;
   }

   public WeatherDomain getWeatherDomain() {
      return this.weatherDomain;
   }

   public void addJointParents(PxJoint joint, PhysicsWorld.Tuple<IRigidBody, IRigidBody> tuple) {
      this.jointParents.put(joint, tuple);
   }

   public void removeJointParents(PxJoint joint) {
      this.jointParents.remove(joint);
   }

   public PhysicsWorld.Tuple<IRigidBody, IRigidBody> getJointParents(PxJoint joint) {
      return this.jointParents.get(joint);
   }

   public void queue(Runnable runnable) {
      this.dynamicsWorld.queue(runnable);
   }

   public void removeParticleSystem(PxPBDParticleSystem particleSystem) {
      this.dynamicsWorld.removeActor(particleSystem);
   }

   public void addParticleSystem(PxPBDParticleSystem particleSystem) {
      this.dynamicsWorld.addActor(particleSystem);
   }

   public PxPBDParticleSystem getFluidSystem() {
      if (this.fluidSystem == null) {
         this.createFluidSystem();
      }

      return this.fluidSystem;
   }

   public int getFluidPhase() {
      if (this.fluidSystem == null) {
         this.createFluidSystem();
      }

      return this.fluidPhase;
   }

   public void adjustOffset(double posX, double posY, double posZ) {
      if (this.bodies.size() == 0 && this.chunkBodies.size() == 0 && this.fluidSystem == null && this.smokeDomain.particleCount() == 0) {
         this.offset.set(posX, posY, posZ);
      }
   }

   public void adjustOffset(Matrix4d transformation) {
      boolean invalidTransformation = !transformation.isFinite();
      if (invalidTransformation) {
         Thread.dumpStack();
         StarterClient.logger.error("non finite physics transformation " + transformation);
         transformation.identity();
      }

      if (this.bodies.size() == 0
         && this.chunkBodies.size() == 0
         && this.fluidSystem == null
         && this.smokeDomain.particleCount() == 0
         && !invalidTransformation) {
         transformation.getTranslation(this.offset);
      }
   }

   public PxJoint createGrabJoint(PxRigidActor controller, PxRigidActor target, PxVec3 hitPoint) {
      Matrix4f localFrameController = this.convertTransform(controller.getGlobalPose()).invert().translate(hitPoint.getX(), hitPoint.getY(), hitPoint.getZ());
      Matrix4f localFrameTarget = this.convertTransform(target.getGlobalPose()).invert().translate(hitPoint.getX(), hitPoint.getY(), hitPoint.getZ());
      PxD6Joint joint = null;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         joint = PxTopLevelFunctions.D6JointCreate(
            StarterClient.physics, controller, this.convertTransform(localFrameController, mem), target, this.convertTransform(localFrameTarget, mem)
         );
         joint.setMotion(PxD6AxisEnum.eX, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eY, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eZ, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eTWIST, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING1, PxD6MotionEnum.eLIMITED);
         joint.setMotion(PxD6AxisEnum.eSWING2, PxD6MotionEnum.eLIMITED);
         PxD6JointDrive drive = new PxD6JointDrive(100.0F, 20.0F, 2.0F);
         joint.setDrive(PxD6DriveEnum.eX, drive);
         joint.setDrive(PxD6DriveEnum.eY, drive);
         joint.setDrive(PxD6DriveEnum.eZ, drive);
         joint.setDrive(PxD6DriveEnum.eTWIST, drive);
         joint.setDrive(PxD6DriveEnum.eSWING, drive);
         joint.setDrivePosition(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         drive.destroy();
      } catch (Throwable var11) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (mem != null) {
         mem.close();
      }

      return joint;
   }

   private Matrix4f convertTransform(PxTransform transform) {
      float rotX = MemoryUtil.memGetFloat(transform.getAddress());
      float rotY = MemoryUtil.memGetFloat(transform.getAddress() + 4L);
      float rotZ = MemoryUtil.memGetFloat(transform.getAddress() + 8L);
      float rotW = MemoryUtil.memGetFloat(transform.getAddress() + 12L);
      float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
      float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
      float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
      return new Matrix4f().translationRotate(posX, posY, posZ, rotX, rotY, rotZ, rotW);
   }

   private PxTransform convertTransform(Matrix4f matrix, MemoryStack mem) {
      Vector3f translation = matrix.getTranslation(new Vector3f());
      Quaternionf rotation = matrix.getUnnormalizedRotation(new Quaternionf());
      return PxTransform.createAt(
         mem,
         MemoryStack::nmalloc,
         PxVec3.createAt(mem, MemoryStack::nmalloc, translation.x, translation.y, translation.z),
         PxQuat.createAt(mem, MemoryStack::nmalloc, rotation.x, rotation.y, rotation.z, rotation.w)
      );
   }

   public InstanceUpdateCallback getLiquidInstanceUpdateCallback() {
      return this.liquidInstanceUpdateCallback;
   }

   public void setLiquidInstanceUpdateCallback(InstanceUpdateCallback liquidInstanceUpdateCallback) {
      this.liquidInstanceUpdateCallback = liquidInstanceUpdateCallback;
   }

   public void destroy() {
      this.dynamicsWorld.finish();
      ((DynamicLoader)this.level.getChunkSource()).setPhysicsMod(null);
      this.snowWorld.destroy();
      this.oceanWorld.destroy();
      if (this.vivecraft != null) {
         this.vivecraft.destroy();
      }

      if (this.grabHand.getJoint() != null) {
         this.grabHand.getJoint().release();
         this.grabHand.setJoint(null);
      }

      for (IRigidBody body : this.bodies) {
         if (!body.separateController) {
            this.dynamicsWorld.removeActor(body.getRigidBody());
            body.destroy();
         }
      }

      ObjectIterator var4 = this.worldEntities.values().iterator();

      while (var4.hasNext()) {
         IRigidBody bodyx = (IRigidBody)var4.next();
         this.dynamicsWorld.removeActor(bodyx.getRigidBody());
         bodyx.destroy();
      }

      for (Ragdoll ragdoll : this.ragdolls) {
         ragdoll.remove(this);
         ragdoll.destroy();
      }

      for (Liquid liquid : this.liquids) {
         liquid.destroy(this);
      }

      if (this.fluidSystem != null) {
         this.removeParticleSystem(this.fluidSystem);
         this.fluidSystem.release();
         this.fluidMat.release();
      }

      this.smokeDomain.destroy();
      var4 = this.chunkBodies.long2ObjectEntrySet().iterator();

      while (var4.hasNext()) {
         it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<ChunkRigidBody> entry = (it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry<ChunkRigidBody>)var4.next();
         ChunkRigidBody bodyx = (ChunkRigidBody)entry.getValue();
         this.dynamicsWorld.removeActor(bodyx.getActor());
         bodyx.destroy();
      }

      if (this.modelBuffers != null) {
         this.modelBuffers.vertexBuffer.close();
         this.modelBuffers.indexBuffer.close();
      }

      if (this.modelStagingBuffer != null) {
         this.modelStagingBuffer.close();
      }

      for (VerletSimulation simulation : this.getVerletSimulations()) {
         simulation.destroyed = true;
      }

      this.getVerletSimulations().clear();
      this.dynamicsWorld.destroy();
      this.ragdolls.clear();
      this.chunkBodies.clear();
      this.loadedChunks.clear();
      this.bodies.clear();
      this.bodyLinks.clear();
   }

   public IRigidBody getGrabBody() {
      return (IRigidBody)this.worldEntities.get(Integer.MIN_VALUE);
   }

   public static record ModelRenderBufferSlice(GpuBuffer vertexBuffer, long vertexBufferOffset, GpuBuffer indexBuffer, long indexBufferOffset, int indexCount) {
   }

   private static record ModelUberBuffers(UberGpuBuffer<Model> vertexBuffer, UberGpuBuffer<Model> indexBuffer) {
   }

   public static class Tuple<A, B> {
      private A a;
      private B b;

      public Tuple(A a, B b) {
         this.a = a;
         this.b = b;
      }

      public A getA() {
         return this.a;
      }

      public void setA(A a) {
         this.a = a;
      }

      public B getB() {
         return this.b;
      }

      public void setB(B b) {
         this.b = b;
      }
   }
}
