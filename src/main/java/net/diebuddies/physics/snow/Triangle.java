package net.diebuddies.physics.snow;

import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.Vector3d;

public class Triangle {
   public Vector3d p0;
   public Vector3d p1;
   public Vector3d p2;
   public Object a0;
   public Object a1;
   public Object a2;

   public Triangle(Vector3d p0, Vector3d p1, Vector3d p2) {
      this.p0 = p0;
      this.p1 = p1;
      this.p2 = p2;
   }

   public Vector3d calculateNormal() {
      Vector3d d1 = this.p1.sub(this.p0, new Vector3d());
      Vector3d d2 = this.p2.sub(this.p0, new Vector3d());
      return d1.cross(d2, new Vector3d()).normalize();
   }

   public static boolean intersectsSchwarzSeidel(
      Vector3d p0, Vector3d p1, Vector3d p2, double startX, double startY, double startZ, double endX, double endY, double endZ
   ) {
      double v0x = (p0.x - startX) / (endX - startX);
      double v0y = (p0.y - startY) / (endY - startY);
      double v0z = (p0.z - startZ) / (endZ - startZ);
      double v1x = (p1.x - startX) / (endX - startX);
      double v1y = (p1.y - startY) / (endY - startY);
      double v1z = (p1.z - startZ) / (endZ - startZ);
      double v2x = (p2.x - startX) / (endX - startX);
      double v2y = (p2.y - startY) / (endY - startY);
      double v2z = (p2.z - startZ) / (endZ - startZ);
      double e0x = v1x - v0x;
      double e0y = v1y - v0y;
      double e0z = v1z - v0z;
      double e1x = v2x - v1x;
      double e1y = v2y - v1y;
      double e1z = v2z - v1z;
      double e2x = v0x - v2x;
      double e2y = v0y - v2y;
      double e2z = v0z - v2z;
      double normx = e0y * e1z - e0z * e1y;
      double normy = e0z * e1x - e0x * e1z;
      double normz = e0x * e1y - e0y * e1x;
      double invLength = 1.0 / Vector3d.length(normx, normy, normz);
      normx *= invLength;
      normy *= invLength;
      normz *= invLength;
      double r = 0.5 * Math.abs(normx) + 0.5 * Math.abs(normy) + 0.5 * Math.abs(normz);
      double s = normx * (0.5 - v0x) + normy * (0.5 - v0y) + normz * (0.5 - v0z);
      if (Math.abs(s) > r) {
         return false;
      } else {
         double cx = normx >= 0.0 ? 1.0 : -1.0;
         double cy = normy >= 0.0 ? 1.0 : -1.0;
         double cz = normz >= 0.0 ? 1.0 : -1.0;
         if (-(-e0y * v0x + e0x * v0y) * cz + Math.max(0.0, -e0y * cz) + Math.max(0.0, e0x * cz) < 0.0) {
            return false;
         } else if (-(-e1y * v1x + e1x * v1y) * cz + Math.max(0.0, -e1y * cz) + Math.max(0.0, e1x * cz) < 0.0) {
            return false;
         } else if (-(-e2y * v2x + e2x * v2y) * cz + Math.max(0.0, -e2y * cz) + Math.max(0.0, e2x * cz) < 0.0) {
            return false;
         } else if (-(-e0x * v0z + e0z * v0x) * cy + Math.max(0.0, -e0x * cy) + Math.max(0.0, e0z * cy) < 0.0) {
            return false;
         } else if (-(-e1x * v1z + e1z * v1x) * cy + Math.max(0.0, -e1x * cy) + Math.max(0.0, e1z * cy) < 0.0) {
            return false;
         } else if (-(-e2x * v2z + e2z * v2x) * cy + Math.max(0.0, -e2x * cy) + Math.max(0.0, e2z * cy) < 0.0) {
            return false;
         } else if (-(-e0z * v0y + e0y * v0z) * cx + Math.max(0.0, -e0z * cx) + Math.max(0.0, e0y * cx) < 0.0) {
            return false;
         } else {
            return -(-e1z * v1y + e1y * v1z) * cx + Math.max(0.0, -e1z * cx) + Math.max(0.0, e1y * cx) < 0.0
               ? false
               : !(-(-e2z * v2y + e2y * v2z) * cx + Math.max(0.0, -e2z * cx) + Math.max(0.0, e2y * cx) < 0.0);
         }
      }
   }

