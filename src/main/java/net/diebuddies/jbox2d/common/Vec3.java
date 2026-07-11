package net.diebuddies.jbox2d.common;

import java.io.Serializable;

public class Vec3 implements Serializable {
   private static final long serialVersionUID = 1L;
   public float x;
   public float y;
   public float z;

   public Vec3() {
      this.x = this.y = this.z = 0.0F;
   }

   public Vec3(float argX, float argY, float argZ) {
      this.x = argX;
      this.y = argY;
      this.z = argZ;
   }

   public Vec3(Vec3 copy) {
      this.x = copy.x;
      this.y = copy.y;
      this.z = copy.z;
   }

   public Vec3 set(Vec3 vec) {
      this.x = vec.x;
      this.y = vec.y;
      this.z = vec.z;
      return this;
   }

   public Vec3 set(float argX, float argY, float argZ) {
      this.x = argX;
      this.y = argY;
      this.z = argZ;
      return this;
   }

   public Vec3 addLocal(Vec3 argVec) {
      this.x = this.x + argVec.x;
      this.y = this.y + argVec.y;
      this.z = this.z + argVec.z;
      return this;
   }

   public Vec3 add(Vec3 argVec) {
      return new Vec3(this.x + argVec.x, this.y + argVec.y, this.z + argVec.z);
   }

   public Vec3 subLocal(Vec3 argVec) {
      this.x = this.x - argVec.x;
      this.y = this.y - argVec.y;
      this.z = this.z - argVec.z;
      return this;
   }

   public Vec3 sub(Vec3 argVec) {
      return new Vec3(this.x - argVec.x, this.y - argVec.y, this.z - argVec.z);
   }

   public Vec3 mulLocal(float argScalar) {
      this.x *= argScalar;
      this.y *= argScalar;
      this.z *= argScalar;
      return this;
   }

   public Vec3 mul(float argScalar) {
      return new Vec3(this.x * argScalar, this.y * argScalar, this.z * argScalar);
   }

   public Vec3 negate() {
      return new Vec3(-this.x, -this.y, -this.z);
   }

   public Vec3 negateLocal() {
      this.x = -this.x;
      this.y = -this.y;
      this.z = -this.z;
      return this;
   }

   public void setZero() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.z = 0.0F;
   }

   public Vec3 clone() {
      return new Vec3(this);
   }

   @Override
   public String toString() {
      return "(" + this.x + "," + this.y + "," + this.z + ")";
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + Float.floatToIntBits(this.x);
      result = 31 * result + Float.floatToIntBits(this.y);
      return 31 * result + Float.floatToIntBits(this.z);
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
         Vec3 other = (Vec3)obj;
         if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
         } else {
            return Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y) ? false : Float.floatToIntBits(this.z) == Float.floatToIntBits(other.z);
         }
      }
   }

   public static final float dot(Vec3 a, Vec3 b) {
      return a.x * b.x + a.y * b.y + a.z * b.z;
   }

   public static final Vec3 cross(Vec3 a, Vec3 b) {
      return new Vec3(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
   }

   public static final void crossToOut(Vec3 a, Vec3 b, Vec3 out) {
      float tempy = a.z * b.x - a.x * b.z;
      float tempz = a.x * b.y - a.y * b.x;
      out.x = a.y * b.z - a.z * b.y;
      out.y = tempy;
      out.z = tempz;
   }

   public static final void crossToOutUnsafe(Vec3 a, Vec3 b, Vec3 out) {
      assert out != b;

      assert out != a;

      out.x = a.y * b.z - a.z * b.y;
      out.y = a.z * b.x - a.x * b.z;
      out.z = a.x * b.y - a.y * b.x;
   }
}
