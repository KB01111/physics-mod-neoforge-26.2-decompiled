package net.diebuddies.jbox2d.common;

class PlatformMathUtils {
   private static final float SHIFT23 = 8388608.0F;
   private static final float INV_SHIFT23 = 1.1920929E-7F;

   public static final float fastPow(float a, float b) {
      float x = (float)Float.floatToRawIntBits(a);
      x *= 1.1920929E-7F;
      x -= 127.0F;
      float y = x - (float)(x >= 0.0F ? (int)x : (int)x - 1);
      b *= x + (y - y * y) * 0.346607F;
      y = b - (float)(b >= 0.0F ? (int)b : (int)b - 1);
      y = (y - y * y) * 0.33971F;
      return Float.intBitsToFloat((int)((b + 127.0F - y) * 8388608.0F));
   }
}
