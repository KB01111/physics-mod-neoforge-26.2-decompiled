package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.Vec2;

public class ParticleDef {
   int flags;
   public final Vec2 position = new Vec2();
   public final Vec2 velocity = new Vec2();
   public ParticleColor color;
   public Object userData;
}
