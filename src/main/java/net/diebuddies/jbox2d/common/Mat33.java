package net.diebuddies.jbox2d.common;

import java.io.Serializable;

public class Mat33 implements Serializable {
   private static final long serialVersionUID = 2L;
   public static final Mat33 IDENTITY = new Mat33(new Vec3(1.0F, 0.0F, 0.0F), new Vec3(0.0F, 1.0F, 0.0F), new Vec3(0.0F, 0.0F, 1.0F));
   public final Vec3 ex;
   public final Vec3 ey;
   public final Vec3 ez;

   public Mat33() {
      this.ex = new Vec3();
      this.ey = new Vec3();
      this.ez = new Vec3();
   }

   public Mat33(float exx, float exy, float exz, float eyx, float eyy, float eyz, float ezx, float ezy, float ezz) {
      this.ex = new Vec3(exx, exy, exz);
      this.ey = new Vec3(eyx, eyy, eyz);
      this.ez = new Vec3(ezx, ezy, ezz);
   }

   public Mat33(Vec3 argCol1, Vec3 argCol2, Vec3 argCol3) {
      this.ex = argCol1.clone();
      this.ey = argCol2.clone();
      this.ez = argCol3.clone();
   }

   public void setZero() {
      this.ex.setZero();
      this.ey.setZero();
      this.ez.setZero();
   }

   public void set(float exx, float exy, float exz, float eyx, float eyy, float eyz, float ezx, float ezy, float ezz) {
      this.ex.x = exx;
      this.ex.y = exy;
      this.ex.z = exz;
      this.ey.x = eyx;
      this.ey.y = eyy;
      this.ey.z = eyz;
      this.ez.x = eyx;
      this.ez.y = eyy;
      this.ez.z = eyz;
   }

   public void set(Mat33 mat) {
      Vec3 vec = mat.ex;
      this.ex.x = vec.x;
      this.ex.y = vec.y;
      this.ex.z = vec.z;
      Vec3 vec1 = mat.ey;
      this.ey.x = vec1.x;
      this.ey.y = vec1.y;
      this.ey.z = vec1.z;
      Vec3 vec2 = mat.ez;
      this.ez.x = vec2.x;
      this.ez.y = vec2.y;
      this.ez.z = vec2.z;
   }

   public void setIdentity() {
      this.ex.x = 1.0F;
      this.ex.y = 0.0F;
      this.ex.z = 0.0F;
      this.ey.x = 0.0F;
      this.ey.y = 1.0F;
      this.ey.z = 0.0F;
      this.ez.x = 0.0F;
      this.ez.y = 0.0F;
      this.ez.z = 1.0F;
   }

   public static final Vec3 mul(Mat33 A, Vec3 v) {
      return new Vec3(v.x * A.ex.x + v.y * A.ey.x + v.z + A.ez.x, v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y, v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z);
   }

   public static final Vec2 mul22(Mat33 A, Vec2 v) {
      return new Vec2(A.ex.x * v.x + A.ey.x * v.y, A.ex.y * v.x + A.ey.y * v.y);
   }

   public static final void mul22ToOut(Mat33 A, Vec2 v, Vec2 out) {
      float tempx = A.ex.x * v.x + A.ey.x * v.y;
      out.y = A.ex.y * v.x + A.ey.y * v.y;
      out.x = tempx;
   }

   public static final void mul22ToOutUnsafe(Mat33 A, Vec2 v, Vec2 out) {
      assert v != out;

      out.y = A.ex.y * v.x + A.ey.y * v.y;
      out.x = A.ex.x * v.x + A.ey.x * v.y;
   }

   public static final void mulToOut(Mat33 A, Vec3 v, Vec3 out) {
      float tempy = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
      float tempz = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
      out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
      out.y = tempy;
      out.z = tempz;
   }

   public static final void mulToOutUnsafe(Mat33 A, Vec3 v, Vec3 out) {
      assert out != v;

      out.x = v.x * A.ex.x + v.y * A.ey.x + v.z * A.ez.x;
      out.y = v.x * A.ex.y + v.y * A.ey.y + v.z * A.ez.y;
      out.z = v.x * A.ex.z + v.y * A.ey.z + v.z * A.ez.z;
   }

