package net.diebuddies.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.gizmos.Gizmo;
import net.minecraft.gizmos.GizmoPrimitives;
import net.minecraft.gizmos.GizmoProperties;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.LineGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import physx.NativeObject;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxJoint;
import physx.extensions.PxJointActorIndexEnum;
import physx.geometry.PxBoxGeometry;
import physx.geometry.PxCapsuleGeometry;
import physx.geometry.PxConvexMesh;
import physx.geometry.PxConvexMeshGeometry;
import physx.geometry.PxGeometry;
import physx.geometry.PxGeometryTypeEnum;
import physx.geometry.PxHullPolygon;
import physx.geometry.PxSphereGeometry;
import physx.physics.PxActor;
import physx.physics.PxActorFlagEnum;
import physx.physics.PxActorTypeEnum;
import physx.physics.PxActorTypeFlagEnum;
import physx.physics.PxActorTypeFlags;
import physx.physics.PxConstraint;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBodyFlagEnum;
import physx.physics.PxRigidDynamic;
import physx.physics.PxScene;
import physx.physics.PxShape;
import physx.support.NativeArrayHelpers;
import physx.support.PxArray_PxActorPtr;
import physx.support.PxArray_PxConstraintPtr;
import physx.support.PxArray_PxShapePtr;
import physx.support.PxArray_PxU32;
import physx.support.PxU8ConstPtr;
import physx.support.SupportFunctions;

public class DebugRenderer {
   private static final int KINEMATIC_COLOR = ARGB.colorFromFloat(0.5F, 0.5F, 1.0F, 0.5F);
   private static final int STATIC_COLOR = ARGB.colorFromFloat(0.5F, 0.5F, 0.5F, 1.0F);
   private static final int DYNAMIC_COLOR = ARGB.colorFromFloat(0.5F, 1.0F, 0.5F, 0.5F);
   private static final int JOINT_1_COLOR = ARGB.colorFromFloat(0.5F, 1.0F, 1.0F, 0.5F);
   private static final int JOINT_2_COLOR = ARGB.colorFromFloat(0.5F, 0.5F, 1.0F, 1.0F);
   private static final int DISABLED_COLOR = ARGB.colorFromFloat(0.5F, 0.5F, 0.5F, 0.5F);
   private List<Gizmo> activeGizmos = new ObjectArrayList();

   public void renderDebugGizmos() {
      if (ConfigClient.renderPhysicsDebugOverlay) {
         for (Gizmo gizmo : this.activeGizmos) {
            GizmoProperties properties = Gizmos.addGizmo(gizmo);
            if (gizmo instanceof LineGizmo) {
               properties.setAlwaysOnTop();
            }
         }
      }
   }

   public void createDebugGizmos(PhysicsWorld physics) {
      if (ConfigClient.renderPhysicsDebugOverlay) {
         this.activeGizmos.clear();
         Vector3d offset = physics.getOffset();
         PxScene scene = physics.getDynamicsWorld().getScene();
         IRigidBody exclude = physics.getGrabBody();
         PxActorTypeFlags flags = new PxActorTypeFlags((short)(PxActorTypeFlagEnum.eRIGID_DYNAMIC.value | PxActorTypeFlagEnum.eRIGID_STATIC.value));
         int actors = scene.getNbActors(flags);
         if (actors > 0) {
            PxArray_PxActorPtr actorPtr = new PxArray_PxActorPtr(actors);
            scene.getActors(flags, actorPtr.begin(), actors, 0);

            for (int i = 0; i < actors; i++) {
               PxActor actor = actorPtr.get(i);
               if (exclude == null || exclude.getRigidBody().getAddress() != actor.getAddress()) {
                  this.debugActor(actor, offset);
               }
            }

            actorPtr.destroy();
         }

         flags.destroy();
         int constraints = scene.getNbConstraints();
         if (constraints > 0) {
            PxArray_PxConstraintPtr constraintPtr = new PxArray_PxConstraintPtr(constraints);
            scene.getConstraints(constraintPtr.begin(), constraints, 0);

            for (int ix = 0; ix < constraints; ix++) {
               PxConstraint constraint = constraintPtr.get(ix);
               this.debugConstraint(constraint, offset);
            }

            constraintPtr.destroy();
         }
      }
   }

