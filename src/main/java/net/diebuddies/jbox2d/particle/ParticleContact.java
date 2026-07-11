package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.Vec2;

public class ParticleContact {
   public int indexA;
   public int indexB;
   public int flags;
   public float weight;
   public final Vec2 normal = new Vec2();
}
