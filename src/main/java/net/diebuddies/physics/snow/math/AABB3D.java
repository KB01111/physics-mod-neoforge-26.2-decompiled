package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;
import org.joml.Vector3i;

public class AABB3D {
   public Vector3d start;
   public Vector3d end;

   public AABB3D(Vector3d start, Vector3d end) {
      this.start = start;
      this.end = end;
   }

   public AABB3D(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      this.start = new Vector3d(minX, minY, minZ);
      this.end = new Vector3d(maxX, maxY, maxZ);
   }

   public AABB3D(Vector3d point) {
      this.start = new Vector3d(point);
      this.end = new Vector3d(point);
   }

   public AABB3D(BoundingSphere bs) {
      this.start = new Vector3d(bs.center).add(-bs.radius, -bs.radius, -bs.radius);
      this.end = new Vector3d(bs.center).add(bs.radius, bs.radius, bs.radius);
   }

   public void set(AABB3D aabb) {
      this.start.set(aabb.start);
      this.end.set(aabb.end);
   }

   @Override
   public String toString() {
      return "AABB3D [start=" + this.start.x + " " + this.start.y + " " + this.start.z + ", end=" + this.end.x + " " + this.end.y + " " + this.end.z + "]";
   }

   public boolean isInside(Vector3i point) {
      return this.isInside(point.x, point.y, point.z);
   }

   public boolean isInside(Vector3d point) {
      return this.isInside(point.x, point.y, point.z);
   }

   public boolean intersect(AABB3D aabb) {
      if (this.end.x < aabb.start.x) {
         return false;
      } else if (this.start.x > aabb.end.x) {
         return false;
      } else if (this.end.y < aabb.start.y) {
         return false;
      } else if (this.start.y > aabb.end.y) {
         return false;
      } else {
         return this.end.z < aabb.start.z ? false : !(this.start.z > aabb.end.z);
      }
   }

   public boolean intersect(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      if (this.end.x < startX) {
         return false;
      } else if (this.start.x > endX) {
         return false;
      } else if (this.end.y < startY) {
         return false;
      } else if (this.start.y > endY) {
         return false;
      } else {
         return this.end.z < startZ ? false : !(this.start.z > endZ);
      }
   }

   public boolean intersect(BoundingSphere boundingSphere) {
      return this.distanceSquared(boundingSphere.center) <= boundingSphere.radius * boundingSphere.radius;
   }

   public boolean intersect(Vector3d center, double radius) {
      return this.distanceSquared(center) <= radius * radius;
   }

   public double distanceSquared(Vector3d p) {
      return this.distanceSquared(p.x, p.y, p.z);
   }

   public double distanceSquared(double px, double py, double pz) {
      double squaredDistance = 0.0;
      if (px < this.start.x) {
         squaredDistance += (this.start.x - px) * (this.start.x - px);
      }

      if (px > this.end.x) {
         squaredDistance += (px - this.end.x) * (px - this.end.x);
      }

      if (py < this.start.y) {
         squaredDistance += (this.start.y - py) * (this.start.y - py);
      }

      if (py > this.end.y) {
         squaredDistance += (py - this.end.y) * (py - this.end.y);
      }

      if (pz < this.start.z) {
         squaredDistance += (this.start.z - pz) * (this.start.z - pz);
      }

      if (pz > this.end.z) {
         squaredDistance += (pz - this.end.z) * (pz - this.end.z);
      }

      return squaredDistance;
   }

   public boolean isInside(int x, int y, int z) {
      if (this.start.x > (double)x || this.end.x < (double)x) {
         return false;
      } else {
         return this.start.y > (double)y || this.end.y < (double)y ? false : !(this.start.z > (double)z) && !(this.end.z < (double)z);
      }
   }

   public boolean isInside(double x, double y, double z) {
      if (this.start.x > x || this.end.x < x) {
         return false;
      } else {
         return this.start.y > y || this.end.y < y ? false : !(this.start.z > z) && !(this.end.z < z);
      }
   }

