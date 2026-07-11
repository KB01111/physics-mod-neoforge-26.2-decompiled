package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

public class WorldManifold {
   public final Vec2 normal;
   public final Vec2[] points;
   public final float[] separations;
   private final Vec2 pool3 = new Vec2();
   private final Vec2 pool4 = new Vec2();

   public WorldManifold() {
      this.normal = new Vec2();
      this.points = new Vec2[Settings.maxManifoldPoints];
      this.separations = new float[Settings.maxManifoldPoints];

      for (int i = 0; i < Settings.maxManifoldPoints; i++) {
         this.points[i] = new Vec2();
      }
   }

   public final void initialize(Manifold manifold, Transform xfA, float radiusA, Transform xfB, float radiusB) {
      if (manifold.pointCount != 0) {
         switch (manifold.type) {
            case CIRCLES: {
               Vec2 pointA = this.pool3;
               Vec2 pointB = this.pool4;
               this.normal.x = 1.0F;
               this.normal.y = 0.0F;
               Vec2 v = manifold.localPoint;
               pointA.x = xfA.q.c * v.x - xfA.q.s * v.y + xfA.p.x;
               pointA.y = xfA.q.s * v.x + xfA.q.c * v.y + xfA.p.y;
               Vec2 mp0p = manifold.points[0].localPoint;
               pointB.x = xfB.q.c * mp0p.x - xfB.q.s * mp0p.y + xfB.p.x;
               pointB.y = xfB.q.s * mp0p.x + xfB.q.c * mp0p.y + xfB.p.y;
               if (MathUtils.distanceSquared(pointA, pointB) > 1.4210855E-14F) {
                  this.normal.x = pointB.x - pointA.x;
                  this.normal.y = pointB.y - pointA.y;
                  this.normal.normalize();
               }

               float cAx = this.normal.x * radiusA + pointA.x;
               float cAy = this.normal.y * radiusA + pointA.y;
               float cBx = -this.normal.x * radiusB + pointB.x;
               float cBy = -this.normal.y * radiusB + pointB.y;
               this.points[0].x = (cAx + cBx) * 0.5F;
               this.points[0].y = (cAy + cBy) * 0.5F;
               this.separations[0] = (cBx - cAx) * this.normal.x + (cBy - cAy) * this.normal.y;
               break;
            }
            case FACE_A: {
               Vec2 planePoint = this.pool3;
               Rot.mulToOutUnsafe(xfA.q, manifold.localNormal, this.normal);
               Transform.mulToOut(xfA, manifold.localPoint, planePoint);
               Vec2 clipPoint = this.pool4;

               for (int i = 0; i < manifold.pointCount; i++) {
                  Transform.mulToOut(xfB, manifold.points[i].localPoint, clipPoint);
                  float scalar = radiusA - ((clipPoint.x - planePoint.x) * this.normal.x + (clipPoint.y - planePoint.y) * this.normal.y);
                  float cAx = this.normal.x * scalar + clipPoint.x;
                  float cAy = this.normal.y * scalar + clipPoint.y;
                  float cBx = -this.normal.x * radiusB + clipPoint.x;
                  float cBy = -this.normal.y * radiusB + clipPoint.y;
                  this.points[i].x = (cAx + cBx) * 0.5F;
                  this.points[i].y = (cAy + cBy) * 0.5F;
                  this.separations[i] = (cBx - cAx) * this.normal.x + (cBy - cAy) * this.normal.y;
               }
               break;
            }
            case FACE_B: {
               Vec2 planePoint = this.pool3;
               Rot.mulToOutUnsafe(xfB.q, manifold.localNormal, this.normal);
               Transform.mulToOut(xfB, manifold.localPoint, planePoint);
               Vec2 clipPoint = this.pool4;

               for (int i = 0; i < manifold.pointCount; i++) {
                  Transform.mulToOut(xfA, manifold.points[i].localPoint, clipPoint);
                  float scalar = radiusB - ((clipPoint.x - planePoint.x) * this.normal.x + (clipPoint.y - planePoint.y) * this.normal.y);
                  float cBx = this.normal.x * scalar + clipPoint.x;
                  float cBy = this.normal.y * scalar + clipPoint.y;
                  float cAx = -this.normal.x * radiusA + clipPoint.x;
                  float cAy = -this.normal.y * radiusA + clipPoint.y;
                  this.points[i].x = (cAx + cBx) * 0.5F;
                  this.points[i].y = (cAy + cBy) * 0.5F;
                  this.separations[i] = (cAx - cBx) * this.normal.x + (cAy - cBy) * this.normal.y;
               }

               this.normal.x = -this.normal.x;
               this.normal.y = -this.normal.y;
               break;
            }
         }
      }
   }
}
