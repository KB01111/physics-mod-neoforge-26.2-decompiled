package net.diebuddies.math;

import org.joml.Random;

public class Math {
   public static final double PI = java.lang.Math.PI;
   public static final float INVERT_COLOR = 0.003921569F;
   public static final FastRandomSource fastRandomSource = new FastRandomSource();
   private static final Random random = new Random();

   public static double lerpDegree(double first, double second, double target) {
      if (first == second) {
         return first;
      } else {
         double shortestAngle = ((second - first) % 360.0 + 540.0) % 360.0 - 180.0;
         return first + shortestAngle * target;
      }
   }

   public static double lerpRadians(double first, double second, double target) {
      return first == second ? first : first + target * wrapRadians(second - first);
   }

   public static double lerpRadians(double first, double second, double maxMovement, double target) {
      if (first == second) {
         return first;
      } else {
         double movement = target * wrapRadians(second - first);
         if (movement < -maxMovement) {
            movement = -maxMovement;
         } else if (movement > maxMovement) {
            movement = maxMovement;
         }

         return first + movement;
      }
   }

   private static double wrapRadians(double radians) {
      return wrapMinMax(radians, -java.lang.Math.PI, java.lang.Math.PI);
   }

   private static double wrapMax(double x, double max) {
      return (max + x % max) % max;
   }

   private static double wrapMinMax(double x, double min, double max) {
      return min + wrapMax(x - min, max - min);
   }

   public static double getWeight(double first, double second, double target) {
      return second == first ? 1.0 : (target - first) / (second - first);
   }

   public static float getWeight(float first, float second, float target) {
      return second == first ? 1.0F : (target - first) / (second - first);
   }

   public static double clamp(double val, double min, double max) {
      return java.lang.Math.max(min, java.lang.Math.min(val, max));
   }

   public static byte clamp(int val, byte min, byte max) {
      return (byte)java.lang.Math.max(min, java.lang.Math.min(val, max));
   }

   public static int clamp(int val, int min, int max) {
      return java.lang.Math.max(min, java.lang.Math.min(val, max));
   }

   public static float clamp(float val, float min, float max) {
      return java.lang.Math.max(min, java.lang.Math.min(val, max));
   }

   public static double toRadians(double angdeg) {
      return angdeg / 180.0 * java.lang.Math.PI;
   }

   public static double remap(double value, double oldMin, double oldMax, double newMin, double newMax) {
      return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
   }

   public static double remapClamp(double value, double oldMin, double oldMax, double newMin, double newMax) {
      return newMin < newMax
         ? clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMin, newMax)
         : clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMax, newMin);
   }

   public static float remap(float value, float oldMin, float oldMax, float newMin, float newMax) {
      return newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin);
   }

   public static float remapClamp(float value, float oldMin, float oldMax, float newMin, float newMax) {
      return newMin < newMax
         ? clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMin, newMax)
         : clamp(newMin + (value - oldMin) / (oldMax - oldMin) * (newMax - newMin), newMax, newMin);
   }

   public static int fastFloor(double x) {
      int xi = (int)x;
      return x < (double)xi ? xi - 1 : xi;
   }

   public static int fastFloor(float x) {
      int xi = (int)x;
      return x < (float)xi ? xi - 1 : xi;
   }

   public static final int fastRound(float x) {
      return fastFloor(x + 0.5F);
   }

   public static final int fastRound(double x) {
      return fastFloor(x + 0.5);
   }

   public static float random() {
      return random.nextFloat();
   }

   public static int randomInt(int size) {
      return random.nextInt(size);
   }

   public static float rsqrt(float value) {
      return value == 0.0F ? 1.0F : (float)(1.0 / java.lang.Math.sqrt((double)value));
   }
}
