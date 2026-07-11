package net.diebuddies.jbox2d.collision.shapes;

import net.diebuddies.jbox2d.common.Vec2;

public class MassData {
   public float mass;
   public final Vec2 center;
   public float I;

   public MassData() {
      this.mass = this.I = 0.0F;
      this.center = new Vec2();
   }

   public MassData(MassData md) {
      this.mass = md.mass;
      this.I = md.I;
      this.center = md.center.clone();
   }

   public void set(MassData md) {
      this.mass = md.mass;
      this.I = md.I;
      this.center.set(md.center);
   }

   public MassData clone() {
      return new MassData(this);
   }
}