   public static boolean intersects(Vector3d p0, Vector3d p1, Vector3d p2, double startX, double startY, double startZ, double endX, double endY, double endZ) {
      double centerx = (endX + startX) * 0.5;
      double centery = (endY + startY) * 0.5;
      double centerz = (endZ + startZ) * 0.5;
      double halfSizex = (endX - startX) * 0.5;
      double halfSizey = (endY - startY) * 0.5;
      double halfSizez = (endZ - startZ) * 0.5;
      double v0x = p0.x - centerx;
      double v0y = p0.y - centery;
      double v0z = p0.z - centerz;
      double v1x = p1.x - centerx;
      double v1y = p1.y - centery;
      double v1z = p1.z - centerz;
      double v2x = p2.x - centerx;
      double v2y = p2.y - centery;
      double v2z = p2.z - centerz;
      double e0x = v1x - v0x;
      double e0y = v1y - v0y;
      double e0z = v1z - v0z;
      double invLength = 1.0 / Vector3d.length(e0x, e0y, e0z);
      e0x *= invLength;
      e0y *= invLength;
      e0z *= invLength;
      double e1x = v2x - v1x;
      double e1y = v2y - v1y;
      double e1z = v2z - v1z;
      invLength = 1.0 / Vector3d.length(e1x, e1y, e1z);
      e1x *= invLength;
      e1y *= invLength;
      e1z *= invLength;
      double e2x = v0x - v2x;
      double e2y = v0y - v2y;
      double e2z = v0z - v2z;
      invLength = 1.0 / Vector3d.length(e2x, e2y, e2z);
      e2x *= invLength;
      e2y *= invLength;
      e2z *= invLength;
      double fex = Math.abs(e0x);
      double fey = Math.abs(e0y);
      double fez = Math.abs(e0z);
      if (!axisTextX1(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e0z, e0y, fez, fey)) {
         return false;
      } else if (!axisTextY2(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e0z, e0x, fez, fex)) {
         return false;
      } else if (!axisTestZ2(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e0y, e0x, fey, fex)) {
         return false;
      } else {
         fex = Math.abs(e1x);
         fey = Math.abs(e1y);
         fez = Math.abs(e1z);
         if (!axisTextX1(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e1z, e1y, fez, fey)) {
            return false;
         } else if (!axisTextY2(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e1z, e1x, fez, fex)) {
            return false;
         } else if (!axisTestZ1(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e1y, e1x, fey, fex)) {
            return false;
         } else {
            fex = Math.abs(e2x);
            fey = Math.abs(e2y);
            fez = Math.abs(e2z);
            if (!axisTextX2(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e2z, e2y, fez, fey)) {
               return false;
            } else if (!axisTextY1(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e2z, e2x, fez, fex)) {
               return false;
            } else if (!axisTestZ2(v0x, v0y, v0z, v1x, v1y, v1z, v2x, v2y, v2z, halfSizex, halfSizey, halfSizez, e2y, e2x, fey, fex)) {
               return false;
            } else {
               float min = (float)Math.min(v0x, Math.min(v1x, v2x));
               float max = (float)Math.max(v0x, Math.max(v1x, v2x));
               if (!((double)min > halfSizex) && !((double)max < -halfSizex)) {
                  min = (float)Math.min(v0y, Math.min(v1y, v2y));
                  max = (float)Math.max(v0y, Math.max(v1y, v2y));
                  if (!((double)min > halfSizey) && !((double)max < -halfSizey)) {
                     min = (float)Math.min(v0z, Math.min(v1z, v2z));
                     max = (float)Math.max(v0z, Math.max(v1z, v2z));
                     if (!((double)min > halfSizez) && !((double)max < -halfSizez)) {
                        double normalx = e0y * e1z - e0z * e1y;
                        double normaly = e0z * e1x - e0x * e1z;
                        double normalz = e0x * e1y - e0y * e1x;
                        return planeBoxIntersect(normalx, normaly, normalz, v0x, v0y, v0z, halfSizex, halfSizey, halfSizez);
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            }
         }
      }
   }

   public static boolean intersects(Vector3d p0, Vector3d p1, Vector3d p2, AABB3D aabb) {
      return intersects(p0, p1, p2, aabb.start.x, aabb.start.y, aabb.start.z, aabb.end.x, aabb.end.y, aabb.end.z);
   }

   private static boolean planeBoxIntersect(
      double normalx, double normaly, double normalz, double vertx, double verty, double vertz, double maxboxx, double maxboxy, double maxboxz
   ) {
      double vminx = 0.0;
      double vminy = 0.0;
      double vminz = 0.0;
      double vmaxx = 0.0;
      double vmaxy = 0.0;
      double vmaxz = 0.0;
      if (normalx > 0.0) {
         vminx = -maxboxx - vertx;
         vmaxx = maxboxx - vertx;
      } else {
         vminx = maxboxx - vertx;
         vmaxx = -maxboxx - vertx;
      }

      if (normaly > 0.0) {
         vminy = -maxboxy - verty;
         vmaxy = maxboxy - verty;
      } else {
         vminy = maxboxy - verty;
         vmaxy = -maxboxy - verty;
      }

      if (normalz > 0.0) {
         vminz = -maxboxz - vertz;
         vmaxz = maxboxz - vertz;
      } else {
         vminz = maxboxz - vertz;
         vmaxz = -maxboxz - vertz;
      }

      return dot(normalx, normaly, normalz, vminx, vminy, vminz) > 0.0 ? false : dot(normalx, normaly, normalz, vmaxx, vmaxy, vmaxz) >= 0.0;
   }

   private static double dot(double vx, double vy, double vz, double x, double y, double z) {
      return vx * x + vy * y + vz * z;
   }

   private static boolean axisTextX1(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double px = a * v0y - b * v0z;
      double pz = a * v2y - b * v2z;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (px < pz) {
         minMaxRadx = px;
         minMaxRady = pz;
      } else {
         minMaxRadx = pz;
         minMaxRady = px;
      }

      minMaxRadz = fa * halfSizey + fb * halfSizez;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   private static boolean axisTextX2(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double px = a * v0y - b * v0z;
      double py = a * v1y - b * v1z;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (px < py) {
         minMaxRadx = px;
         minMaxRady = py;
      } else {
         minMaxRadx = py;
         minMaxRady = px;
      }

      minMaxRadz = fa * halfSizey + fb * halfSizez;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   private static boolean axisTextY2(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double px = -a * v0x + b * v0z;
      double pz = -a * v2x + b * v2z;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (px < pz) {
         minMaxRadx = px;
         minMaxRady = pz;
      } else {
         minMaxRadx = pz;
         minMaxRady = px;
      }

      minMaxRadz = fa * halfSizex + fb * halfSizez;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   private static boolean axisTextY1(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double px = -a * v0x + b * v0z;
      double py = -a * v1x + b * v1z;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (px < py) {
         minMaxRadx = px;
         minMaxRady = py;
      } else {
         minMaxRadx = py;
         minMaxRady = px;
      }

      minMaxRadz = fa * halfSizex + fb * halfSizez;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   private static boolean axisTestZ2(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double py = a * v1x - b * v1y;
      double pz = a * v2x - b * v2y;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (pz < py) {
         minMaxRadx = pz;
         minMaxRady = py;
      } else {
         minMaxRadx = py;
         minMaxRady = pz;
      }

      minMaxRadz = fa * halfSizex + fb * halfSizey;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   private static boolean axisTestZ1(
      double v0x,
      double v0y,
      double v0z,
      double v1x,
      double v1y,
      double v1z,
      double v2x,
      double v2y,
      double v2z,
      double halfSizex,
      double halfSizey,
      double halfSizez,
      double a,
      double b,
      double fa,
      double fb
   ) {
      double px = a * v0x - b * v0y;
      double py = a * v1x - b * v1y;
      double minMaxRadx = 0.0;
      double minMaxRady = 0.0;
      double minMaxRadz = 0.0;
      if (px < py) {
         minMaxRadx = px;
         minMaxRady = py;
      } else {
         minMaxRadx = py;
         minMaxRady = px;
      }

      minMaxRadz = fa * halfSizex + fb * halfSizey;
      return !(minMaxRadx > minMaxRadz) && !(minMaxRady < -minMaxRadz);
   }

   @Override
   public String toString() {
      return "Triangle [p0=" + this.p0 + ", p1=" + this.p1 + ", p2=" + this.p2 + "]";
   }
}
