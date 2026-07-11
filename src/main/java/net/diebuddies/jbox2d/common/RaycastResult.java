package net.diebuddies.jbox2d.common;

public class RaycastResult {
   public float lambda = 0.0F;
   public final Vec2 normal = new Vec2();

   public RaycastResult set(RaycastResult argOther) {
      this.lambda = argOther.lambda;
      this.normal.set(argOther.normal);
      return this;
   }
}
