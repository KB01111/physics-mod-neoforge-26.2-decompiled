package net.diebuddies.physics;

import net.diebuddies.physics.sound.ContactSimulationCallback;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxSphereGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxMaterial;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class LiquidRigidBody extends IRigidBody {
   private boolean changedTransformation;
   private Vector3f changedTranslation = new Vector3f();
   public float startLifetime;

   private LiquidRigidBody() {
   }

   @Override
   public void updateTransformations(PhysicsWorld physics, double diff) {
      if (this.changedTransformation) {
         this.entity.oldPosition.set(this.entity.position);
         double yPos = (double)this.changedTranslation.y + physics.getOffset().y;
         if (yPos < (double)(physics.getWorld().getMinY() - 10)) {
            this.entity.startDespawnAnimation(physics.getWorld());
         }

         this.entity.position.set(this.changedTranslation.x, this.changedTranslation.y, this.changedTranslation.z);
      }

      this.changedTransformation = false;
   }

   @Override
   public void updatePhysics(PhysicsWorld physics, double diff, boolean blocksChanged) {
      super.updatePhysics(physics, diff, blocksChanged);
      PxRigidActor rigidBody = this.getRigidBody();
      PxTransform transform = rigidBody.getGlobalPose();
      float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
      float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
      float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
      this.loadChunkPhysics(posX, posY, posZ);
      this.changedTransformation = true;
      this.changedTranslation.set(posX, posY, posZ);
   }

   public static LiquidRigidBody create(PhysicsEntity entity, float radius, PxMaterial material) {
      LiquidRigidBody rigidBody = new LiquidRigidBody();
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      rigidBody.entity = new PhysicsRenderable(entity);
      Quaterniond rotation = new Quaterniond();
      entity.getTransformation().getNormalizedRotation(rotation);
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxShapeFlags shapeFlags = PxShapeFlags.createAt(
            mem, MemoryStack::nmalloc, (byte)(PxShapeFlagEnum.eSIMULATION_SHAPE.value | PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value)
         );
         PxVec3 tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, (float)translation.x, (float)translation.y, (float)translation.z);
         PxQuat tmpQuat = PxQuat.createAt(mem, MemoryStack::nmalloc, (float)rotation.x, (float)rotation.y, (float)rotation.z, (float)rotation.w);
         PxTransform tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, tmpVec, tmpQuat);
         PxFilterData tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, ContactSimulationCallback.REPORT_CONTACT_FLAGS, 0);
         PxSphereGeometry sphereGeometry = PxSphereGeometry.createAt(mem, MemoryStack::nmalloc, radius);
         PxShape boxShape = StarterClient.physics.createShape(sphereGeometry, material, true, shapeFlags);
         PxRigidActor sphere = null;
         PxRigidActor var17 = StarterClient.physics.createRigidDynamic(tmpPose);
         rigidBody.shape = boxShape;
         rigidBody.rigidBody = var17;
         boxShape.setSimulationFilterData(tmpFilterData);
         PxRigidDynamic dynamicActor = (PxRigidDynamic)var17;
         dynamicActor.attachShape(boxShape);
         PxRigidBodyExt.updateMassAndInertia(dynamicActor, 0.1F);
         rigidBody.setMass(dynamicActor.getMass());
         ((PxRigidDynamic)rigidBody.rigidBody).setContactReportThreshold(0.25F);
      } catch (Throwable var16) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (mem != null) {
         mem.close();
      }

      return rigidBody;
   }
}
