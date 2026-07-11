package net.diebuddies.physics.snow.math;

import java.util.List;
import net.diebuddies.physics.snow.Triangle;
import org.joml.Vector3d;

public class Ray {
   private static final double EPSILON = 1.0E-7;
   private Vector3d start;
   private Vector3d direction;

   public Ray(Vector3d start, Vector3d direction) {
      this.start = start;
      this.direction = direction;
   }

   public Vector3d intersect(Triangle triangle) {
      Vector3d e1 = triangle.p1.sub(triangle.p0, new Vector3d());
      Vector3d e2 = triangle.p2.sub(triangle.p0, new Vector3d());
      Vector3d h = this.direction.cross(e2, new Vector3d());
      double a = e1.dot(h);
      if (a > -1.0E-7 && a < 1.0E-7) {
         return null;
      } else {
         double f = 1.0 / a;
         Vector3d s = this.start.sub(triangle.p0, new Vector3d());
         double u = f * s.dot(h);
         if (!(u < 0.0) && !(u > 1.0)) {
            Vector3d q = s.cross(e1, new Vector3d());
            double v = f * this.direction.dot(q);
            if (!(v < 0.0) && !(u + v > 1.0)) {
               double t = f * e2.dot(q);
               return t > 1.0E-7 ? this.direction.mul(t, new Vector3d()).add(this.start) : null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }
   }

   public RayResult intersect(List<Triangle> mesh, boolean rayLengthAsMaxDistance) {
      RayResult result = new RayResult();
      double length = this.getDirection().length();

      for (Triangle t : mesh) {
         Vector3d hit = this.intersect(t);
         if (hit != null && (!rayLengthAsMaxDistance || hit.distance(this.getStart()) < length)) {
            result.addRayHit(new RayHit(t.calculateNormal(), hit));
         }
      }

      result.sortByDistance(this.start);
      return result;
   }

   public RayResult intersect(Vector3d position, Vector3d normal) {
      RayResult result = new RayResult();
      double denom = normal.dot(this.direction);
      if (Math.abs(denom) > 1.0E-5) {
         double t = position.sub(this.start, new Vector3d()).dot(normal) / denom;
         if (t >= 0.0) {
            result.addRayHit(new RayHit(new Vector3d(normal), new Vector3d(this.start).add(this.direction.mul(t, new Vector3d()))));
         }
      }

      return result;
   }

   public boolean intersectTest(List<Triangle> mesh, boolean rayLengthAsMaxDistance) {
      double length = this.getDirection().length();

      for (Triangle t : mesh) {
         Vector3d hit = this.intersect(t);
         if (hit != null && (!rayLengthAsMaxDistance || hit.distance(this.getStart()) < length)) {
            return true;
         }
      }

      return false;
   }

   public RayResult intersect(List<Triangle> mesh) {
      return this.intersect(mesh, false);
   }

   public RayResult intersect(BoundingSphere boundingSphere) {
      RayResult result = new RayResult();
      Vector3d centerToPoint = this.start.sub(boundingSphere.center, new Vector3d());
      Vector3d normalizedDirection = new Vector3d(this.direction).normalize();
      double b = centerToPoint.dot(normalizedDirection);
      double c = centerToPoint.dot(centerToPoint) - boundingSphere.radius * boundingSphere.radius;
      if (c > 0.0 && b > 0.0) {
         return result;
      } else {
         double discr = b * b - c;
         if (discr < 0.0) {
            return result;
         } else {
            double t = -b - Math.sqrt(discr);
            if (t < 0.0) {
               t = 0.0;
            }

            Vector3d hitPoint = this.start.add(normalizedDirection.mul(t, new Vector3d()), new Vector3d());
            result.addRayHit(new RayHit(boundingSphere.center.sub(hitPoint, new Vector3d()).normalize(), hitPoint));
            return result;
         }
      }
   }

   public Vector3d getStart() {
      return this.start;
   }

   public Vector3d getDirection() {
      return this.direction;
   }
}
