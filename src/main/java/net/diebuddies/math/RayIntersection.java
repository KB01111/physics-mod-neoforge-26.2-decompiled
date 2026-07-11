package net.diebuddies.math;

import org.joml.Vector3d;

public class RayIntersection {
   public static RayIntersection.IntersectionResult intersectAABB(Vector3d rayOrigin, Vector3d rayDir, Vector3d boxMin, Vector3d boxMax) {
      double rayDirInvX = 1.0 / rayDir.x;
      double rayDirInvY = 1.0 / rayDir.y;
      double rayDirInvZ = 1.0 / rayDir.z;
      double tx1 = (boxMin.x - rayOrigin.x) * rayDirInvX;
      double ty1 = (boxMin.y - rayOrigin.y) * rayDirInvY;
      double tz1 = (boxMin.z - rayOrigin.z) * rayDirInvZ;
      double tx2 = (boxMax.x - rayOrigin.x) * rayDirInvX;
      double ty2 = (boxMax.y - rayOrigin.y) * rayDirInvY;
      double tz2 = (boxMax.z - rayOrigin.z) * rayDirInvZ;
      double tmin = org.joml.Math.max(org.joml.Math.max(org.joml.Math.min(tx1, tx2), org.joml.Math.min(ty1, ty2)), org.joml.Math.min(tz1, tz2));
      double tmax = org.joml.Math.min(org.joml.Math.min(org.joml.Math.max(tx1, tx2), org.joml.Math.max(ty1, ty2)), org.joml.Math.max(tz1, tz2));
      if (tmax < 0.0 || tmin > tmax) {
         return new RayIntersection.IntersectionResult(false, tmax);
      } else {
         return !Double.isFinite(tmin) ? new RayIntersection.IntersectionResult(false, tmin) : new RayIntersection.IntersectionResult(true, tmin);
      }
   }

   public static class IntersectionResult {
      public boolean hit;
      public double fraction;

      public IntersectionResult(boolean hit, double fraction) {
         this.hit = hit;
         this.fraction = fraction;
      }
   }
}
