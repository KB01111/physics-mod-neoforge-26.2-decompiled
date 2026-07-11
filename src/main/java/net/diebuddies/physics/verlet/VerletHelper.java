package net.diebuddies.physics.verlet;

import org.joml.Math;
import org.joml.Vector3d;

public class VerletHelper {
   private double[] dist = new double[6];

   public boolean movePointOutOfBox(Vector3d point, double preferUpMovement, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY && point.z > minZ && point.z < maxZ) {
         this.dist[0] = point.x - minX;
         this.dist[1] = maxX - point.x;
         this.dist[2] = point.y - minY;
         this.dist[3] = (maxY - point.y) * preferUpMovement;
         this.dist[4] = point.z - minZ;
         this.dist[5] = maxZ - point.z;
         double smallest = this.dist[0];
         int index = 0;

         for (int i = 1; i < this.dist.length; i++) {
            if (this.dist[i] < smallest) {
               smallest = this.dist[i];
               index = i;
            }
         }

         if (index == 0) {
            point.x = minX;
         } else if (index == 1) {
            point.x = maxX;
         } else if (index == 2) {
            point.y = minY;
         } else if (index == 3) {
            point.y = maxY;
         } else if (index == 4) {
            point.z = minZ;
         } else if (index == 5) {
            point.z = maxZ;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean movePointOutOfBoxFaster(Vector3d point, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      if (point.x > (double)minX
         && point.x < (double)maxX
         && point.y > (double)minY
         && point.y < (double)maxY
         && point.z > (double)minZ
         && point.z < (double)maxZ) {
         double[] dist = new double[]{
            point.x - (double)minX, (double)maxX - point.x, point.y - (double)minY, (double)maxY - point.y, point.z - (double)minZ, (double)maxZ - point.z
         };
         double smallest = dist[0];
         int index = 0;

         for (int i = 1; i < dist.length; i++) {
            if (dist[i] < smallest) {
               smallest = dist[i];
               index = i;
            }
         }

         if (index == 0) {
            point.x -= smallest;
         } else if (index == 1) {
            point.x += smallest;
         } else if (index == 2) {
            point.y -= smallest;
         } else if (index == 3) {
            point.y += smallest;
         } else if (index == 4) {
            point.z -= smallest;
         } else if (index == 5) {
            point.z += smallest;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean movePointOutOfBox(Vector3d point, float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
      if (point.x > (double)minX
         && point.x < (double)maxX
         && point.y > (double)minY
         && point.y < (double)maxY
         && point.z > (double)minZ
         && point.z < (double)maxZ) {
         double dx = Math.min(point.x - (double)minX, (double)maxX - point.x);
         double dy = Math.min(point.y - (double)minY, (double)maxY - point.y);
         double dz = Math.min(point.z - (double)minZ, (double)maxZ - point.z);
         if (dx <= dy && dx <= dz) {
            point.x = point.x < (double)(minX + maxX) * 0.5 ? (double)minX : (double)maxX;
         } else if (dy <= dx && dy <= dz) {
            point.y = point.y < (double)(minY + maxY) * 0.5 ? (double)minY : (double)maxY;
         } else {
            point.z = point.z < (double)(minZ + maxZ) * 0.5 ? (double)minZ : (double)maxZ;
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean movePointOutOfBox(Vector3d prevPoint, Vector3d point, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      if (point.x > minX && point.x < maxX && point.y > minY && point.y < maxY && point.z > minZ && point.z < maxZ) {
         this.dist[0] = point.x - minX;
         this.dist[1] = maxX - point.x;
         this.dist[2] = point.y - minY;
         this.dist[3] = maxY - point.y;
         this.dist[4] = point.z - minZ;
         this.dist[5] = maxZ - point.z;
         Vector3d rayOrigin = new Vector3d(prevPoint);
         Vector3d rayDir = new Vector3d(point).sub(prevPoint);
         double lengthSquared = rayDir.lengthSquared();
         if (lengthSquared != 0.0) {
            double length = java.lang.Math.sqrt(lengthSquared);
            rayDir.div(length);
            Vector3d boxMin = new Vector3d(minX, minY, minZ);
            Vector3d boxMax = new Vector3d(maxX, maxY, maxZ);
            double dist = intersectAABB(rayOrigin, rayDir, boxMin, boxMax);
            if (!Double.isNaN(dist) && dist < length && dist > 0.0) {
               point.set(rayOrigin.add(rayDir.mul(dist - 0.01F)));
               return true;
            }
         }

         double smallest = this.dist[0];
         int index = 0;

         for (int i = 1; i < this.dist.length; i++) {
            if (this.dist[i] < smallest) {
               smallest = this.dist[i];
               index = i;
            }
         }

         if (index == 0) {
            point.x = minX;
         } else if (index == 1) {
            point.x = maxX;
         } else if (index == 2) {
            point.y = minY;
         } else if (index == 3) {
            point.y = maxY;
         } else if (index == 4) {
            point.z = minZ;
         } else if (index == 5) {
            point.z = maxZ;
         }

         return true;
      } else {
         return false;
      }
   }

   public static double intersectAABB(Vector3d rayOrigin, Vector3d rayDir, Vector3d boxMin, Vector3d boxMax) {
      double invraydirx = 1.0 / rayDir.x;
      double invraydiry = 1.0 / rayDir.y;
      double invraydirz = 1.0 / rayDir.z;
      double tminx = (boxMin.x - rayOrigin.x) * invraydirx;
      double tminy = (boxMin.y - rayOrigin.y) * invraydiry;
      double tminz = (boxMin.z - rayOrigin.z) * invraydirz;
      double tmaxx = (boxMax.x - rayOrigin.x) * invraydirx;
      double tmaxy = (boxMax.y - rayOrigin.y) * invraydiry;
      double tmaxz = (boxMax.z - rayOrigin.z) * invraydirz;
      double t1x = java.lang.Math.min(tminx, tmaxx);
      double t1y = java.lang.Math.min(tminy, tmaxy);
      double t1z = java.lang.Math.min(tminz, tmaxz);
      double t2x = java.lang.Math.max(tminx, tmaxx);
      double t2y = java.lang.Math.max(tminy, tmaxy);
      double t2z = java.lang.Math.max(tminz, tmaxz);
      double near = java.lang.Math.max(java.lang.Math.max(t1x, t1y), t1z);
      double far = java.lang.Math.min(java.lang.Math.min(t2x, t2y), t2z);
      if (far < 0.0) {
         return Double.NaN;
      } else {
         return near > far ? Double.NaN : near;
      }
   }
}
