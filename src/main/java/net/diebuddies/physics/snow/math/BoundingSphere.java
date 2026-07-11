package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;
import org.joml.Vector3i;

public class BoundingSphere {
   public Vector3d center;
   public double radius;

   public BoundingSphere(Vector3d center, double radius) {
      this.center = center;
      this.radius = radius;
   }

   public BoundingSphere() {
      this(new Vector3d(), 0.0);
   }

   public boolean intersect(BoundingSphere sphere) {
      return this.distance(sphere.center) < this.radius + sphere.radius;
   }

   public double distance(Vector3d point) {
      return this.center.sub(point, new Vector3d()).length() - this.radius;
   }

   public void set(BoundingSphere bs) {
      this.center.set(bs.center);
      this.radius = bs.radius;
   }

   public void include(BoundingSphere bs) {
      double distance = this.center.distance(bs.center);
      if (distance + this.radius < bs.radius) {
         this.center.set(bs.center);
         this.radius = bs.radius;
      } else if (!(distance + bs.radius < this.radius)) {
         double newRadius = (this.radius + bs.radius + distance) / 2.0;
         double factor = (newRadius - this.radius) / distance;
         this.center.lerp(bs.center, factor, this.center);
         this.radius = newRadius;
      }
   }

   public boolean isInside(Vector3i min, int size) {
      if (this.center.x - this.radius < (double)min.x || this.center.x + this.radius > (double)(min.x + size)) {
         return false;
      } else {
         return this.center.y - this.radius < (double)min.y || this.center.y + this.radius > (double)(min.y + size)
            ? false
            : !(this.center.z - this.radius < (double)min.z) && !(this.center.z + this.radius > (double)(min.z + size));
      }
   }

   public boolean isInside(Vector3d point) {
      return this.center.distance(point) <= this.radius;
   }

   public Vector3d getMin() {
      return new Vector3d(this.center.x - this.radius, this.center.y - this.radius, this.center.z - this.radius);
   }

   @Override
   public String toString() {
      return "BoundingSphere [center=" + this.center + ", radius=" + this.radius + "]";
   }
}