   public final Vec2 solve22(Vec2 b) {
      Vec2 x = new Vec2();
      this.solve22ToOut(b, x);
      return x;
   }

   public final void solve22ToOut(Vec2 b, Vec2 out) {
      float a11 = this.ex.x;
      float a12 = this.ey.x;
      float a21 = this.ex.y;
      float a22 = this.ey.y;
      float det = a11 * a22 - a12 * a21;
      if (det != 0.0F) {
         det = 1.0F / det;
      }

      out.x = det * (a22 * b.x - a12 * b.y);
      out.y = det * (a11 * b.y - a21 * b.x);
   }

   public final Vec3 solve33(Vec3 b) {
      Vec3 x = new Vec3();
      this.solve33ToOut(b, x);
      return x;
   }

   public final void solve33ToOut(Vec3 b, Vec3 out) {
      assert b != out;

      Vec3.crossToOutUnsafe(this.ey, this.ez, out);
      float det = Vec3.dot(this.ex, out);
      if (det != 0.0F) {
         det = 1.0F / det;
      }

      Vec3.crossToOutUnsafe(this.ey, this.ez, out);
      float x = det * Vec3.dot(b, out);
      Vec3.crossToOutUnsafe(b, this.ez, out);
      float y = det * Vec3.dot(this.ex, out);
      Vec3.crossToOutUnsafe(this.ey, b, out);
      float z = det * Vec3.dot(this.ex, out);
      out.x = x;
      out.y = y;
      out.z = z;
   }

   public void getInverse22(Mat33 M) {
      float a = this.ex.x;
      float b = this.ey.x;
      float c = this.ex.y;
      float d = this.ey.y;
      float det = a * d - b * c;
      if (det != 0.0F) {
         det = 1.0F / det;
      }

      M.ex.x = det * d;
      M.ey.x = -det * b;
      M.ex.z = 0.0F;
      M.ex.y = -det * c;
      M.ey.y = det * a;
      M.ey.z = 0.0F;
      M.ez.x = 0.0F;
      M.ez.y = 0.0F;
      M.ez.z = 0.0F;
   }

   public void getSymInverse33(Mat33 M) {
      float bx = this.ey.y * this.ez.z - this.ey.z * this.ez.y;
      float by = this.ey.z * this.ez.x - this.ey.x * this.ez.z;
      float bz = this.ey.x * this.ez.y - this.ey.y * this.ez.x;
      float det = this.ex.x * bx + this.ex.y * by + this.ex.z * bz;
      if (det != 0.0F) {
         det = 1.0F / det;
      }

      float a11 = this.ex.x;
      float a12 = this.ey.x;
      float a13 = this.ez.x;
      float a22 = this.ey.y;
      float a23 = this.ez.y;
      float a33 = this.ez.z;
      M.ex.x = det * (a22 * a33 - a23 * a23);
      M.ex.y = det * (a13 * a23 - a12 * a33);
      M.ex.z = det * (a12 * a23 - a13 * a22);
      M.ey.x = M.ex.y;
      M.ey.y = det * (a11 * a33 - a13 * a13);
      M.ey.z = det * (a13 * a12 - a11 * a23);
      M.ez.x = M.ex.z;
      M.ez.y = M.ey.z;
      M.ez.z = det * (a11 * a22 - a12 * a12);
   }

   public static final void setScaleTransform(float scale, Mat33 out) {
      out.ex.x = scale;
      out.ey.y = scale;
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      result = 31 * result + (this.ex == null ? 0 : this.ex.hashCode());
      result = 31 * result + (this.ey == null ? 0 : this.ey.hashCode());
      return 31 * result + (this.ez == null ? 0 : this.ez.hashCode());
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
         Mat33 other = (Mat33)obj;
         if (this.ex == null) {
            if (other.ex != null) {
               return false;
            }
         } else if (!this.ex.equals(other.ex)) {
            return false;
         }

         if (this.ey == null) {
            if (other.ey != null) {
               return false;
            }
         } else if (!this.ey.equals(other.ey)) {
            return false;
         }

         if (this.ez == null) {
            if (other.ez != null) {
               return false;
            }
         } else if (!this.ez.equals(other.ez)) {
            return false;
         }

         return true;
      }
   }
}