   private void debugConstraint(PxConstraint constraint, Vector3d offset) {
      PxArray_PxU32 constraintType = new PxArray_PxU32(1);
      NativeObject obj = SupportFunctions.PxConstraint_getExternalReference(constraint, NativeArrayHelpers.voidToU32Ptr(constraintType.begin()));
      int jointType = 0;
      if (constraintType.get(0) == jointType) {
         PxJoint joint = PxJoint.wrapPointer(obj.getAddress());
         PxRigidActor actor0 = SupportFunctions.PxConstraint_getActor0(constraint);
         PxRigidActor actor1 = SupportFunctions.PxConstraint_getActor1(constraint);
         Matrix4f transform0 = new Matrix4f();
         Matrix4f transform1 = new Matrix4f();
         if (actor0 != null) {
            transform0 = this.fromPhysicsTransform(actor0.getGlobalPose());
         }

         if (actor1 != null) {
            transform1 = this.fromPhysicsTransform(actor1.getGlobalPose());
         }

         transform0.m30((float)((double)transform0.m30() + offset.x));
         transform0.m31((float)((double)transform0.m31() + offset.y));
         transform0.m32((float)((double)transform0.m32() + offset.z));
         transform1.m30((float)((double)transform1.m30() + offset.x));
         transform1.m31((float)((double)transform1.m31() + offset.y));
         transform1.m32((float)((double)transform1.m32() + offset.z));
         Matrix4f local0 = this.fromPhysicsTransform(joint.getLocalPose(PxJointActorIndexEnum.eACTOR0));
         Matrix4f local1 = this.fromPhysicsTransform(joint.getLocalPose(PxJointActorIndexEnum.eACTOR1));
         Vector3f position0 = transform0.mul(local0, new Matrix4f()).transformPosition(new Vector3f());
         Vector3f position1 = transform1.mul(local1, new Matrix4f()).transformPosition(new Vector3f());
         Vector3f center0 = transform0.transformPosition(new Vector3f());
         Vector3f center1 = transform1.transformPosition(new Vector3f());
         this.activeGizmos
            .add(
               new LineGizmo(
                  new Vec3((double)center0.x, (double)center0.y, (double)center0.z),
                  new Vec3((double)position1.x, (double)position1.y, (double)position1.z),
                  JOINT_1_COLOR,
                  3.0F
               )
            );
         this.activeGizmos
            .add(
               new LineGizmo(
                  new Vec3((double)center1.x, (double)center1.y, (double)center1.z),
                  new Vec3((double)position0.x, (double)position0.y, (double)position0.z),
                  JOINT_2_COLOR,
                  3.0F
               )
            );
      }

      constraintType.destroy();
   }

