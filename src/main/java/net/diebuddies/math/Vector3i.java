package net.diebuddies.math;

import org.joml.Vector3d;

public class Vector3i {
   public int x;
   public int y;
   public int z;

   public Vector3i(Vector3i v) {
      this.x = v.x;
      this.y = v.y;
      this.z = v.z;
   }

   public Vector3i(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
   }

   public Vector3i(float x, float y, float z) {
      this.x = (int)x;
      this.y = (int)y;
      this.z = (int)z;
   }

   public Vector3i(double x, double y, double z) {
      this.x = (int)x;
      this.y = (int)y;
      this.z = (int)z;
   }

   public Vector3i() {
      this.x = 0;
      this.y = 0;
      this.z = 0;
   }

   public Vector3i add(Vector3i v) {
      return this.add(v, this);
   }

   public Vector3i add(Vector3i v, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x + v.x;
      dst.y = this.y + v.y;
      dst.z = this.z + v.z;
      return dst;
   }

   public Vector3i add(int x, int y, int z) {
      return this.add(x, y, z, this);
   }

   public Vector3i add(int x, int y, int z, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x + x;
      dst.y = this.y + y;
      dst.z = this.z + z;
      return dst;
   }

   public Vector3i add(int i, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x + i;
      dst.y = this.y + i;
      dst.z = this.z + i;
      return dst;
   }

   public Vector3i sub(Vector3i v) {
      this.x = this.x - v.x;
      this.y = this.y - v.y;
      this.z = this.z - v.z;
      return this;
   }

   public Vector3i sub(Vector3i v, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x - v.x;
      dst.y = this.y - v.y;
      dst.z = this.z - v.z;
      return dst;
   }

   public Vector3i sub(int v, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x - v;
      dst.y = this.y - v;
      dst.z = this.z - v;
      return dst;
   }

   public float dot(Vector3i v) {
      return (float)(this.x * v.x + this.y * v.y + this.z * v.z);
   }

   public Vector3i cross(Vector3i v) {
      Vector3i tmp = new Vector3i();
      tmp.x = this.y * v.z - v.y * this.z;
      tmp.y = this.z * v.x - v.z * this.x;
      tmp.z = this.x * v.y - v.x * this.y;
      return tmp;
   }

   public double distance(Vector3i v) {
      int tmpX = this.x - v.x;
      int tmpY = this.y - v.y;
      int tmpZ = this.z - v.z;
      return this.length(tmpX, tmpY, tmpZ);
   }

   public int distanceSquared(Vector3i v) {
      int tmpX = this.x - v.x;
      int tmpY = this.y - v.y;
      int tmpZ = this.z - v.z;
      return this.lengthSquared(tmpX, tmpY, tmpZ);
   }

   private double length(int x, int y, int z) {
      return java.lang.Math.sqrt((double)(x * x + y * y + z * z));
   }

   public double length() {
      return java.lang.Math.sqrt((double)this.lengthSquared());
   }

   public int lengthSquared(int x, int y, int z) {
      return x * x + y * y + z * z;
   }

   public int lengthSquared() {
      return this.x * this.x + this.y * this.y + this.z * this.z;
   }

   public Vector3i normalize() {
      double invLength = 1.0 / this.length();
      this.x = (int)((double)this.x * invLength);
      this.y = (int)((double)this.y * invLength);
      this.z = (int)((double)this.z * invLength);
      return this;
   }

   public Vector3i mul(int scalar) {
      this.x *= scalar;
      this.y *= scalar;
      this.z *= scalar;
      return this;
   }

   public Vector3i div(int scalar) {
      this.x /= scalar;
      this.y /= scalar;
      this.z /= scalar;
      return this;
   }

   public Vector3i mul(Vector3i mul, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x * mul.x;
      dst.y = this.y * mul.y;
      dst.z = this.z * mul.z;
      return dst;
   }

   public Vector3i mul(int scalar, Vector3i dst) {
      if (dst == null) {
         dst = new Vector3i();
      }

      dst.x = this.x * scalar;
      dst.y = this.y * scalar;
      dst.z = this.z * scalar;
      return dst;
   }

   public Vector3i set(Vector3i v) {
      return this.set(v.x, v.y, v.z);
   }

   public Vector3i set(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
      return this;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getZ() {
      return this.z;
   }

   public void setZ(int z) {
      this.z = z;
   }

   public Vector3d toDouble() {
      return new Vector3d((double)this.x, (double)this.y, (double)this.z);
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + this.x;
      result = 31 * result + this.y;
      return 31 * result + this.z;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else {
         return !(obj instanceof Vector3i v) ? false : v.x == this.x && v.y == this.y && v.z == this.z;
      }
   }

   public boolean equals(int x, int y, int z) {
      return this.x == x && this.y == y && this.z == z;
   }

   @Override
   public String toString() {
      return "Vector3i [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
   }
}
