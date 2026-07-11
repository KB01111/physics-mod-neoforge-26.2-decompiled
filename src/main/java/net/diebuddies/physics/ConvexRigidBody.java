package net.diebuddies.physics;

import java.util.List;
import net.diebuddies.physics.sound.ContactSimulationCallback;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import physx.PxTopLevelFunctions;
import physx.common.PxBoundedData;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.cooking.PxConvexFlagEnum;
import physx.cooking.PxConvexFlags;
import physx.cooking.PxConvexMeshDesc;
import physx.extensions.PxRigidActorExt;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.PxConvexMesh;
import physx.geometry.PxConvexMeshGeometry;
import physx.geometry.PxMeshScale;
import physx.physics.PxFilterData;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;
import physx.physics.PxShapeFlagEnum;
import physx.physics.PxShapeFlags;
import physx.support.Vector_PxVec3;

public class ConvexRigidBody extends BasicRigidBody {
   private PxConvexMesh convexMesh;

   private ConvexRigidBody() {
   }

   public static ConvexRigidBody create(PhysicsEntity entity, PxRigidActor baseActor, boolean dynamic) {
      ConvexRigidBody convexRigidBody = new ConvexRigidBody();
      Vector3d scale = entity.getTransformation().getScale(new Vector3d());
      Vector3d translation = entity.getTransformation().getTranslation(new Vector3d());
      convexRigidBody.entity = new PhysicsRenderable(entity);
      Quaterniond rotation = new Quaterniond();
      entity.getTransformation().getNormalizedRotation(rotation);
      MemoryStack mem = StarterClient.memoryStack.push();

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
         Vector_PxVec3 pointVector = new Vector_PxVec3();
         int numPoints = 0;
         double scaleX = scale.x * (double)entity.enlargeHitbox.x;
         double scaleY = scale.y * (double)entity.enlargeHitbox.y;
         double scaleZ = scale.z * (double)entity.enlargeHitbox.z;
         List<Model> models = entity.models;

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
                  pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, position.x, position.y, position.z));
                  numPoints++;
               }
            }
         }

         if (numPoints < 4) {
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, 0.5F, 0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, 0.5F, -0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, -0.5F, 0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, -0.5F, -0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, 0.5F, 0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, 0.5F, -0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, -0.5F, 0.5F));
            pointVector.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, -0.5F, -0.5F));
         }

         PxBoundedData points = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
         points.setCount(pointVector.size());
         points.setStride(PxVec3.SIZEOF);
         points.setData(pointVector.data());
         PxConvexMeshDesc convexMeshDesc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
         convexMeshDesc.setPoints(points);
         convexMeshDesc.setFlags(
            PxConvexFlags.createAt(
               mem, MemoryStack::nmalloc, (short)(PxConvexFlagEnum.eCOMPUTE_CONVEX.value | PxConvexFlagEnum.eFAST_INERTIA_COMPUTATION.value)
            )
         );
         PxConvexMesh convexMesh = PxTopLevelFunctions.CreateConvexMesh(StarterClient.cookingParams, convexMeshDesc);
         if (convexMesh == null) {
            Vector_PxVec3 pointVectorDefault = new Vector_PxVec3();
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, 0.5F, 0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, 0.5F, -0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, -0.5F, 0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, 0.5F, -0.5F, -0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, 0.5F, 0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, 0.5F, -0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, -0.5F, 0.5F));
            pointVectorDefault.push_back(PxVec3.createAt(mem, MemoryStack::nmalloc, -0.5F, -0.5F, -0.5F));
            points = PxBoundedData.createAt(mem, MemoryStack::nmalloc);
            points.setCount(pointVectorDefault.size());
            points.setStride(PxVec3.SIZEOF);
            points.setData(pointVectorDefault.data());
            convexMeshDesc = PxConvexMeshDesc.createAt(mem, MemoryStack::nmalloc);
            convexMeshDesc.setPoints(points);
            convexMeshDesc.setFlags(PxConvexFlags.createAt(mem, MemoryStack::nmalloc, (short)PxConvexFlagEnum.eCOMPUTE_CONVEX.value));
            convexMesh = PxTopLevelFunctions.CreateConvexMesh(StarterClient.cookingParams, convexMeshDesc);
            pointVectorDefault.destroy();
         }

         PxMeshScale meshScale = PxMeshScale.createAt(
            mem,
            MemoryStack::nmalloc,
            PxVec3.createAt(mem, MemoryStack::nmalloc, (float)scaleX, (float)scaleY, (float)scaleZ),
            PxQuat.createAt(mem, MemoryStack::nmalloc, 0.0F, 0.0F, 0.0F, 1.0F)
         );
         PxConvexMeshGeometry geometry = PxConvexMeshGeometry.createAt(mem, MemoryStack::nmalloc, convexMesh, meshScale);
         if (baseActor == null) {
            PxShape shape = StarterClient.physics.createShape(geometry, StarterClient.defaultMaterial, true, shapeFlags);
            PxRigidActor actor = null;
            if (dynamic) {
               actor = StarterClient.physics.createRigidDynamic(tmpPose);
            } else {
               actor = StarterClient.physics.createRigidStatic(tmpPose);
            }

            convexRigidBody.shape = shape;
            convexRigidBody.rigidBody = actor;
            actor.attachShape(shape);
         } else {
            PxShape shape = PxRigidActorExt.createExclusiveShape(baseActor, geometry, StarterClient.defaultMaterial, shapeFlags);
            convexRigidBody.shape = shape;
            convexRigidBody.rigidBody = baseActor;
         }

         convexRigidBody.shape.setSimulationFilterData(tmpFilterData);
         convexRigidBody.convexMesh = convexMesh;
         if (convexRigidBody.rigidBody instanceof PxRigidBody) {
            ((PxRigidBody)convexRigidBody.rigidBody).setMaxDepenetrationVelocity(2.5F);
         }

         if (convexRigidBody.rigidBody instanceof PxRigidDynamic) {
            ((PxRigidDynamic)convexRigidBody.rigidBody).setContactReportThreshold(0.25F);
         }

         if (dynamic) {
            PxRigidBodyExt.updateMassAndInertia((PxRigidBody)convexRigidBody.rigidBody, 0.1F);
            convexRigidBody.setMass(((PxRigidBody)convexRigidBody.rigidBody).getMass());
         }

         pointVector.destroy();
      } catch (Throwable var30) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var29) {
               var30.addSuppressed(var29);
            }
         }

         throw var30;
      }

      if (mem != null) {
         mem.close();
      }

      return convexRigidBody;
   }

   public static ConvexRigidBody create(PhysicsEntity entity, boolean dynamic) {
      return create(entity, null, dynamic);
   }

   @Override
   public void destroy() {
      boolean d = this.isDestroyed();
      super.destroy();
      if (!d) {
         this.convexMesh.release();
      }
   }
}
