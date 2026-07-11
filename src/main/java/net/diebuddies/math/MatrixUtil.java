package net.diebuddies.math;

import net.diebuddies.physics.PhysicsRenderable;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
   public static Matrix4d lerp(Matrix4d first, Matrix4d second, double target, Matrix4d dst) {
      return first.lerp(second, target, dst);
   }

   public static Matrix4d slerp(Matrix4d first, Matrix4d second, double target, Matrix4d dst) {
      double translationX = org.joml.Math.lerp(first.m30(), second.m30(), target);
      double translationY = org.joml.Math.lerp(first.m31(), second.m31(), target);
      double translationZ = org.joml.Math.lerp(first.m32(), second.m32(), target);
      double xs = java.lang.Math.signum(first.m00() * first.m01() * first.m02() * first.m03()) < 0.0 ? -1.0 : 1.0;
      double ys = java.lang.Math.signum(first.m10() * first.m11() * first.m12() * first.m13()) < 0.0 ? -1.0 : 1.0;
      double zs = java.lang.Math.signum(first.m20() * first.m21() * first.m22() * first.m23()) < 0.0 ? -1.0 : 1.0;
      double scaleX1 = xs * length(first.m00(), first.m01(), first.m02());
      double scaleY1 = ys * length(first.m10(), first.m11(), first.m12());
      double scaleZ1 = zs * length(first.m20(), first.m21(), first.m22());
      xs = java.lang.Math.signum(second.m00() * second.m01() * second.m02() * second.m03()) < 0.0 ? -1.0 : 1.0;
      ys = java.lang.Math.signum(second.m10() * second.m11() * second.m12() * second.m13()) < 0.0 ? -1.0 : 1.0;
      zs = java.lang.Math.signum(second.m20() * second.m21() * second.m22() * second.m23()) < 0.0 ? -1.0 : 1.0;
      double scaleX2 = xs * length(second.m00(), second.m01(), second.m02());
      double scaleY2 = ys * length(second.m10(), second.m11(), second.m12());
      double scaleZ2 = zs * length(second.m20(), second.m21(), second.m22());
      double scaleX = org.joml.Math.lerp(scaleX1, scaleX2, target);
      double scaleY = org.joml.Math.lerp(scaleY1, scaleY2, target);
      double scaleZ = org.joml.Math.lerp(scaleZ1, scaleZ2, target);
      double m00 = first.m00();
      double m10 = first.m10();
      double m20 = first.m20();
      double m01 = first.m01();
      double m11 = first.m11();
      double m21 = first.m21();
      double m02 = first.m02();
      double m12 = first.m12();
      double m22 = first.m22();
      double invLengthRow1 = 1.0 / scaleX1;
      double invLengthRow2 = 1.0 / scaleY1;
      double invLengthRow3 = 1.0 / scaleZ1;
      m00 *= invLengthRow1;
      m01 *= invLengthRow1;
      m02 *= invLengthRow1;
      m10 *= invLengthRow2;
      m11 *= invLengthRow2;
      m12 *= invLengthRow2;
      m20 *= invLengthRow3;
      m21 *= invLengthRow3;
      m22 *= invLengthRow3;
      double t = m00 + m11 + m22;
      double rotation1X;
      double rotation1Y;
      double rotation1Z;
      double rotation1W;
      if (t >= 0.0) {
         double s = java.lang.Math.sqrt(t + 1.0);
         rotation1W = 0.5 * s;
         s = 0.5 / s;
         rotation1X = (m12 - m21) * s;
         rotation1Y = (m20 - m02) * s;
         rotation1Z = (m01 - m10) * s;
      } else if (m00 > m11 && m00 > m22) {
         double s = java.lang.Math.sqrt(1.0 + m00 - m11 - m22);
         rotation1X = 0.5 * s;
         s = 0.5 / s;
         rotation1Y = (m01 + m10) * s;
         rotation1Z = (m02 + m20) * s;
         rotation1W = (m12 - m21) * s;
      } else if (m11 > m22) {
         double s = java.lang.Math.sqrt(1.0 + m11 - m00 - m22);
         rotation1Y = 0.5 * s;
         s = 0.5 / s;
         rotation1X = (m01 + m10) * s;
         rotation1Z = (m12 + m21) * s;
         rotation1W = (m20 - m02) * s;
      } else {
         double s = java.lang.Math.sqrt(1.0 + m22 - m00 - m11);
         rotation1Z = 0.5 * s;
         s = 0.5 / s;
         rotation1X = (m02 + m20) * s;
         rotation1Y = (m12 + m21) * s;
         rotation1W = (m01 - m10) * s;
      }

      m00 = second.m00();
      m10 = second.m10();
      m20 = second.m20();
      m01 = second.m01();
      m11 = second.m11();
      m21 = second.m21();
      m02 = second.m02();
      m12 = second.m12();
      m22 = second.m22();
      invLengthRow1 = 1.0 / scaleX2;
      invLengthRow2 = 1.0 / scaleY2;
      invLengthRow3 = 1.0 / scaleZ2;
      m00 *= invLengthRow1;
      m01 *= invLengthRow1;
      m02 *= invLengthRow1;
      m10 *= invLengthRow2;
      m11 *= invLengthRow2;
      m12 *= invLengthRow2;
      m20 *= invLengthRow3;
      m21 *= invLengthRow3;
      m22 *= invLengthRow3;
      t = m00 + m11 + m22;
      double var142 = 0.0;
      double rotation2X;
      double rotation2Y;
      double rotation2Z;
      double rotation2W;
      if (t >= 0.0) {
         var142 = java.lang.Math.sqrt(t + 1.0);
         rotation2W = 0.5 * var142;
         var142 = 0.5 / var142;
         rotation2X = (m12 - m21) * var142;
         rotation2Y = (m20 - m02) * var142;
         rotation2Z = (m01 - m10) * var142;
      } else if (m00 > m11 && m00 > m22) {
         var142 = java.lang.Math.sqrt(1.0 + m00 - m11 - m22);
         rotation2X = 0.5 * var142;
         var142 = 0.5 / var142;
         rotation2Y = (m01 + m10) * var142;
         rotation2Z = (m02 + m20) * var142;
         rotation2W = (m12 - m21) * var142;
      } else if (m11 > m22) {
         var142 = java.lang.Math.sqrt(1.0 + m11 - m00 - m22);
         rotation2Y = 0.5 * var142;
         var142 = 0.5 / var142;
         rotation2X = (m01 + m10) * var142;
         rotation2Z = (m12 + m21) * var142;
         rotation2W = (m20 - m02) * var142;
      } else {
         var142 = java.lang.Math.sqrt(1.0 + m22 - m00 - m11);
         rotation2Z = 0.5 * var142;
         var142 = 0.5 / var142;
         rotation2X = (m02 + m20) * var142;
         rotation2Y = (m12 + m21) * var142;
         rotation2W = (m01 - m10) * var142;
      }

      double cosom = rotation1X * rotation2X + rotation1Y * rotation2Y + rotation1Z * rotation2Z + rotation1W * rotation2W;
      double absCosom = org.joml.Math.abs(cosom);
      double scale0;
      double scale1;
      if (1.0 - absCosom > 1.0E-6) {
         double sinSqr = 1.0 - absCosom * absCosom;
         double sinom = 1.0 / org.joml.Math.sqrt(sinSqr);
         double omega = org.joml.Math.atan2(sinSqr * sinom, absCosom);
         scale0 = org.joml.Math.sin((1.0 - target) * omega) * sinom;
         scale1 = org.joml.Math.sin(target * omega) * sinom;
      } else {
         scale0 = 1.0 - target;
         scale1 = target;
      }

      scale1 = cosom >= 0.0 ? scale1 : -scale1;
      double rotation3X = scale0 * rotation1X + scale1 * rotation2X;
      double rotation3Y = scale0 * rotation1Y + scale1 * rotation2Y;
      double rotation3Z = scale0 * rotation1Z + scale1 * rotation2Z;
      double rotation3W = scale0 * rotation1W + scale1 * rotation2W;
      dst.translationRotateScale(translationX, translationY, translationZ, rotation3X, rotation3Y, rotation3Z, rotation3W, scaleX, scaleY, scaleZ);
      return dst;
   }

   public static Matrix4f slerp(PhysicsRenderable entity, float target, Matrix4f dst) {
      Vector3f first = entity.oldPosition;
      Vector3f second = entity.position;
      Vector3f scale = entity.scale;
      Quaternionf rotation1 = entity.oldRotation;
      Quaternionf rotation2 = entity.rotation;
      float translationX = org.joml.Math.lerp(first.x, second.x, target);
      float translationY = org.joml.Math.lerp(first.y, second.y, target);
      float translationZ = org.joml.Math.lerp(first.z, second.z, target);
      float cosom = org.joml.Math.fma(
         rotation1.x, rotation2.x, org.joml.Math.fma(rotation1.y, rotation2.y, org.joml.Math.fma(rotation1.z, rotation2.z, rotation1.w * rotation2.w))
      );
      float scale0 = 1.0F - target;
      float scale1 = (double)cosom >= 0.0 ? target : -target;
      float rotation3X = org.joml.Math.fma(scale0, rotation1.x, scale1 * rotation2.x);
      float rotation3Y = org.joml.Math.fma(scale0, rotation1.y, scale1 * rotation2.y);
      float rotation3Z = org.joml.Math.fma(scale0, rotation1.z, scale1 * rotation2.z);
      float rotation3W = org.joml.Math.fma(scale0, rotation1.w, scale1 * rotation2.w);
      float s = org.joml.Math.invsqrt(
         org.joml.Math.fma(
            rotation3X, rotation3X, org.joml.Math.fma(rotation3Y, rotation3Y, org.joml.Math.fma(rotation3Z, rotation3Z, rotation3W * rotation3W))
         )
      );
      rotation3X *= s;
      rotation3Y *= s;
      rotation3Z *= s;
      rotation3W *= s;
      dst.translationRotateScale(translationX, translationY, translationZ, rotation3X, rotation3Y, rotation3Z, rotation3W, scale.x, scale.y, scale.z);
      return dst;
   }

   private static double length(double x, double y, double z) {
      return java.lang.Math.sqrt(x * x + y * y + z * z);
   }
}
