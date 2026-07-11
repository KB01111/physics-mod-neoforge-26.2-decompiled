package net.diebuddies.opengl;

public class Pack {
   public static int Y_POS_NORMAL = normal(0.0F, 1.0F, 0.0F);
   public static int X_POS_TANGENT = normal(1.0F, 0.0F, 0.0F, 1.0F);

   public static int color(float r, float g, float b) {
      return (int)(r * 255.0F) | (int)(g * 255.0F) << 8 | (int)(b * 255.0F) << 16 | 0xFF000000;
   }

   public static int color(float r, float g, float b, float a) {
      return (int)(r * 255.0F) | (int)(g * 255.0F) << 8 | (int)(b * 255.0F) << 16 | (int)(a * 255.0F) << 24;
   }

   public static int color(int r, int g, int b, int a) {
      return r | g << 8 | b << 16 | a << 24;
   }

   public static int normal(float x, float y, float z, float w) {
      return (int)(x * 127.0F) & 0xFF | ((int)(y * 127.0F) & 0xFF) << 8 | ((int)(z * 127.0F) & 0xFF) << 16 | ((int)(w * 127.0F) & 0xFF) << 24;
   }

   public static int normal(float x, float y, float z) {
      return normal(x, y, z, 0.0F);
   }

   public static float getRed(int color) {
      return (float)(color >> 16 & 0xFF) * 0.003921569F;
   }

   public static float getGreen(int color) {
      return (float)(color >> 8 & 0xFF) * 0.003921569F;
   }

   public static float getBlue(int color) {
      return (float)(color & 0xFF) * 0.003921569F;
   }

   public static float getAlpha(int color) {
      return (float)(color >> 24 & 0xFF) * 0.003921569F;
   }
}
