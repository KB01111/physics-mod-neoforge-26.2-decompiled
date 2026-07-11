package net.diebuddies.physics;

import java.util.List;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxCapsuleGeometry;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;

public class BoxRigidBody extends BasicRigidBody {
   public float width;
   public float height;
   public float depth;

   private BoxRigidBody() {
   }

   public static BoxRigidBody create(PhysicsEntity entity, float width, float height, float depth, float offsetx, float offsety, float offsetz, boolean dynamic) {
      BoxRigidBody boxRigidBody = new BoxRigidBody();
      Vector3d scale = entity.getTransformation().getScale(new Vector3d());
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      boxRigidBody.entity = new PhysicsRenderable(entity);
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
         PxFilterData tmpFilterData = null;
         tmpFilterData = PxFilterData.createAt(
            mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0
         );
         PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(
            mem,
            MemoryStack::nmalloc,
            width * 0.5F * (float)Math.abs(scale.x) * entity.enlargeHitbox.x,
            height * 0.5F * (float)Math.abs(scale.y) * entity.enlargeHitbox.y,
            depth * 0.5F * (float)Math.abs(scale.z) * entity.enlargeHitbox.z
         );
         PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
         if (offsetx != 0.0F || offsety != 0.0F || offsetz != 0.0F) {
            boxShape.setLocalPose(
               PxTransform.createAt(
                  mem,
                  MemoryStack::nmalloc,
                  PxVec3.createAt(mem, MemoryStack::nmalloc, offsetx, offsety, offsetz),
                  PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
               )
            );
         }

         PxRigidActor box = null;
         if (dynamic) {
            box = StarterClient.physics.createRigidDynamic(tmpPose);
            boxRigidBody.setMass(((PxRigidDynamic)box).getMass());
         } else {
            box = StarterClient.physics.createRigidStatic(tmpPose);
         }

         boxRigidBody.shape = boxShape;
         boxRigidBody.rigidBody = box;
         boxShape.setSimulationFilterData(tmpFilterData);
         box.attachShape(boxShape);
         if (dynamic) {
            PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1F);
            boxRigidBody.setMass(((PxRigidBody)box).getMass());
            ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25F);
            PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
            d.setContactReportThreshold(0.25F);
            ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5F);
         }
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

      boxRigidBody.width = width;
      boxRigidBody.height = height;
      boxRigidBody.depth = depth;
      return boxRigidBody;
   }

   public static BoxRigidBody create(PhysicsEntity entity, boolean dynamic) {
      BoxRigidBody boxRigidBody = new BoxRigidBody();
      Vector3d scale = entity.getTransformation().getScale(new Vector3d());
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      boxRigidBody.entity = new PhysicsRenderable(entity);
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
         PxFilterData tmpFilterData = null;
         tmpFilterData = PxFilterData.createAt(
            mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0
         );
         int numPoints = 0;
         List<Model> models = entity.models;
         Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
         Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

         for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            if (!model.onlyVisual) {
               Mesh mesh = model.mesh;
               if (model.physicsMesh != null) {
                  mesh = model.physicsMesh;
               }

               List<Vector3f> positions = mesh.positions;

               for (int j = 0; j < positions.size(); j++) {
                  Vector3f position = positions.get(j);
                  min.min(position);
                  max.max(position);
                  numPoints++;
               }
            }
         }

         if (numPoints <= 1 || min.x == max.x || min.y == max.y || min.z == max.z) {
            min.set(-0.5F);
            max.set(0.5F);
         }

         float width = max.x - min.x;
         float height = max.y - min.y;
         float depth = max.z - min.z;
         PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(
            mem,
            MemoryStack::nmalloc,
            width * 0.5F * (float)Math.abs(scale.x) * entity.enlargeHitbox.x,
            height * 0.5F * (float)Math.abs(scale.y) * entity.enlargeHitbox.y,
            depth * 0.5F * (float)Math.abs(scale.z) * entity.enlargeHitbox.z
         );
         PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
         PxRigidActor box = null;
         if (dynamic) {
            box = StarterClient.physics.createRigidDynamic(tmpPose);
            boxRigidBody.setMass(((PxRigidDynamic)box).getMass());
         } else {
            box = StarterClient.physics.createRigidStatic(tmpPose);
         }

         boxRigidBody.shape = boxShape;
         boxRigidBody.rigidBody = box;
         boxShape.setSimulationFilterData(tmpFilterData);
         box.attachShape(boxShape);
         if (dynamic) {
            PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1F);
            boxRigidBody.setMass(((PxRigidBody)box).getMass());
            ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25F);
            PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
            d.setContactReportThreshold(0.25F);
            ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5F);
         }

         boxRigidBody.width = width;
         boxRigidBody.height = height;
         boxRigidBody.depth = depth;
      } catch (Throwable var24) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var23) {
               var24.addSuppressed(var23);
            }
         }

         throw var24;
      }

      if (mem != null) {
         mem.close();
      }

      return boxRigidBody;
   }

   public static BoxRigidBody createFromConvexWithOffset(PhysicsEntity entity, boolean dynamic) {
      BoxRigidBody boxRigidBody = new BoxRigidBody();
      Vector3d scale = entity.getTransformation().getScale(new Vector3d());
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      boxRigidBody.entity = new PhysicsRenderable(entity);
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
         PxFilterData tmpFilterData = null;
         tmpFilterData = PxFilterData.createAt(
            mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0
         );
         int numPoints = 0;
         List<Model> models = entity.models;
         Vector3f min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
         Vector3f max = new Vector3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

         for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            if (!model.onlyVisual) {
               Mesh mesh = model.mesh;
               if (model.physicsMesh != null) {
                  mesh = model.physicsMesh;
               }

               List<Vector3f> positions = mesh.positions;

               for (int j = 0; j < positions.size(); j++) {
                  Vector3f position = positions.get(j);
                  min.min(position);
                  max.max(position);
                  numPoints++;
               }
            }
         }

         if (numPoints <= 0) {
            min.set(-0.5F);
            max.set(0.5F);
         }

         float threshold = 0.02F;
         if (Math.abs(max.x - min.x) < threshold) {
            min.x -= threshold;
            max.x += threshold;
         }

         if (Math.abs(max.y - min.y) < threshold) {
            min.y -= threshold;
            max.y += threshold;
         }

         if (Math.abs(max.z - min.z) < threshold) {
            min.z -= threshold;
            max.z += threshold;
         }

         float width = max.x - min.x;
         float height = max.y - min.y;
         float depth = max.z - min.z;
         float offsetx = (max.x + min.x) * 0.5F;
         float offsety = (max.y + min.y) * 0.5F;
         float offsetz = (max.z + min.z) * 0.5F;
         PxBoxGeometry boxGeometry = PxBoxGeometry.createAt(
            mem,
            MemoryStack::nmalloc,
            width * 0.5F * (float)Math.abs(scale.x) * entity.enlargeHitbox.x,
            height * 0.5F * (float)Math.abs(scale.y) * entity.enlargeHitbox.y,
            depth * 0.5F * (float)Math.abs(scale.z) * entity.enlargeHitbox.z
         );
         PxShape boxShape = StarterClient.physics.createShape(boxGeometry, StarterClient.defaultMaterial, true, shapeFlags);
         boxShape.setLocalPose(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, offsetx, offsety, offsetz),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
            )
         );
         PxRigidActor box = null;
         if (dynamic) {
            box = StarterClient.physics.createRigidDynamic(tmpPose);
            boxRigidBody.setMass(((PxRigidDynamic)box).getMass());
         } else {
            box = StarterClient.physics.createRigidStatic(tmpPose);
         }

         boxRigidBody.shape = boxShape;
         boxRigidBody.rigidBody = box;
         boxShape.setSimulationFilterData(tmpFilterData);
         box.attachShape(boxShape);
         if (dynamic) {
            PxRigidBodyExt.updateMassAndInertia((PxRigidBody)box, 0.1F);
            boxRigidBody.setMass(((PxRigidBody)box).getMass());
            ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25F);
            PxRigidDynamic d = (PxRigidDynamic)boxRigidBody.rigidBody;
            d.setContactReportThreshold(0.25F);
            ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5F);
         }

         boxRigidBody.width = width;
         boxRigidBody.height = height;
         boxRigidBody.depth = depth;
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

      return boxRigidBody;
   }

   public static BoxRigidBody createPlayer(PhysicsEntity entity, float width, float height, float depth, boolean dynamic) {
      BoxRigidBody boxRigidBody = new BoxRigidBody();
      Vector3d scale = entity.getTransformation().getScale(new Vector3d());
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      boxRigidBody.entity = new PhysicsRenderable(entity);
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
         PxFilterData tmpFilterData = null;
         tmpFilterData = PxFilterData.createAt(
            mem, MemoryStack::nmalloc, entity.physicsGroup, entity.physicsMask, dynamic ? ContactSimulationCallback.REPORT_CONTACT_FLAGS : 0, 0
         );
         float radius = depth * 0.5F * (float)Math.abs(scale.x) * entity.enlargeHitbox.x;
         float halfHeight = height * 0.5F * (float)Math.abs(scale.y) * entity.enlargeHitbox.y;
         PxCapsuleGeometry capsuleGeometry = PxCapsuleGeometry.createAt(mem, MemoryStack::nmalloc, radius, halfHeight - radius * 0.5F);
         PxShape capsuleShape = StarterClient.physics.createShape(capsuleGeometry, StarterClient.defaultMaterial, true, shapeFlags);
         float a = (float) (Math.PI / 4);
         float s = (float)Math.sin((double)a);
         capsuleShape.setLocalPose(
            PxTransform.createAt(
               mem,
               MemoryStack::nmalloc,
               PxVec3.createAt(mem, MemoryStack::nmalloc, 0.0F, radius * 0.5F, 0.0F),
               PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, s, (float)Math.cos((double)a))
            )
         );
         PxRigidActor actor = null;
         if (dynamic) {
            actor = StarterClient.physics.createRigidDynamic(tmpPose);
            boxRigidBody.setMass(((PxRigidDynamic)actor).getMass());
         } else {
            actor = StarterClient.physics.createRigidStatic(tmpPose);
         }

         boxRigidBody.shape = capsuleShape;
         boxRigidBody.rigidBody = actor;
         capsuleShape.setSimulationFilterData(tmpFilterData);
         actor.attachShape(capsuleShape);
         if (dynamic) {
            PxRigidBodyExt.updateMassAndInertia((PxRigidBody)actor, 0.1F);
            boxRigidBody.setMass(((PxRigidBody)actor).getMass());
            ((PxRigidDynamic)boxRigidBody.rigidBody).setContactReportThreshold(0.25F);
            ((PxRigidBody)boxRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5F);
         }
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

      boxRigidBody.width = width;
      boxRigidBody.height = height;
      boxRigidBody.depth = depth;
      return boxRigidBody;
   }
}
