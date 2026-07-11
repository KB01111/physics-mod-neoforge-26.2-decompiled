package net.diebuddies.jbox2d.collision.shapes;

import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.pooling.arrays.IntArray;
import net.diebuddies.jbox2d.pooling.arrays.Vec2Array;

public class PolygonShape extends Shape {
   private static final boolean m_debug = false;
   public final Vec2 m_centroid = new Vec2();
   public final Vec2[] m_vertices;
   public final Vec2[] m_normals;
   public int m_count;
   private final Vec2 pool1 = new Vec2();
   private final Vec2 pool2 = new Vec2();
   private final Vec2 pool3 = new Vec2();
   private final Vec2 pool4 = new Vec2();
   private Transform poolt1 = new Transform();

   public PolygonShape() {
      super(ShapeType.POLYGON);
      this.m_count = 0;
      this.m_vertices = new Vec2[Settings.maxPolygonVertices];

      for (int i = 0; i < this.m_vertices.length; i++) {
         this.m_vertices[i] = new Vec2();
      }

      this.m_normals = new Vec2[Settings.maxPolygonVertices];

      for (int i = 0; i < this.m_normals.length; i++) {
         this.m_normals[i] = new Vec2();
      }

      this.setRadius(Settings.polygonRadius);
      this.m_centroid.setZero();
   }

   @Override
   public final Shape clone() {
      PolygonShape shape = new PolygonShape();
      shape.m_centroid.set(this.m_centroid);

      for (int i = 0; i < shape.m_normals.length; i++) {
         shape.m_normals[i].set(this.m_normals[i]);
         shape.m_vertices[i].set(this.m_vertices[i]);
      }

      shape.setRadius(this.getRadius());
      shape.m_count = this.m_count;
      return shape;
   }

   public final void set(Vec2[] vertices, int count) {
      this.set(vertices, count, null, null);
   }

   public final void set(Vec2[] verts, int num, Vec2Array vecPool, IntArray intPool) {
      assert 3 <= num && num <= Settings.maxPolygonVertices;

      if (num < 3) {
         this.setAsBox(1.0F, 1.0F);
      } else {
         int n = MathUtils.min(num, Settings.maxPolygonVertices);
         Vec2[] ps = vecPool != null ? vecPool.get(Settings.maxPolygonVertices) : new Vec2[Settings.maxPolygonVertices];
         int tempCount = 0;

         for (int i = 0; i < n; i++) {
            Vec2 v = verts[i];
            boolean unique = true;

            for (int j = 0; j < tempCount; j++) {
               if (MathUtils.distanceSquared(v, ps[j]) < 0.5F * Settings.linearSlop) {
                  unique = false;
                  break;
               }
            }

            if (unique) {
               ps[tempCount++] = v;
            }
         }

         n = tempCount;
         if (tempCount >= 3) {
            int i0 = 0;
            float x0 = ps[0].x;

            for (int i = 1; i < n; i++) {
               float x = ps[i].x;
               if (x > x0 || x == x0 && ps[i].y < ps[i0].y) {
                  i0 = i;
                  x0 = x;
               }
            }

            int[] hull = intPool != null ? intPool.get(Settings.maxPolygonVertices) : new int[Settings.maxPolygonVertices];
            int m = 0;
            int ih = i0;

            int ie;
            do {
               hull[m] = ih;
               ie = 0;

               for (int jx = 1; jx < n; jx++) {
                  if (ie == ih) {
                     ie = jx;
                  } else {
                     Vec2 r = this.pool1.set(ps[ie]).subLocal(ps[hull[m]]);
                     Vec2 v = this.pool2.set(ps[jx]).subLocal(ps[hull[m]]);
                     float c = Vec2.cross(r, v);
                     if (c < 0.0F) {
                        ie = jx;
                     }

                     if (c == 0.0F && v.lengthSquared() > r.lengthSquared()) {
                        ie = jx;
                     }
                  }
               }

               m++;
               ih = ie;
            } while (ie != i0);

            this.m_count = m;

            for (int ix = 0; ix < this.m_count; ix++) {
               if (this.m_vertices[ix] == null) {
                  this.m_vertices[ix] = new Vec2();
               }

               this.m_vertices[ix].set(ps[hull[ix]]);
            }

            Vec2 edge = this.pool1;

            for (int ix = 0; ix < this.m_count; ix++) {
               int i2 = ix + 1 < this.m_count ? ix + 1 : 0;
               edge.set(this.m_vertices[i2]).subLocal(this.m_vertices[ix]);

               assert edge.lengthSquared() > 1.4210855E-14F;

               Vec2.crossToOutUnsafe(edge, 1.0F, this.m_normals[ix]);
               this.m_normals[ix].normalize();
            }

            this.computeCentroidToOut(this.m_vertices, this.m_count, this.m_centroid);
         } else {
            assert false;

            this.setAsBox(1.0F, 1.0F);
         }
      }
   }

