package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.callbacks.RayCastCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhase;
import net.diebuddies.jbox2d.common.Vec2;

class WorldRayCastWrapper implements TreeRayCastCallback {
   private final RayCastOutput output = new RayCastOutput();
   private final Vec2 temp = new Vec2();
   private final Vec2 point = new Vec2();
   BroadPhase broadPhase;
   RayCastCallback callback;

   @Override
   public float raycastCallback(RayCastInput input, int nodeId) {
      Object userData = this.broadPhase.getUserData(nodeId);
      FixtureProxy proxy = (FixtureProxy)userData;
      Fixture fixture = proxy.fixture;
      int index = proxy.childIndex;
      boolean hit = fixture.raycast(this.output, input, index);
      if (hit) {
         float fraction = this.output.fraction;
         this.temp.set(input.p2).mulLocal(fraction);
         this.point.set(input.p1).mulLocal(1.0F - fraction).addLocal(this.temp);
         return this.callback.reportFixture(fixture, this.point, this.output.normal, fraction);
      } else {
         return input.maxFraction;
      }
   }
}