   private void debugActor(PxActor actor, Vector3d offset) {
      PxActorTypeEnum actorType = actor.getType();
      if (actorType == PxActorTypeEnum.eRIGID_DYNAMIC || actorType == PxActorTypeEnum.eRIGID_STATIC) {
         PxRigidActor rigidActor = PxRigidActor.wrapPointer(actor.getAddress());
         Matrix4f transform = this.fromPhysicsTransform(rigidActor.getGlobalPose());
         transform.m30((float)((double)transform.m30() + offset.x));
         transform.m31((float)((double)transform.m31() + offset.y));
         transform.m32((float)((double)transform.m32() + offset.z));
         int shapes = rigidActor.getNbShapes();
         PxArray_PxShapePtr arrPtr = new PxArray_PxShapePtr(shapes);
         rigidActor.getShapes(arrPtr.begin(), shapes, 0);
         int color = STATIC_COLOR;
         if (rigidActor.getActorFlags().isSet(PxActorFlagEnum.eDISABLE_SIMULATION)) {
            color = DISABLED_COLOR;
         } else if (actorType == PxActorTypeEnum.eRIGID_DYNAMIC) {
            color = DYNAMIC_COLOR;
            PxRigidDynamic dynamic = PxRigidDynamic.wrapPointer(actor.getAddress());
            if (dynamic.getRigidBodyFlags().isSet(PxRigidBodyFlagEnum.eKINEMATIC)) {
               color = KINEMATIC_COLOR;
            }
         }

         for (int i = 0; i < arrPtr.size(); i++) {
            PxShape shape = arrPtr.get(i);
            PxGeometry geometry = shape.getGeometry();
            Matrix4f localTransform = this.fromPhysicsTransform(shape.getLocalPose());
            Matrix4f worldShapeTransform = transform.mul(localTransform, new Matrix4f());
            GizmoStyle style = GizmoStyle.fill(color);
            if (geometry.getType() == PxGeometryTypeEnum.eBOX) {
               PxBoxGeometry box = PxBoxGeometry.wrapPointer(geometry.getAddress());
               PxVec3 halfExtents = box.getHalfExtents();
               this.activeGizmos
                  .add(
                     new DebugRenderer.TransformedCuboidGizmo(
                        worldShapeTransform,
                        new AABB(
                           (double)(-halfExtents.getX()) - 0.01,
                           (double)(-halfExtents.getY()) - 0.01,
                           (double)(-halfExtents.getZ()) - 0.01,
                           (double)halfExtents.getX() + 0.01,
                           (double)halfExtents.getY() + 0.01,
                           (double)halfExtents.getZ() + 0.01
                        ),
                        style,
                        false
                     )
                  );
            } else if (geometry.getType() == PxGeometryTypeEnum.eSPHERE) {
               PxSphereGeometry sphere = PxSphereGeometry.wrapPointer(geometry.getAddress());
               float r = sphere.getRadius();
               this.activeGizmos.add(new DebugRenderer.TransformedSphereGizmo(worldShapeTransform, r, style));
            } else if (geometry.getType() == PxGeometryTypeEnum.eCAPSULE) {
               PxCapsuleGeometry capsule = PxCapsuleGeometry.wrapPointer(geometry.getAddress());
               float radius = capsule.getRadius();
               float halfHeight = capsule.getHalfHeight();
               if (!Minecraft.getInstance().gameRenderer.gameRenderState().optionsRenderState.cameraType.isFirstPerson()) {
                  this.activeGizmos.add(new DebugRenderer.TransformedCapsuleGizmo(worldShapeTransform, radius, halfHeight, style));
               }
            } else if (geometry.getType() == PxGeometryTypeEnum.eCONVEXMESH) {
               PxConvexMeshGeometry convexMesh = PxConvexMeshGeometry.wrapPointer(geometry.getAddress());
               worldShapeTransform.scale(1.04F);
               this.activeGizmos.add(new DebugRenderer.TransformedConvexMeshGizmo(worldShapeTransform, convexMesh, style));
            }
         }

         arrPtr.destroy();
      }
   }

   private Matrix4f fromPhysicsTransform(PxTransform transform) {
      PxVec3 position = transform.getP();
      PxQuat rotation = transform.getQ();
      return new Matrix4f()
         .translationRotate(position.getX(), position.getY(), position.getZ(), rotation.getX(), rotation.getY(), rotation.getZ(), rotation.getW());
   }

   public static record TransformedCapsuleGizmo(Matrix4fc transformation, float radius, float halfHeight, GizmoStyle style) implements Gizmo {
      private Vec3 transform(float x, float y, float z) {
         Vector3f tmp = new Vector3f();
         this.transformation.transformPosition(x, y, z, tmp);
         return new Vec3((double)tmp.x(), (double)tmp.y(), (double)tmp.z());
      }

