package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

class PositionSolverManifold {
   public final Vec2 normal = new Vec2();
   public final Vec2 point = new Vec2();
   public float separation;

   public void initialize(ContactPositionConstraint pc, Transform xfA, Transform xfB, int index) {
      assert pc.pointCount > 0;

      Rot xfAq = xfA.q;
      Rot xfBq = xfB.q;
      Vec2 pcLocalPointsI = pc.localPoints[index];
      switch (pc.type) {
         case CIRCLES: {
            Vec2 plocalPoint = pc.localPoint;
            Vec2 pLocalPoints0 = pc.localPoints[0];
            float pointAx = xfAq.c * plocalPoint.x - xfAq.s * plocalPoint.y + xfA.p.x;
            float pointAy = xfAq.s * plocalPoint.x + xfAq.c * plocalPoint.y + xfA.p.y;
            float pointBx = xfBq.c * pLocalPoints0.x - xfBq.s * pLocalPoints0.y + xfB.p.x;
            float pointBy = xfBq.s * pLocalPoints0.x + xfBq.c * pLocalPoints0.y + xfB.p.y;
            this.normal.x = pointBx - pointAx;
            this.normal.y = pointBy - pointAy;
            this.normal.normalize();
            this.point.x = (pointAx + pointBx) * 0.5F;
            this.point.y = (pointAy + pointBy) * 0.5F;
            float tempx = pointBx - pointAx;
            float tempy = pointBy - pointAy;
            this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
            break;
         }
         case FACE_A: {
            Vec2 pcLocalNormal = pc.localNormal;
            Vec2 pcLocalPoint = pc.localPoint;
            this.normal.x = xfAq.c * pcLocalNormal.x - xfAq.s * pcLocalNormal.y;
            this.normal.y = xfAq.s * pcLocalNormal.x + xfAq.c * pcLocalNormal.y;
            float planePointx = xfAq.c * pcLocalPoint.x - xfAq.s * pcLocalPoint.y + xfA.p.x;
            float planePointy = xfAq.s * pcLocalPoint.x + xfAq.c * pcLocalPoint.y + xfA.p.y;
            float clipPointx = xfBq.c * pcLocalPointsI.x - xfBq.s * pcLocalPointsI.y + xfB.p.x;
            float clipPointy = xfBq.s * pcLocalPointsI.x + xfBq.c * pcLocalPointsI.y + xfB.p.y;
            float tempx = clipPointx - planePointx;
            float tempy = clipPointy - planePointy;
            this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
            this.point.x = clipPointx;
            this.point.y = clipPointy;
            break;
         }
         case FACE_B: {
            Vec2 pcLocalNormal = pc.localNormal;
            Vec2 pcLocalPoint = pc.localPoint;
            this.normal.x = xfBq.c * pcLocalNormal.x - xfBq.s * pcLocalNormal.y;
            this.normal.y = xfBq.s * pcLocalNormal.x + xfBq.c * pcLocalNormal.y;
            float planePointx = xfBq.c * pcLocalPoint.x - xfBq.s * pcLocalPoint.y + xfB.p.x;
            float planePointy = xfBq.s * pcLocalPoint.x + xfBq.c * pcLocalPoint.y + xfB.p.y;
            float clipPointx = xfAq.c * pcLocalPointsI.x - xfAq.s * pcLocalPointsI.y + xfA.p.x;
            float clipPointy = xfAq.s * pcLocalPointsI.x + xfAq.c * pcLocalPointsI.y + xfA.p.y;
            float tempx = clipPointx - planePointx;
            float tempy = clipPointy - planePointy;
            this.separation = tempx * this.normal.x + tempy * this.normal.y - pc.radiusA - pc.radiusB;
            this.point.x = clipPointx;
            this.point.y = clipPointy;
            this.normal.x *= -1.0F;
            this.normal.y *= -1.0F;
         }
      }
   }
}
