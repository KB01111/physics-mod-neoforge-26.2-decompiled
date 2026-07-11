package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.Vec2;

public class RayCastInput {
   public final Vec2 p1 = new Vec2();
   public final Vec2 p2 = new Vec2();
   public float maxFraction = 0.0F;

   public void set(RayCastInput rci) {
      this.p1.set(rci.p1);
      this.p2.set(rci.p2);
      this.maxFraction = rci.maxFraction;
   }
}
