package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.Vec2;

public class ParticleGroupDef {
   public int flags;
   public int groupFlags;
   public final Vec2 position = new Vec2();
   public float angle;
   public final Vec2 linearVelocity = new Vec2();
   public float angularVelocity;
   public ParticleColor color;
   public float strength;
   public Shape shape;
   public boolean destroyAutomatically;
   public Object userData;

   public ParticleGroupDef() {
      this.flags = 0;
      this.groupFlags = 0;
      this.angle = 0.0F;
      this.angularVelocity = 0.0F;
      this.strength = 1.0F;
      this.destroyAutomatically = true;
   }
}