      public void emit(GizmoPrimitives gizmoPrimitives, float f) {
         int slices = 8;
         int capStacks = 3;
         Vec3[][] left = new Vec3[capStacks + 1][slices];
         Vec3[][] right = new Vec3[capStacks + 1][slices];

         for (int s = 0; s <= capStacks; s++) {
            double t = (double)s / (double)capStacks;
            double alpha = t * (Math.PI / 2);
            double sinA = Math.sin(alpha);
            double cosA = Math.cos(alpha);
            double ringRadius = (double)this.radius * cosA;
            double offset = (double)this.radius * sinA;
            float xLeftBase = -this.halfHeight;
            float xRightBase = this.halfHeight;

            for (int slice = 0; slice < slices; slice++) {
               double u = (double)slice / (double)slices;
               double theta = u * (Math.PI * 2);
               double cosT = Math.cos(theta);
               double sinT = Math.sin(theta);
               float y = (float)(ringRadius * cosT);
               float z = (float)(ringRadius * sinT);
               float xLeft = (float)((double)xLeftBase - offset);
               float xRight = (float)((double)xRightBase + offset);
               left[s][slice] = this.transform(xLeft, y, z);
               right[s][slice] = this.transform(xRight, y, z);
            }
         }

         if (this.style.hasFill()) {
            int fillColor = this.style.multipliedFill(f);

            for (int slice = 0; slice < slices; slice++) {
               int nextSlice = (slice + 1) % slices;
               Vec3 l0 = left[0][slice];
               Vec3 l1 = left[0][nextSlice];
               Vec3 r1 = right[0][nextSlice];
               Vec3 r0 = right[0][slice];
               gizmoPrimitives.addQuad(l0, l1, r1, r0, fillColor);
            }

            for (int s = 0; s < capStacks; s++) {
               for (int slice = 0; slice < slices; slice++) {
                  int nextSlice = (slice + 1) % slices;
                  Vec3 v00 = left[s][slice];
                  Vec3 v01 = left[s][nextSlice];
                  Vec3 v11 = left[s + 1][nextSlice];
                  Vec3 v10 = left[s + 1][slice];
                  gizmoPrimitives.addQuad(v00, v01, v11, v10, fillColor);
               }
            }

            for (int s = 0; s < capStacks; s++) {
               for (int slice = 0; slice < slices; slice++) {
                  int nextSlice = (slice + 1) % slices;
                  Vec3 v00 = right[s][slice];
                  Vec3 v01 = right[s][nextSlice];
                  Vec3 v11 = right[s + 1][nextSlice];
                  Vec3 v10 = right[s + 1][slice];
                  gizmoPrimitives.addQuad(v00, v01, v11, v10, fillColor);
               }
            }
         }

         if (this.style.hasStroke()) {
            int strokeColor = this.style.multipliedStroke(f);
            float width = this.style.strokeWidth();

            for (int slice = 0; slice < slices; slice++) {
               gizmoPrimitives.addLine(left[0][slice], right[0][slice], strokeColor, width);
            }

            for (int s = 0; s <= capStacks; s++) {
               for (int slice = 0; slice < slices; slice++) {
                  Vec3 a = left[s][slice];
                  Vec3 b = left[s][(slice + 1) % slices];
                  gizmoPrimitives.addLine(a, b, strokeColor, width);
               }
            }

            for (int s = 0; s <= capStacks; s++) {
               for (int slice = 0; slice < slices; slice++) {
                  Vec3 a = right[s][slice];
                  Vec3 b = right[s][(slice + 1) % slices];
                  gizmoPrimitives.addLine(a, b, strokeColor, width);
               }
            }
         }
      }
   }

   public static record TransformedConvexMeshGizmo(Matrix4fc transformation, Vector3f[][] fans, GizmoStyle style) implements Gizmo {
      public TransformedConvexMeshGizmo(Matrix4fc transformation, PxConvexMeshGeometry geometry, GizmoStyle style) {
         this(transformation, createGeometryFans(geometry), style);
      }

      private static Vector3f[][] createGeometryFans(PxConvexMeshGeometry geometry) {
         PxConvexMesh mesh = geometry.getConvexMesh();
         int polygonCount = mesh.getNbPolygons();
         if (polygonCount == 0) {
            return null;
         } else {
            PxHullPolygon polygon = new PxHullPolygon();
            PxU8ConstPtr indices = mesh.getIndexBuffer();
            PxVec3 vertices = mesh.getVertices();
            Vector3f[][] fans = new Vector3f[polygonCount][];

            for (int p = 0; p < polygonCount; p++) {
               mesh.getPolygonData(p, polygon);
               int firstIndex = polygon.getMIndexBase();
               int vertCount = polygon.getMNbVerts();
               if (vertCount >= 3) {
                  Vector3f[] fan = new Vector3f[vertCount];

                  for (int i = 0; i < vertCount; i++) {
                     int vertexIndex = NativeArrayHelpers.getU8At(indices, firstIndex + i);
                     PxVec3 v = NativeArrayHelpers.getVec3At(vertices, vertexIndex);
                     fan[i] = new Vector3f(v.getX(), v.getY(), v.getZ());
                  }

                  fans[p] = fan;
               }
            }

            polygon.destroy();
            return fans;
         }
      }

