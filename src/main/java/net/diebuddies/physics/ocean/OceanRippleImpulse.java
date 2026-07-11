package net.diebuddies.physics.ocean;

public final class OceanRippleImpulse {
   public static final float MODE_RADIAL = 0.0F;
   public static final float MODE_RAIN = 1.0F;
   public static final float MODE_BOAT = 2.0F;
   public final double x;
   public final double y;
   public final double z;
   public final float strength;
   public final float radius;
   public final float mode;
   public final float softness;
   public final float width;

   private OceanRippleImpulse(double x, double y, double z, float strength, float radius, float mode, float softness, float width) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.strength = strength;
      this.radius = radius;
      this.mode = mode;
      this.softness = softness;
      this.width = width;
   }

   public static OceanRippleImpulse radial(double x, double y, double z, float radius, float strength) {
      return new OceanRippleImpulse(x, y, z, strength, Math.max(0.35F, radius), 0.0F, 0.28F, 0.42F);
   }

   public static OceanRippleImpulse rain(double x, double y, double z, float radius, float strength) {
      return new OceanRippleImpulse(x, y, z, strength, Math.max(0.18F, radius), 1.0F, 0.2F, 0.25F);
   }

   public static OceanRippleImpulse boat(double x, double y, double z, float radius, float strength) {
      return new OceanRippleImpulse(x, y, z, strength, Math.max(1.75F, radius), 2.0F, 0.38F, 0.64F);
   }
}
