package net.diebuddies.jbox2d.common;

import java.util.Random;

public class MathUtils extends PlatformMathUtils {
   public static final float PI = (float) Math.PI;
   public static final float TWOPI = (float) (Math.PI * 2);
   public static final float INV_PI = 0.31830987F;
   public static final float HALF_PI = (float) (Math.PI / 2);
   public static final float QUARTER_PI = (float) (Math.PI / 4);
   public static final float THREE_HALVES_PI = (float) (Math.PI * 3.0 / 2.0);
   public static final float DEG2RAD = (float) (Math.PI / 180.0);
   public static final float RAD2DEG = 180.0F / (float)Math.PI;

   public static final float sin(float x) {
      return (float)StrictMath.sin((double)x);
   }

   public static final float cos(float x) {
      return (float)StrictMath.cos((double)x);
   }

   public static final float abs(float x) {
      if (Settings.FAST_ABS) {
         return x > 0.0F ? x : -x;
      } else {
         return StrictMath.abs(x);
      }
   }

   public static final float fastAbs(float x) {
      return x > 0.0F ? x : -x;
   }

   public static final int abs(int x) {
      int y = x >> 31;
      return (x ^ y) - y;
   }

   public static final int floor(float x) {
      return Settings.FAST_FLOOR ? fastFloor(x) : (int)StrictMath.floor((double)x);
   }

   public static final int fastFloor(float x) {
      int y = (int)x;
      return x < (float)y ? y - 1 : y;
   }

   public static final int ceil(float x) {
      return Settings.FAST_CEIL ? fastCeil(x) : (int)StrictMath.ceil((double)x);
   }

   public static final int fastCeil(float x) {
      int y = (int)x;
      return x > (float)y ? y + 1 : y;
   }

   public static final int round(float x) {
      return Settings.FAST_ROUND ? floor(x + 0.5F) : StrictMath.round(x);
   }

   public static final int ceilPowerOf2(int x) {
      int pow2 = 1;

      while (pow2 < x) {
         pow2 <<= 1;
      }

      return pow2;
   }

   public static final float max(float a, float b) {
      return a > b ? a : b;
   }

   public static final int max(int a, int b) {
      return a > b ? a : b;
   }

   public static final float min(float a, float b) {
      return a < b ? a : b;
   }

   public static final int min(int a, int b) {
      return a < b ? a : b;
   }

   public static final float map(float val, float fromMin, float fromMax, float toMin, float toMax) {
      float mult = (val - fromMin) / (fromMax - fromMin);
      return toMin + mult * (toMax - toMin);
   }

   public static final float clamp(float a, float low, float high) {
      return max(low, min(a, high));
   }

   public static final Vec2 clamp(Vec2 a, Vec2 low, Vec2 high) {
      Vec2 min = new Vec2();
      min.x = a.x < high.x ? a.x : high.x;
      min.y = a.y < high.y ? a.y : high.y;
      min.x = low.x > min.x ? low.x : min.x;
      min.y = low.y > min.y ? low.y : min.y;
      return min;
   }

   public static final void clampToOut(Vec2 a, Vec2 low, Vec2 high, Vec2 dest) {
      dest.x = a.x < high.x ? a.x : high.x;
      dest.y = a.y < high.y ? a.y : high.y;
      dest.x = low.x > dest.x ? low.x : dest.x;
      dest.y = low.y > dest.y ? low.y : dest.y;
   }

   public static final int nextPowerOfTwo(int x) {
      x |= x >> 1;
      x |= x >> 2;
      x |= x >> 4;
      x |= x >> 8;
      x |= x >> 16;
      return x + 1;
   }

   public static final boolean isPowerOfTwo(int x) {
      return x > 0 && (x & x - 1) == 0;
   }

   public static final float pow(float a, float b) {
      return Settings.FAST_POW ? fastPow(a, b) : (float)StrictMath.pow((double)a, (double)b);
   }

   public static final float atan2(float y, float x) {
      return Settings.FAST_ATAN2 ? fastAtan2(y, x) : (float)StrictMath.atan2((double)y, (double)x);
   }

   public static final float fastAtan2(float y, float x) {
      if (x == 0.0F) {
         if (y > 0.0F) {
            return (float) (Math.PI / 2);
         } else {
            return y == 0.0F ? 0.0F : (float) (-Math.PI / 2);
         }
      } else {
         float z = y / x;
         float atan;
         if (abs(z) < 1.0F) {
            atan = z / (1.0F + 0.28F * z * z);
            if (x < 0.0F) {
               if (y < 0.0F) {
                  return atan - (float) Math.PI;
               }

               return atan + (float) Math.PI;
            }
         } else {
            atan = (float) (Math.PI / 2) - z / (z * z + 0.28F);
            if (y < 0.0F) {
               return atan - (float) Math.PI;
            }
         }

         return atan;
      }
   }

   public static final float reduceAngle(float theta) {
      theta %= (float) (Math.PI * 2);
      if (abs(theta) > (float) Math.PI) {
         theta -= (float) (Math.PI * 2);
      }

      if (abs(theta) > (float) (Math.PI / 2)) {
         theta = (float) Math.PI - theta;
      }

      return theta;
   }

   public static final float randomFloat(float argLow, float argHigh) {
      return net.diebuddies.math.Math.random() * (argHigh - argLow) + argLow;
   }

   public static final float randomFloat(Random r, float argLow, float argHigh) {
      return r.nextFloat() * (argHigh - argLow) + argLow;
   }

   public static final float sqrt(float x) {
      return (float)StrictMath.sqrt((double)x);
   }

   public static final float distanceSquared(Vec2 v1, Vec2 v2) {
      float dx = v1.x - v2.x;
      float dy = v1.y - v2.y;
      return dx * dx + dy * dy;
   }

   public static final float distance(Vec2 v1, Vec2 v2) {
      return sqrt(distanceSquared(v1, v2));
   }
}