      private Vec3 transform(float x, float y, float z) {
         Vector3f tmp = new Vector3f();
         this.transformation.transformPosition(x, y, z, tmp);
         return new Vec3((double)tmp.x(), (double)tmp.y(), (double)tmp.z());
      }

      public void emit(GizmoPrimitives gizmoPrimitives, float f) {
         if (this.fans != null) {
            int fillColor = this.style.hasFill() ? this.style.multipliedFill(f) : 0;
            int strokeColor = this.style.hasStroke() ? this.style.multipliedStroke(f) : 0;
            float width = this.style.strokeWidth();

            for (int p = 0; p < this.fans.length; p++) {
               Vector3f[] fan = this.fans[p];
               if (fan != null) {
                  Vec3[] transformed = new Vec3[fan.length];

                  for (int i = 0; i < fan.length; i++) {
                     Vector3f v = fan[i];
                     transformed[i] = this.transform(v.x(), v.y(), v.z());
                  }

                  if (this.style.hasFill()) {
                     gizmoPrimitives.addTriangleFan(transformed, fillColor);
                  }

                  if (this.style.hasStroke()) {
                     for (int i = 0; i < transformed.length; i++) {
                        Vec3 a = transformed[i];
                        Vec3 b = transformed[(i + 1) % transformed.length];
                        gizmoPrimitives.addLine(a, b, strokeColor, width);
                     }
                  }
               }
            }
         }
      }
   }

   public static record TransformedCuboidGizmo(Matrix4fc transformation, AABB aabb, GizmoStyle style, boolean coloredCornerStroke) implements Gizmo {
      private Vec3 transform(double x, double y, double z) {
         Vector3f tmp = new Vector3f();
         this.transformation.transformPosition((float)x, (float)y, (float)z, tmp);
         return new Vec3((double)tmp.x(), (double)tmp.y(), (double)tmp.z());
      }

