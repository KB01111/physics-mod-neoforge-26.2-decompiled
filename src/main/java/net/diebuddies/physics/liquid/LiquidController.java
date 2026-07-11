package net.diebuddies.physics.liquid;

import net.diebuddies.physics.PhysicsWorld;

public interface LiquidController {
   void init(PhysicsWorld var1, Liquid var2);

   void update(Liquid var1, double var2);
}
