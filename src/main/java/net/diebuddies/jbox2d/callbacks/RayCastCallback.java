package net.diebuddies.jbox2d.callbacks;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Fixture;

public interface RayCastCallback {
   float reportFixture(Fixture var1, Vec2 var2, Vec2 var3, float var4);
}
