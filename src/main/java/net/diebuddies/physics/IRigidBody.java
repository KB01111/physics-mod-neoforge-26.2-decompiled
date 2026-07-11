package net.diebuddies.physics;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.DoublyLinkedList;
import net.minecraft.util.Mth;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxActorFlagEnum;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;

public abstract class IRigidBody implements DoublyLinkedList.NodeStorage<IRigidBody> {
   private static int counter;
   private int hashCode;
   protected PhysicsWorld physics;
   protected PxShape shape;
   protected PxRigidActor rigidBody;
   protected PhysicsRenderable entity;
   private float mass;
   private long lastChunk = Long.MAX_VALUE;
   private float angularDamping;
   private float linearDamping;
   private boolean kinematic;
   private boolean frozen;
   private boolean gravity = true;
   public boolean separateController;
   private boolean destroyed = false;
   private DoublyLinkedList.Node<IRigidBody> node;

   public IRigidBody() {
      this.hashCode = counter++;
   }

   public void destroy() {
      if (!this.destroyed) {
         if (this.entity != null) {
            this.entity.destroy();
         }

         if (this.shape != null) {
            this.shape.release();
         }

         if (this.rigidBody != null) {
            this.rigidBody.release();
         }
      }

      this.destroyed = true;
   }

   public boolean isDestroyed() {
      return this.destroyed;
   }

   public PhysicsRenderable getEntity() {
      return this.entity;
   }

   public PxShape getShape() {
      return this.shape;
   }

   public PxRigidActor getRigidBody() {
      return this.rigidBody;
   }

   public void updateTransformations(PhysicsWorld physics, double diff) {
   }

   public void setPhysicsWorld(PhysicsWorld physics) {
      this.physics = physics;
   }

   public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
      if (ConfigClient.windPhysics && this.getRigidBody() instanceof PxRigidBody rigidBody && !this.isKinematicOrFrozen()) {
         PxTransform transform = rigidBody.getGlobalPose();
         float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
         float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
         float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
         Vector3d offset = physics.getOffset();
         WeatherDomain weatherDomain = physics.getWeatherDomain();
         int rx = Mth.floor((double)posX + offset.x);
         int ry = Mth.floor((double)posY + offset.y);
         int rz = Mth.floor((double)posZ + offset.z);
         float forceStrength = weatherDomain.getWindStrength(rx, ry, rz);
         if (forceStrength > 0.001F) {
            Vector3f windDirection = weatherDomain.getWindDirection(rx, ry, rz);
            MemoryStack mem = MemoryStack.stackPush();

            try {
               float strengthMultiplier = 0.35F;
               forceStrength *= strengthMultiplier;
               PxVec3 counterForce = PxVec3.createAt(
                  mem, MemoryStack::nmalloc, windDirection.x * forceStrength, windDirection.y * forceStrength * 0.2F, windDirection.z * forceStrength
               );
               rigidBody.addForce(counterForce, PxForceModeEnum.eVELOCITY_CHANGE, true);
            } catch (Throwable var21) {
               if (mem != null) {
                  try {
                     mem.close();
                  } catch (Throwable var20) {
                     var21.addSuppressed(var20);
                  }
               }

               throw var21;
            }

            if (mem != null) {
               mem.close();
            }
         }
      }
   }

   protected void loadChunkPhysics(float x, float y, float z) {
      Vector3d offset = this.physics.getOffset();
      if (!this.isKinematicOrFrozen()) {
         int cx = Mth.floor((double)x + offset.x) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         int cy = Mth.floor((double)y + offset.y) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         int cz = Mth.floor((double)z + offset.z) >> PhysicsWorld.CHUNK_SIZE_NUM_BITS;
         long chunkIndex = PhysicsIndex.pack(cx, cy, cz);
         if (chunkIndex != this.lastChunk) {
            if (this.lastChunk != Long.MAX_VALUE) {
               this.physics.removeLoadedChunkEntity(this.lastChunk);
            }

            this.lastChunk = chunkIndex;
            this.physics.addLoadedChunkEntity(this.lastChunk);
         }
      }
   }

   public boolean hasTransformationChanged() {
      return !this.kinematic && !this.frozen;
   }

   public void setKinematic(boolean kinematic) {
      if (this.kinematic != kinematic) {
         if (this.rigidBody instanceof PxRigidDynamic) {
            ((PxRigidDynamic)this.rigidBody).setRigidBodyFlag(PxRigidBodyFlagEnum.eKINEMATIC, kinematic);
            if (!this.isKinematicOrFrozen() && this.physics != null && this.lastChunk != Long.MAX_VALUE && kinematic) {
               this.physics.removeLoadedChunkEntity(this.lastChunk);
               this.lastChunk = Long.MAX_VALUE;
            }
         }

         this.kinematic = kinematic;
      }
   }

   public boolean isKinematicOrFrozen() {
      return this.kinematic || this.frozen;
   }

   public void setFrozen(boolean frozen) {
      if (this.frozen != frozen) {
         if (frozen) {
            if (!this.isKinematicOrFrozen() && this.physics != null && this.lastChunk != Long.MAX_VALUE) {
               this.physics.removeLoadedChunkEntity(this.lastChunk);
               this.lastChunk = Long.MAX_VALUE;
            }

            this.entity.oldPosition.set(this.entity.position);
            this.entity.oldRotation.set(this.entity.rotation);
         }

         this.rigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_SIMULATION, frozen);
         this.frozen = frozen;
      }
   }

   public void recalculateLight() {
      this.entity.invalidateBrightness();
   }

   public void setGravity(boolean gravity) {
      if (this.gravity != gravity) {
         this.rigidBody.setActorFlag(PxActorFlagEnum.eDISABLE_GRAVITY, !gravity);
         this.gravity = gravity;
      }
   }

   public void applyRandomSpawnForces(float strength) {
      if (this.getRigidBody() instanceof PxRigidDynamic rigidBody) {
         PxVec3 v = rigidBody.getLinearVelocity();
         v.setX((Math.random() - 0.5F) * strength);
         v.setY((Math.random() - 0.5F) * strength);
         v.setZ((Math.random() - 0.5F) * strength);
         rigidBody.setLinearVelocity(v);
      }
   }

   public void applyRandomSpawnForces() {
      this.applyRandomSpawnForces(9.0F);
   }

   public boolean hasGravity() {
      return this.gravity;
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public void setMass(float mass) {
      this.mass = mass;
   }

   public float getMass() {
      return this.mass;
   }

   public void setAngularDamping(float angularDamping) {
      if (this.angularDamping != angularDamping) {
         this.angularDamping = angularDamping;
         ((PxRigidBody)this.rigidBody).setAngularDamping(angularDamping);
      }
   }

   public void setLinearDamping(float linearDamping) {
      if (this.linearDamping != linearDamping) {
         this.linearDamping = linearDamping;
         ((PxRigidBody)this.rigidBody).setLinearDamping(linearDamping);
      }
   }

   public long getLastChunk() {
      return this.lastChunk;
   }

   @Override
   public void setNode(DoublyLinkedList.Node<IRigidBody> node) {
      this.node = node;
   }

   @Override
   public DoublyLinkedList.Node<IRigidBody> getNode() {
      return this.node;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }
}