   public final void setAsBox(float hx, float hy) {
      this.m_count = 4;
      this.m_vertices[0].set(-hx, -hy);
      this.m_vertices[1].set(hx, -hy);
      this.m_vertices[2].set(hx, hy);
      this.m_vertices[3].set(-hx, hy);
      this.m_normals[0].set(0.0F, -1.0F);
      this.m_normals[1].set(1.0F, 0.0F);
      this.m_normals[2].set(0.0F, 1.0F);
      this.m_normals[3].set(-1.0F, 0.0F);
      this.m_centroid.setZero();
   }

   public final void setAsBox(float hx, float hy, Vec2 center, float angle) {
      this.m_count = 4;
      this.m_vertices[0].set(-hx, -hy);
      this.m_vertices[1].set(hx, -hy);
      this.m_vertices[2].set(hx, hy);
      this.m_vertices[3].set(-hx, hy);
      this.m_normals[0].set(0.0F, -1.0F);
      this.m_normals[1].set(1.0F, 0.0F);
      this.m_normals[2].set(0.0F, 1.0F);
      this.m_normals[3].set(-1.0F, 0.0F);
      this.m_centroid.set(center);
      Transform xf = this.poolt1;
      xf.p.set(center);
      xf.q.set(angle);

      for (int i = 0; i < this.m_count; i++) {
         Transform.mulToOut(xf, this.m_vertices[i], this.m_vertices[i]);
         Rot.mulToOut(xf.q, this.m_normals[i], this.m_normals[i]);
      }
   }

   @Override
   public int getChildCount() {
      return 1;
   }