   public static boolean isInside(double startX, double startY, double startZ, double endX, double endY, double endZ, double x, double y, double z) {
      if (startX > x || endX < x) {
         return false;
      } else {
         return startY > y || endY < y ? false : !(startZ > z) && !(endZ < z);
      }
   }

   public boolean contains(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      if (this.start.x > startX || this.end.x < startX) {
         return false;
      } else if (this.start.y > startY || this.end.y < startY) {
         return false;
      } else if (this.start.z > startZ || this.end.z < startZ) {
         return false;
      } else if (this.start.x > endX || this.end.x < endX) {
         return false;
      } else if (this.start.y > endY || this.end.y < endY) {
         return false;
      } else {
         return !(this.start.z > endZ) && !(this.end.z < endZ) ? false : false;
      }
   }

   public boolean contains(AABB3D aabb) {
      if (this.start.x > aabb.start.x || this.end.x < aabb.start.x) {
         return false;
      } else if (this.start.y > aabb.start.y || this.end.y < aabb.start.y) {
         return false;
      } else if (this.start.z > aabb.start.z || this.end.z < aabb.start.z) {
         return false;
      } else if (this.start.x > aabb.end.x || this.end.x < aabb.end.x) {
         return false;
      } else {
         return this.start.y > aabb.end.y || this.end.y < aabb.end.y ? false : !(this.start.z > aabb.end.z) && !(this.end.z < aabb.end.z);
      }
   }

   public void include(Vector3d point) {
      if (point.x < this.start.x) {
         this.start.x = point.x;
      }

      if (point.y < this.start.y) {
         this.start.y = point.y;
      }

      if (point.z < this.start.z) {
         this.start.z = point.z;
      }

      if (point.x > this.end.x) {
         this.end.x = point.x;
      }

      if (point.y > this.end.y) {
         this.end.y = point.y;
      }

      if (point.z > this.end.z) {
         this.end.z = point.z;
      }
   }

   public void include(double px, double py, double pz) {
      if (px < this.start.x) {
         this.start.x = px;
      }

      if (py < this.start.y) {
         this.start.y = py;
      }

      if (pz < this.start.z) {
         this.start.z = pz;
      }

      if (px > this.end.x) {
         this.end.x = px;
      }

      if (py > this.end.y) {
         this.end.y = py;
      }

      if (pz > this.end.z) {
         this.end.z = pz;
      }
   }

   public void include(AABB3D aabb3d) {
      this.include(aabb3d.start);
      this.include(aabb3d.end);
   }

   public double getWidth() {
      return this.end.x - this.start.x;
   }

   public double getHeight() {
      return this.end.y - this.start.y;
   }

   public double getDepth() {
      return this.end.z - this.start.z;
   }

   public boolean isInside(double startX, double startY, double startZ, double endX, double endY, double endZ) {
      return this.start.x >= startX
         && this.start.x <= endX
         && this.end.x >= startX
         && this.end.x <= endX
         && this.start.y >= startY
         && this.start.y <= endX
         && this.end.y >= startY
         && this.end.y <= endY
         && this.start.z >= startZ
         && this.start.z <= endZ
         && this.end.z >= startZ
         && this.end.z <= endZ;
   }

   public boolean isInside(Vector3i min, int size) {
      return this.isInside((double)min.x, (double)min.y, (double)min.z, (double)(min.x + size), (double)(min.y + size), (double)(min.z + size));
   }

   public Vector3d getMin() {
      return this.start;
   }

   public Vector3d getMax() {
      return this.end;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         AABB3D other = (AABB3D)obj;
         if (this.end == null) {
            if (other.end != null) {
               return false;
            }
         } else if (!this.end.equals(other.end)) {
            return false;
         }

         if (this.start == null) {
            if (other.start != null) {
               return false;
            }
         } else if (!this.start.equals(other.start)) {
            return false;
         }

         return true;
      }
   }
}
