package net.diebuddies.physics;

import net.diebuddies.config.ConfigClient;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxSphereGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;
import physx.physics.PxRigidDynamicLockFlagEnum;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class SmokeRigidBody extends IRigidBody {
   public Vector3d pos = new Vector3d();
   public Vector3d vel = new Vector3d();
   public float density = 1.0F;
   public float averagedDensity = 1.0F;
   public float alive = 0.0F;
   public float damping = 0.0F;
   public float smokeSuppression = 0.0F;
   public boolean airDespawn;
   public float despawnTime;

   private SmokeRigidBody() {
   }

   @Override
   public void updateTransformations(PhysicsWorld physics, double diff) {
   }

   @Override
   public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
      super.updatePhysics(physics, diff, blocksChanged);
      this.loadChunkPhysics((float)this.pos.x, (float)this.pos.y, (float)this.pos.z);
      this.alive = (float)((double)this.alive + diff);
      if (!this.airDespawn) {
         Vector3d physicsOffset = physics.getOffset();
         if (this.pos.y + physicsOffset.y < (double)physics.getLevel().getMinY()) {
            this.startDespawn(2.0F);
         }
      }
   }

   public void startDespawn(float despawnTime) {
      this.entity.time = Math.min(this.entity.time, Math.max(despawnTime, this.despawnTime));
      this.airDespawn = true;
   }

   public static SmokeRigidBody createSmoke(PhysicsEntity entity, float radius) {
      SmokeRigidBody rigidBody = new SmokeRigidBody();
      Matrix4d transformation = entity.getTransformation();
      rigidBody.entity = new PhysicsRenderable(entity);
      switch (entity.type) {
         case SMOKE:
            rigidBody.despawnTime = (float)(
               ConfigClient.particleDespawnTimeSmoke + (double)net.diebuddies.math.Math.random() * ConfigClient.particleDespawnTimeVarianceSmoke
            );
            break;
         case SMOKE_CUDA:
            rigidBody.despawnTime = (float)(
               ConfigClient.particleDespawnTimeSmokeCuda + (double)net.diebuddies.math.Math.random() * ConfigClient.particleDespawnTimeVarianceSmokeCuda
            );
            break;
         default:
            rigidBody.despawnTime = (float)(
               ConfigClient.particleDespawnTimeSmokeVolumetric
                  + (double)net.diebuddies.math.Math.random() * ConfigClient.particleDespawnTimeVarianceSmokeVolumetric
            );
      }

      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxShapeFlags shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, (byte)PxShapeFlagEnum.eSIMULATION_SHAPE.value);
         PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)transformation.m30(), (float)transformation.m31(), (float)transformation.m32());
         PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F);
         PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
         PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, 0, 0);
         PxSphereGeometry sphereGeometry = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, radius);
         PxShape boxShape = StarterClient.physics.createShape(sphereGeometry, StarterClient.smokeMaterial, true, shapeFlags);
         PxRigidActor sphere = null;
         PxRigidActor var16 = StarterClient.physics.createRigidDynamic(tmpPose);
         PxRigidDynamic dynamicActor = (PxRigidDynamic)var16;
         dynamicActor.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, true);
         dynamicActor.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, true);
         dynamicActor.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, true);
         dynamicActor.setSolverIterationCounts(1, 1);
         rigidBody.shape = boxShape;
         rigidBody.rigidBody = var16;
         boxShape.setSimulationFilterData(tmpFilterData);
         dynamicActor.attachShape(boxShape);
         PxRigidBodyExt.updateMassAndInertia(dynamicActor, 0.001F);
         rigidBody.setMass(dynamicActor.getMass());
      } catch (Throwable var15) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var14) {
               var15.addSuppressed(var14);
            }
         }

         throw var15;
      }

      if (mem != null) {
         mem.close();
      }

      return rigidBody;
   }
}
