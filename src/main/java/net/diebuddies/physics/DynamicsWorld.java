package net.diebuddies.physics;

import java.util.ArrayDeque;
import java.util.Queue;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import net.diebuddies.render.DebugRenderer;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxVec3;
import physx.physics.PxActor;
import physx.physics.PxBroadPhaseTypeEnum;
import physx.physics.PxPairFilteringModeEnum;
import physx.physics.PxPruningStructureTypeEnum;
import physx.physics.PxScene;
import physx.physics.PxSceneDesc;
import physx.physics.PxSceneFlagEnum;
import physx.physics.PxSceneQueryUpdateModeEnum;
import physx.physics.PxSolverTypeEnum;

public class DynamicsWorld {
   public static final Vector3f DEFAULT_GRAVITY = new Vector3f(0.0F, -9.81F, 0.0F);
   public static final Vector3f DEFAULT_BUOYANCY = new Vector3f(0.0F, 2.0F, 0.0F);
   private PxSceneDesc sceneDesc;
   private PxScene scene;
   private DebugRenderer debugRenderer;
   private float fixedTimeStep;
   private boolean destroyed;
   private double time = 0.0;
   private PhysicsWorld physics;
   private Queue<Runnable> events;
   private ClientLevel level;
   private Vector3f buoyancy;
   private Vector3f gravity;
   private ContactSimulationCallback contactCallback;
   private boolean simulating = true;

   public DynamicsWorld(PhysicsWorld physics, ClientLevel level, float fixedTimeStep) {
      this.events = new ArrayDeque<>();
      this.physics = physics;
      this.level = level;
      this.destroyed = false;
      this.fixedTimeStep = fixedTimeStep;
      this.debugRenderer = new DebugRenderer();
      int numThreads = ConfigClient.cpuThreads;
      this.sceneDesc = new PxSceneDesc(StarterClient.tolerances);
      Vector3f gravity = ConfigClient.getGravity(level.dimension().identifier());
      this.buoyancy = ConfigClient.getBuoyancy(level.dimension().identifier());
      MemoryStack mem = MemoryStack.stackPush();

      try {
         this.sceneDesc.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
         this.gravity = new Vector3f(gravity.x, gravity.y, gravity.z);
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

      this.sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads));
      this.sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
      this.sceneDesc.setSolverType(PxSolverTypeEnum.ePGS);
      this.sceneDesc.setKineKineFilteringMode(PxPairFilteringModeEnum.eKILL);
      this.sceneDesc.setStaticKineFilteringMode(PxPairFilteringModeEnum.eKILL);
      this.sceneDesc.setSceneQueryUpdateMode(PxSceneQueryUpdateModeEnum.eBUILD_DISABLED_COMMIT_DISABLED);
      this.sceneDesc.setSimulationEventCallback(this.contactCallback = new ContactSimulationCallback(physics, level));
      this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS);
      if (StarterClient.cudaManager != null) {
         this.sceneDesc.setCudaContextManager(StarterClient.cudaManager);
         this.sceneDesc.setStaticStructure(PxPruningStructureTypeEnum.eDYNAMIC_AABB_TREE);
         this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eENABLE_PCM);
         this.sceneDesc.getFlags().raise(PxSceneFlagEnum.eENABLE_GPU_DYNAMICS);
         this.sceneDesc.setBroadPhaseType(PxBroadPhaseTypeEnum.eGPU);
         this.sceneDesc.setSolverType(PxSolverTypeEnum.eTGS);
      }

      this.scene = StarterClient.physics.createScene(this.sceneDesc);
      this.scene.simulate(0.0F);
   }

   public Vector3f getBuoyancy() {
      return this.buoyancy;
   }

   public Vector3f getGravity() {
      return this.gravity;
   }

   public int update(PhysicsUpdate physics, double diff) {
      int updateCount = 0;
      if (!this.destroyed) {
         this.time += diff;
         boolean willUpdate = this.time >= (double)this.fixedTimeStep;
         if (willUpdate) {
            PerformanceTracker.start("physics_tick_physx");
         }

         long physicsTime = System.nanoTime();

         while (this.time >= (double)this.fixedTimeStep) {
            this.time = this.time - (double)this.fixedTimeStep;
            this.scene.fetchResults(true);
            if (ConfigClient.gravityChanged) {
               ConfigClient.gravityChanged = false;
               Vector3f gravity = ConfigClient.getGravity(this.level.dimension().identifier());
               MemoryStack mem = MemoryStack.stackPush();

               try {
                  this.scene.setGravity(PxVec3.createAt(mem, MemoryStack::nmalloc, gravity.x, gravity.y, gravity.z));
                  this.gravity.set(gravity.x, gravity.y, gravity.z);
               } catch (Throwable var13) {
                  if (mem != null) {
                     try {
                        mem.close();
                     } catch (Throwable var12) {
                        var13.addSuppressed(var12);
                     }
                  }

                  throw var13;
               }

               if (mem != null) {
                  mem.close();
               }

               this.buoyancy = ConfigClient.getBuoyancy(this.level.dimension().identifier());
            }

            this.simulating = false;
            this.runQueue();
            physics.physicsUpdate((double)this.fixedTimeStep);
            this.simulating = true;
            PerformanceTracker.pause("physics_tick_physx");
            this.debugRenderer.createDebugGizmos(this.physics);
            PerformanceTracker.unpause("physics_tick_physx");
            this.scene.simulate(this.fixedTimeStep);
            this.contactCallback.tick(this.fixedTimeStep);
            updateCount++;
            long physicsDiff = System.nanoTime() - physicsTime;
            if (physicsDiff > 25000000L) {
               break;
            }
         }

         this.time = this.time % (double)this.fixedTimeStep;
         if (willUpdate) {
            PerformanceTracker.end("physics_tick_physx");
         }
      }

      return updateCount;
   }

   public PxScene getScene() {
      return this.scene;
   }

   private void runQueue() {
      Runnable event = null;

      while ((event = this.events.poll()) != null) {
         event.run();
      }

      this.events.clear();
   }

   public void finish() {
      if (!this.destroyed) {
         this.scene.fetchResults(true);
         this.simulating = false;
         this.runQueue();
         this.scene.flushSimulation();
      }
   }

   public void destroy() {
      if (!this.destroyed) {
         this.scene.release();
         if (this.contactCallback != null) {
            this.contactCallback.destroy();
         }
      }

      this.destroyed = true;
   }

   public void addActor(PxActor actor) {
      if (this.simulating) {
         this.queue(() -> this.scene.addActor(actor));
      } else {
         this.scene.addActor(actor);
      }
   }

   public void removeActor(PxActor actor) {
      if (this.simulating) {
         this.queue(() -> this.scene.removeActor(actor));
      } else {
         this.scene.removeActor(actor);
      }
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public double getTime() {
      return this.time;
   }

   public float getFixedTimeStep() {
      return this.fixedTimeStep;
   }

   public void queue(Runnable runnable) {
      if (this.simulating) {
         this.events.add(runnable);
      } else {
         runnable.run();
      }
   }

   public DebugRenderer getDebugRenderer() {
      return this.debugRenderer;
   }
}
