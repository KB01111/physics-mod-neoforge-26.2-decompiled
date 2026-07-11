package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.pooling.IWorldPool;
import net.diebuddies.jbox2d.pooling.normal.DefaultWorldPool;

public class AABB {
   public final Vec2 lowerBound;
   public final Vec2 upperBound;

   public AABB() {
      this.lowerBound = new Vec2();
      this.upperBound = new Vec2();
   }

   public AABB(AABB copy) {
      this(copy.lowerBound, copy.upperBound);
   }

   public AABB(Vec2 lowerVertex, Vec2 upperVertex) {
      this.lowerBound = lowerVertex.clone();
      this.upperBound = upperVertex.clone();
   }

   public final void set(AABB aabb) {
      Vec2 v = aabb.lowerBound;
      this.lowerBound.x = v.x;
      this.lowerBound.y = v.y;
      Vec2 v1 = aabb.upperBound;
      this.upperBound.x = v1.x;
      this.upperBound.y = v1.y;
   }

   public final boolean isValid() {
      float dx = this.upperBound.x - this.lowerBound.x;
      if (dx < 0.0F) {
         return false;
      } else {
         float dy = this.upperBound.y - this.lowerBound.y;
         return dy < 0.0F ? false : this.lowerBound.isValid() && this.upperBound.isValid();
      }
   }

   public final Vec2 getCenter() {
      Vec2 center = new Vec2(this.lowerBound);
      center.addLocal(this.upperBound);
      center.mulLocal(0.5F);
      return center;
   }

   public final void getCenterToOut(Vec2 out) {
      out.x = (this.lowerBound.x + this.upperBound.x) * 0.5F;
      out.y = (this.lowerBound.y + this.upperBound.y) * 0.5F;
   }

   public final Vec2 getExtents() {
      Vec2 center = new Vec2(this.upperBound);
      center.subLocal(this.lowerBound);
      center.mulLocal(0.5F);
      return center;
   }

   public final void getExtentsToOut(Vec2 out) {
      out.x = (this.upperBound.x - this.lowerBound.x) * 0.5F;
      out.y = (this.upperBound.y - this.lowerBound.y) * 0.5F;
   }

   public final void getVertices(Vec2[] argRay) {
      argRay[0].set(this.lowerBound);
      argRay[1].set(this.lowerBound);
      argRay[1].x = argRay[1].x + (this.upperBound.x - this.lowerBound.x);
      argRay[2].set(this.upperBound);
      argRay[3].set(this.upperBound);
      argRay[3].x = argRay[3].x - (this.upperBound.x - this.lowerBound.x);
   }

   public final void combine(AABB aabb1, AABB aab) {
      this.lowerBound.x = aabb1.lowerBound.x < aab.lowerBound.x ? aabb1.lowerBound.x : aab.lowerBound.x;
      this.lowerBound.y = aabb1.lowerBound.y < aab.lowerBound.y ? aabb1.lowerBound.y : aab.lowerBound.y;
      this.upperBound.x = aabb1.upperBound.x > aab.upperBound.x ? aabb1.upperBound.x : aab.upperBound.x;
      this.upperBound.y = aabb1.upperBound.y > aab.upperBound.y ? aabb1.upperBound.y : aab.upperBound.y;
   }

   public final float getPerimeter() {
      return 2.0F * (this.upperBound.x - this.lowerBound.x + this.upperBound.y - this.lowerBound.y);
   }

   public final void combine(AABB aabb) {
      this.lowerBound.x = this.lowerBound.x < aabb.lowerBound.x ? this.lowerBound.x : aabb.lowerBound.x;
      this.lowerBound.y = this.lowerBound.y < aabb.lowerBound.y ? this.lowerBound.y : aabb.lowerBound.y;
      this.upperBound.x = this.upperBound.x > aabb.upperBound.x ? this.upperBound.x : aabb.upperBound.x;
      this.upperBound.y = this.upperBound.y > aabb.upperBound.y ? this.upperBound.y : aabb.upperBound.y;
   }

   public final boolean contains(AABB aabb) {
      return this.lowerBound.x <= aabb.lowerBound.x
         && this.lowerBound.y <= aabb.lowerBound.y
         && aabb.upperBound.x <= this.upperBound.x
         && aabb.upperBound.y <= this.upperBound.y;
   }

   public final boolean raycast(RayCastOutput output, RayCastInput input) {
      return this.raycast(output, input, new DefaultWorldPool(4, 4));
   }

   public final boolean raycast(RayCastOutput output, RayCastInput input, IWorldPool argPool) {
      float tmin = -Float.MAX_VALUE;
      float tmax = Float.MAX_VALUE;
      Vec2 p = argPool.popVec2();
      Vec2 d = argPool.popVec2();
      Vec2 absD = argPool.popVec2();
      Vec2 normal = argPool.popVec2();
      p.set(input.p1);
      d.set(input.p2).subLocal(input.p1);
      Vec2.absToOut(d, absD);
      if (absD.x < 1.1920929E-7F) {
         if (p.x < this.lowerBound.x || this.upperBound.x < p.x) {
            argPool.pushVec2(4);
            return false;
         }
      } else {
         float inv_d = 1.0F / d.x;
         float t1 = (this.lowerBound.x - p.x) * inv_d;
         float t2 = (this.upperBound.x - p.x) * inv_d;
         float s = -1.0F;
         if (t1 > t2) {
            float temp = t1;
            t1 = t2;
            t2 = temp;
            s = 1.0F;
         }

         if (t1 > tmin) {
            normal.setZero();
            normal.x = s;
            tmin = t1;
         }

         tmax = MathUtils.min(tmax, t2);
         if (tmin > tmax) {
            argPool.pushVec2(4);
            return false;
         }
      }

      if (absD.y < 1.1920929E-7F) {
         if (p.y < this.lowerBound.y || this.upperBound.y < p.y) {
            argPool.pushVec2(4);
            return false;
         }
      } else {
         float inv_dx = 1.0F / d.y;
         float t1x = (this.lowerBound.y - p.y) * inv_dx;
         float t2x = (this.upperBound.y - p.y) * inv_dx;
         float sx = -1.0F;
         if (t1x > t2x) {
            float temp = t1x;
            t1x = t2x;
            t2x = temp;
            sx = 1.0F;
         }

         if (t1x > tmin) {
            normal.setZero();
            normal.y = sx;
            tmin = t1x;
         }

         tmax = MathUtils.min(tmax, t2x);
         if (tmin > tmax) {
            argPool.pushVec2(4);
            return false;
         }
      }

      if (!(tmin < 0.0F) && !(input.maxFraction < tmin)) {
         output.fraction = tmin;
         output.normal.x = normal.x;
         output.normal.y = normal.y;
         argPool.pushVec2(4);
         return true;
      } else {
         argPool.pushVec2(4);
         return false;
      }
   }

   public static final boolean testOverlap(AABB a, AABB b) {
      return b.lowerBound.x - a.upperBound.x > 0.0F || b.lowerBound.y - a.upperBound.y > 0.0F
         ? false
         : !(a.lowerBound.x - b.upperBound.x > 0.0F) && !(a.lowerBound.y - b.upperBound.y > 0.0F);
   }

   @Override
   public final String toString() {
      return "AABB[" + this.lowerBound + " . " + this.upperBound + "]";
   }
}
