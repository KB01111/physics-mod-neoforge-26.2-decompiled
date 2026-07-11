package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class ParticleBodyContact {
   public int index;
   public Body body;
   float weight;
   public final Vec2 normal = new Vec2();
   float mass;
}
