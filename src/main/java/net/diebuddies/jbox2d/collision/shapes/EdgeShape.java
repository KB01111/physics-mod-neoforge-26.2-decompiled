package net.diebuddies.jbox2d.collision.shapes;

import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

public class EdgeShape extends Shape {
   public final Vec2 m_vertex1 = new Vec2();
   public final Vec2 m_vertex2 = new Vec2();
   public final Vec2 m_vertex0 = new Vec2();
   public final Vec2 m_vertex3 = new Vec2();
   public boolean m_hasVertex0 = false;
   public boolean m_hasVertex3 = false;
   private final Vec2 normal = new Vec2();

   public EdgeShape() {
      super(ShapeType.EDGE);
      this.m_radius = Settings.polygonRadius;
   }

   @Override
   public int getChildCount() {
      return 1;
   }

   public void set(Vec2 v1, Vec2 v2) {
      this.m_vertex1.set(v1);
      this.m_vertex2.set(v2);
      this.m_hasVertex0 = this.m_hasVertex3 = false;
   }

   @Override
   public boolean testPoint(Transform xf, Vec2 p) {
      return false;
   }

   @Override
   public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
      float xfqc = xf.q.c;
      float xfqs = xf.q.s;
      float xfpx = xf.p.x;
      float xfpy = xf.p.y;
      float v1x = xfqc * this.m_vertex1.x - xfqs * this.m_vertex1.y + xfpx;
      float v1y = xfqs * this.m_vertex1.x + xfqc * this.m_vertex1.y + xfpy;
      float v2x = xfqc * this.m_vertex2.x - xfqs * this.m_vertex2.y + xfpx;
      float v2y = xfqs * this.m_vertex2.x + xfqc * this.m_vertex2.y + xfpy;
      float dx = p.x - v1x;
      float dy = p.y - v1y;
      float sx = v2x - v1x;
      float sy = v2y - v1y;
      float ds = dx * sx + dy * sy;
      if (ds > 0.0F) {
         float s2 = sx * sx + sy * sy;
         if (ds > s2) {
            dx = p.x - v2x;
            dy = p.y - v2y;
         } else {
            dx -= ds / s2 * sx;
            dy -= ds / s2 * sy;
         }
      }

      float d1 = MathUtils.sqrt(dx * dx + dy * dy);
      if (d1 > 0.0F) {
         normalOut.x = 1.0F / d1 * dx;
         normalOut.y = 1.0F / d1 * dy;
      } else {
         normalOut.x = 0.0F;
         normalOut.y = 0.0F;
      }

      return d1;
   }

   @Override
   public boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
      Vec2 v1 = this.m_vertex1;
      Vec2 v2 = this.m_vertex2;
      Rot xfq = xf.q;
      Vec2 xfp = xf.p;
      float tempx = input.p1.x - xfp.x;
      float tempy = input.p1.y - xfp.y;
      float p1x = xfq.c * tempx + xfq.s * tempy;
      float p1y = -xfq.s * tempx + xfq.c * tempy;
      tempx = input.p2.x - xfp.x;
      tempy = input.p2.y - xfp.y;
      float p2x = xfq.c * tempx + xfq.s * tempy;
      float p2y = -xfq.s * tempx + xfq.c * tempy;
      float dx = p2x - p1x;
      float dy = p2y - p1y;
      this.normal.x = v2.y - v1.y;
      this.normal.y = v1.x - v2.x;
      this.normal.normalize();
      float normalx = this.normal.x;
      float normaly = this.normal.y;
      tempx = v1.x - p1x;
      tempy = v1.y - p1y;
      float numerator = normalx * tempx + normaly * tempy;
      float denominator = normalx * dx + normaly * dy;
      if (denominator == 0.0F) {
         return false;
      } else {
         float t = numerator / denominator;
         if (!(t < 0.0F) && !(1.0F < t)) {
            float qx = p1x + t * dx;
            float qy = p1y + t * dy;
            float rx = v2.x - v1.x;
            float ry = v2.y - v1.y;
            float rr = rx * rx + ry * ry;
            if (rr == 0.0F) {
               return false;
            } else {
               tempx = qx - v1.x;
               tempy = qy - v1.y;
               float s = (tempx * rx + tempy * ry) / rr;
               if (!(s < 0.0F) && !(1.0F < s)) {
                  output.fraction = t;
                  if (numerator > 0.0F) {
                     output.normal.x = -xfq.c * this.normal.x + xfq.s * this.normal.y;
                     output.normal.y = -xfq.s * this.normal.x - xfq.c * this.normal.y;
                  } else {
                     output.normal.x = xfq.c * this.normal.x - xfq.s * this.normal.y;
                     output.normal.y = xfq.s * this.normal.x + xfq.c * this.normal.y;
                  }

                  return true;
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      }
   }

   @Override
   public void computeAABB(AABB aabb, Transform xf, int childIndex) {
      Vec2 lowerBound = aabb.lowerBound;
      Vec2 upperBound = aabb.upperBound;
      Rot xfq = xf.q;
      float v1x = xfq.c * this.m_vertex1.x - xfq.s * this.m_vertex1.y + xf.p.x;
      float v1y = xfq.s * this.m_vertex1.x + xfq.c * this.m_vertex1.y + xf.p.y;
      float v2x = xfq.c * this.m_vertex2.x - xfq.s * this.m_vertex2.y + xf.p.x;
      float v2y = xfq.s * this.m_vertex2.x + xfq.c * this.m_vertex2.y + xf.p.y;
      lowerBound.x = v1x < v2x ? v1x : v2x;
      lowerBound.y = v1y < v2y ? v1y : v2y;
      upperBound.x = v1x > v2x ? v1x : v2x;
      upperBound.y = v1y > v2y ? v1y : v2y;
      lowerBound.x = lowerBound.x - this.m_radius;
      lowerBound.y = lowerBound.y - this.m_radius;
      upperBound.x = upperBound.x + this.m_radius;
      upperBound.y = upperBound.y + this.m_radius;
   }

   @Override
   public void computeMass(MassData massData, float density) {
      massData.mass = 0.0F;
      massData.center.set(this.m_vertex1).addLocal(this.m_vertex2).mulLocal(0.5F);
      massData.I = 0.0F;
   }

   @Override
   public Shape clone() {
      EdgeShape edge = new EdgeShape();
      edge.m_radius = this.m_radius;
      edge.m_hasVertex0 = this.m_hasVertex0;
      edge.m_hasVertex3 = this.m_hasVertex3;
      edge.m_vertex0.set(this.m_vertex0);
      edge.m_vertex1.set(this.m_vertex1);
      edge.m_vertex2.set(this.m_vertex2);
      edge.m_vertex3.set(this.m_vertex3);
      return edge;
   }
}
