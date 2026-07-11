package net.diebuddies.jbox2d.common;

public class Color3f {
   public static final Color3f WHITE = new Color3f(1.0F, 1.0F, 1.0F);
   public static final Color3f BLACK = new Color3f(0.0F, 0.0F, 0.0F);
   public static final Color3f BLUE = new Color3f(0.0F, 0.0F, 1.0F);
   public static final Color3f GREEN = new Color3f(0.0F, 1.0F, 0.0F);
   public static final Color3f RED = new Color3f(1.0F, 0.0F, 0.0F);
   public float x;
   public float y;
   public float z;

   public Color3f() {
      this.x = this.y = this.z = 0.0F;
   }

   public Color3f(float r, float g, float b) {
      this.x = r;
      this.y = g;
      this.z = b;
   }

   public void set(float r, float g, float b) {
      this.x = r;
      this.y = g;
      this.z = b;
   }

   public void set(Color3f argColor) {
      this.x = argColor.x;
      this.y = argColor.y;
      this.z = argColor.z;
   }
}