   @Override
   public final boolean testPoint(Transform xf, Vec2 p) {
      Rot xfq = xf.q;
      float tempx = p.x - xf.p.x;
      float tempy = p.y - xf.p.y;
      float pLocalx = xfq.c * tempx + xfq.s * tempy;
      float pLocaly = -xfq.s * tempx + xfq.c * tempy;

      for (int i = 0; i < this.m_count; i++) {
         Vec2 vertex = this.m_vertices[i];
         Vec2 normal = this.m_normals[i];
         tempx = pLocalx - vertex.x;
         tempy = pLocaly - vertex.y;
         float dot = normal.x * tempx + normal.y * tempy;
         if (dot > 0.0F) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final void computeAABB(AABB aabb, Transform xf, int childIndex) {
      Vec2 lower = aabb.lowerBound;
      Vec2 upper = aabb.upperBound;
      Vec2 v1 = this.m_vertices[0];
      float xfqc = xf.q.c;
      float xfqs = xf.q.s;
      float xfpx = xf.p.x;
      float xfpy = xf.p.y;
      lower.x = xfqc * v1.x - xfqs * v1.y + xfpx;
      lower.y = xfqs * v1.x + xfqc * v1.y + xfpy;
      upper.x = lower.x;
      upper.y = lower.y;

      for (int i = 1; i < this.m_count; i++) {
         Vec2 v2 = this.m_vertices[i];
         float vx = xfqc * v2.x - xfqs * v2.y + xfpx;
         float vy = xfqs * v2.x + xfqc * v2.y + xfpy;
         lower.x = lower.x < vx ? lower.x : vx;
         lower.y = lower.y < vy ? lower.y : vy;
         upper.x = upper.x > vx ? upper.x : vx;
         upper.y = upper.y > vy ? upper.y : vy;
      }

      lower.x = lower.x - this.m_radius;
      lower.y = lower.y - this.m_radius;
      upper.x = upper.x + this.m_radius;
      upper.y = upper.y + this.m_radius;
   }

   public final int getVertexCount() {
      return this.m_count;
   }

   public final Vec2 getVertex(int index) {
      assert 0 <= index && index < this.m_count;

      return this.m_vertices[index];
   }

   @Override
   public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
      float xfqc = xf.q.c;
      float xfqs = xf.q.s;
      float tx = p.x - xf.p.x;
      float ty = p.y - xf.p.y;
      float pLocalx = xfqc * tx + xfqs * ty;
      float pLocaly = -xfqs * tx + xfqc * ty;
      float maxDistance = -Float.MAX_VALUE;
      float normalForMaxDistanceX = pLocalx;
      float normalForMaxDistanceY = pLocaly;

      for (int i = 0; i < this.m_count; i++) {
         Vec2 vertex = this.m_vertices[i];
         Vec2 normal = this.m_normals[i];
         tx = pLocalx - vertex.x;
         ty = pLocaly - vertex.y;
         float dot = normal.x * tx + normal.y * ty;
         if (dot > maxDistance) {
            maxDistance = dot;
            normalForMaxDistanceX = normal.x;
            normalForMaxDistanceY = normal.y;
         }
      }

      float distance;
      if (maxDistance > 0.0F) {
         float minDistanceX = normalForMaxDistanceX;
         float minDistanceY = normalForMaxDistanceY;
         float minDistance2 = maxDistance * maxDistance;

         for (int ix = 0; ix < this.m_count; ix++) {
            Vec2 vertex = this.m_vertices[ix];
            float distanceVecX = pLocalx - vertex.x;
            float distanceVecY = pLocaly - vertex.y;
            float distance2 = distanceVecX * distanceVecX + distanceVecY * distanceVecY;
            if (minDistance2 > distance2) {
               minDistanceX = distanceVecX;
               minDistanceY = distanceVecY;
               minDistance2 = distance2;
            }
         }

         distance = MathUtils.sqrt(minDistance2);
         normalOut.x = xfqc * minDistanceX - xfqs * minDistanceY;
         normalOut.y = xfqs * minDistanceX + xfqc * minDistanceY;
         normalOut.normalize();
      } else {
         distance = maxDistance;
         normalOut.x = xfqc * normalForMaxDistanceX - xfqs * normalForMaxDistanceY;
         normalOut.y = xfqs * normalForMaxDistanceX + xfqc * normalForMaxDistanceY;
      }

      return distance;
   }

   @Override
   public final boolean raycast(RayCastOutput output, RayCastInput input, Transform xf, int childIndex) {
      float xfqc = xf.q.c;
      float xfqs = xf.q.s;
      Vec2 xfp = xf.p;
      float tempx = input.p1.x - xfp.x;
      float tempy = input.p1.y - xfp.y;
      float p1x = xfqc * tempx + xfqs * tempy;
      float p1y = -xfqs * tempx + xfqc * tempy;
      tempx = input.p2.x - xfp.x;
      tempy = input.p2.y - xfp.y;
      float p2x = xfqc * tempx + xfqs * tempy;
      float p2y = -xfqs * tempx + xfqc * tempy;
      float dx = p2x - p1x;
      float dy = p2y - p1y;
      float lower = 0.0F;
      float upper = input.maxFraction;
      int index = -1;

      for (int i = 0; i < this.m_count; i++) {
         Vec2 normal = this.m_normals[i];
         Vec2 vertex = this.m_vertices[i];
         float tempxn = vertex.x - p1x;
         float tempyn = vertex.y - p1y;
         float numerator = normal.x * tempxn + normal.y * tempyn;
         float denominator = normal.x * dx + normal.y * dy;
         if (denominator == 0.0F) {
            if (numerator < 0.0F) {
               return false;
            }
         } else if (denominator < 0.0F && numerator < lower * denominator) {
            lower = numerator / denominator;
            index = i;
         } else if (denominator > 0.0F && numerator < upper * denominator) {
            upper = numerator / denominator;
         }

         if (upper < lower) {
            return false;
         }
      }

      assert 0.0F <= lower && lower <= input.maxFraction;

      if (index >= 0) {
         output.fraction = lower;
         Vec2 normalx = this.m_normals[index];
         Vec2 out = output.normal;
         out.x = xfqc * normalx.x - xfqs * normalx.y;
         out.y = xfqs * normalx.x + xfqc * normalx.y;
         return true;
      } else {
         return false;
      }
   }

   public final void computeCentroidToOut(Vec2[] vs, int count, Vec2 out) {
      assert count >= 3;

      out.set(0.0F, 0.0F);
      float area = 0.0F;
      Vec2 pRef = this.pool1;
      pRef.setZero();
      Vec2 e1 = this.pool2;
      Vec2 e2 = this.pool3;
      float inv3 = 0.33333334F;

      for (int i = 0; i < count; i++) {
         Vec2 p2 = vs[i];
         Vec2 p3 = i + 1 < count ? vs[i + 1] : vs[0];
         e1.set(p2).subLocal(pRef);
         e2.set(p3).subLocal(pRef);
         float D = Vec2.cross(e1, e2);
         float triangleArea = 0.5F * D;
         area += triangleArea;
         e1.set(pRef).addLocal(p2).addLocal(p3).mulLocal(triangleArea * 0.33333334F);
         out.addLocal(e1);
      }

      assert area > 1.1920929E-7F;

      out.mulLocal(1.0F / area);
   }

   @Override
   public void computeMass(MassData massData, float density) {
      assert this.m_count >= 3;

      Vec2 center = this.pool1;
      center.setZero();
      float area = 0.0F;
      float I = 0.0F;
      Vec2 s = this.pool2;
      s.setZero();

      for (int i = 0; i < this.m_count; i++) {
         s.addLocal(this.m_vertices[i]);
      }

      s.mulLocal(1.0F / (float)this.m_count);
      float k_inv3 = 0.33333334F;
      Vec2 e1 = this.pool3;
      Vec2 e2 = this.pool4;

      for (int i = 0; i < this.m_count; i++) {
         e1.set(this.m_vertices[i]).subLocal(s);
         e2.set(s).negateLocal().addLocal(i + 1 < this.m_count ? this.m_vertices[i + 1] : this.m_vertices[0]);
         float D = Vec2.cross(e1, e2);
         float triangleArea = 0.5F * D;
         area += triangleArea;
         center.x = center.x + triangleArea * 0.33333334F * (e1.x + e2.x);
         center.y = center.y + triangleArea * 0.33333334F * (e1.y + e2.y);
         float ex1 = e1.x;
         float ey1 = e1.y;
         float ex2 = e2.x;
         float ey2 = e2.y;
         float intx2 = ex1 * ex1 + ex2 * ex1 + ex2 * ex2;
         float inty2 = ey1 * ey1 + ey2 * ey1 + ey2 * ey2;
         I += 0.083333336F * D * (intx2 + inty2);
      }

      massData.mass = density * area;

      assert area > 1.1920929E-7F;

      center.mulLocal(1.0F / area);
      massData.center.set(center).addLocal(s);
      massData.I = I * density;
      massData.I = massData.I + massData.mass * Vec2.dot(massData.center, massData.center);
   }

   public boolean validate() {
      for (int i = 0; i < this.m_count; i++) {
         int i1 = i;
         int i2 = i < this.m_count - 1 ? i + 1 : 0;
         Vec2 p = this.m_vertices[i];
         Vec2 e = this.pool1.set(this.m_vertices[i2]).subLocal(p);

         for (int j = 0; j < this.m_count; j++) {
            if (j != i1 && j != i2) {
               Vec2 v = this.pool2.set(this.m_vertices[j]).subLocal(p);
               float c = Vec2.cross(e, v);
               if (c < 0.0F) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public Vec2[] getVertices() {
      return this.m_vertices;
   }

   public Vec2[] getNormals() {
      return this.m_normals;
   }

   public Vec2 centroid(Transform xf) {
      return Transform.mul(xf, this.m_centroid);
   }

   public Vec2 centroidToOut(Transform xf, Vec2 out) {
      Transform.mulToOutUnsafe(xf, this.m_centroid, out);
      return out;
   }
}