      public void emit(GizmoPrimitives gizmoPrimitives, float f) {
         double minX = this.aabb.minX;
         double minY = this.aabb.minY;
         double minZ = this.aabb.minZ;
         double maxX = this.aabb.maxX;
         double maxY = this.aabb.maxY;
         double maxZ = this.aabb.maxZ;
         if (this.style.hasFill()) {
            int k = this.style.multipliedFill(f);
            gizmoPrimitives.addQuad(
               this.transform(maxX, minY, minZ), this.transform(maxX, maxY, minZ), this.transform(maxX, maxY, maxZ), this.transform(maxX, minY, maxZ), k
            );
            gizmoPrimitives.addQuad(
               this.transform(minX, minY, minZ), this.transform(minX, minY, maxZ), this.transform(minX, maxY, maxZ), this.transform(minX, maxY, minZ), k
            );
            gizmoPrimitives.addQuad(
               this.transform(minX, minY, minZ), this.transform(minX, maxY, minZ), this.transform(maxX, maxY, minZ), this.transform(maxX, minY, minZ), k
            );
            gizmoPrimitives.addQuad(
               this.transform(minX, minY, maxZ), this.transform(maxX, minY, maxZ), this.transform(maxX, maxY, maxZ), this.transform(minX, maxY, maxZ), k
            );
            gizmoPrimitives.addQuad(
               this.transform(minX, maxY, minZ), this.transform(minX, maxY, maxZ), this.transform(maxX, maxY, maxZ), this.transform(maxX, maxY, minZ), k
            );
            gizmoPrimitives.addQuad(
               this.transform(minX, minY, minZ), this.transform(maxX, minY, minZ), this.transform(maxX, minY, maxZ), this.transform(minX, minY, maxZ), k
            );
         }

         if (this.style.hasStroke()) {
            int k = this.style.multipliedStroke(f);
            gizmoPrimitives.addLine(
               this.transform(minX, minY, minZ),
               this.transform(maxX, minY, minZ),
               this.coloredCornerStroke ? ARGB.multiply(k, -34953) : k,
               this.style.strokeWidth()
            );
            gizmoPrimitives.addLine(
               this.transform(minX, minY, minZ),
               this.transform(minX, maxY, minZ),
               this.coloredCornerStroke ? ARGB.multiply(k, -8913033) : k,
               this.style.strokeWidth()
            );
            gizmoPrimitives.addLine(
               this.transform(minX, minY, minZ),
               this.transform(minX, minY, maxZ),
               this.coloredCornerStroke ? ARGB.multiply(k, -8947713) : k,
               this.style.strokeWidth()
            );
            gizmoPrimitives.addLine(this.transform(maxX, minY, minZ), this.transform(maxX, maxY, minZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(maxX, maxY, minZ), this.transform(minX, maxY, minZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(minX, maxY, minZ), this.transform(minX, maxY, maxZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(minX, maxY, maxZ), this.transform(minX, minY, maxZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(minX, minY, maxZ), this.transform(maxX, minY, maxZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(maxX, minY, maxZ), this.transform(maxX, minY, minZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(minX, maxY, maxZ), this.transform(maxX, maxY, maxZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(maxX, minY, maxZ), this.transform(maxX, maxY, maxZ), k, this.style.strokeWidth());
            gizmoPrimitives.addLine(this.transform(maxX, maxY, minZ), this.transform(maxX, maxY, maxZ), k, this.style.strokeWidth());
         }
      }
   }

   public static record TransformedSphereGizmo(Matrix4fc transformation, float radius, GizmoStyle style) implements Gizmo {
      private Vec3 transform(float x, float y, float z) {
         Vector3f tmp = new Vector3f();
         this.transformation.transformPosition(x, y, z, tmp);
         return new Vec3((double)tmp.x(), (double)tmp.y(), (double)tmp.z());
      }

      public void emit(GizmoPrimitives gizmoPrimitives, float f) {
         int slices = 6;
         int stacks = 4;
         Vec3[][] grid = new Vec3[stacks + 1][slices];

         for (int stack = 0; stack <= stacks; stack++) {
            double v = (double)stack / (double)stacks;
            double phi = (-Math.PI / 2) + v * Math.PI;
            double cosPhi = Math.cos(phi);
            double sinPhi = Math.sin(phi);

            for (int slice = 0; slice < slices; slice++) {
               double u = (double)slice / (double)slices;
               double theta = u * (Math.PI * 2);
               double cosTheta = Math.cos(theta);
               double sinTheta = Math.sin(theta);
               float x = (float)((double)this.radius * cosPhi * cosTheta);
               float y = (float)((double)this.radius * sinPhi);
               float z = (float)((double)this.radius * cosPhi * sinTheta);
               grid[stack][slice] = this.transform(x, y, z);
            }
         }

         if (this.style.hasFill()) {
            int fillColor = this.style.multipliedFill(f);

            for (int stack = 0; stack < stacks; stack++) {
               for (int slice = 0; slice < slices; slice++) {
                  int nextSlice = (slice + 1) % slices;
                  Vec3 v00 = grid[stack][slice];
                  Vec3 v01 = grid[stack][nextSlice];
                  Vec3 v11 = grid[stack + 1][nextSlice];
                  Vec3 v10 = grid[stack + 1][slice];
                  gizmoPrimitives.addQuad(v00, v01, v11, v10, fillColor);
               }
            }
         }

         if (this.style.hasStroke()) {
            int strokeColor = this.style.multipliedStroke(f);
            float width = this.style.strokeWidth();

            for (int slice = 0; slice < slices; slice++) {
               for (int stack = 0; stack < stacks; stack++) {
                  gizmoPrimitives.addLine(grid[stack][slice], grid[stack + 1][slice], strokeColor, width);
               }
            }

            for (int stack = 1; stack < stacks; stack++) {
               for (int slice = 0; slice < slices; slice++) {
                  Vec3 a = grid[stack][slice];
                  Vec3 b = grid[stack][(slice + 1) % slices];
                  gizmoPrimitives.addLine(a, b, strokeColor, width);
               }
            }
         }
      }
   }
}
