package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.callbacks.QueryCallback;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.collision.broadphase.BroadPhase;

class WorldQueryWrapper implements TreeCallback {
   BroadPhase broadPhase;
   QueryCallback callback;

   @Override
   public boolean treeCallback(int nodeId) {
      FixtureProxy proxy = (FixtureProxy)this.broadPhase.getUserData(nodeId);
      return this.callback.reportFixture(proxy.fixture);
   }
}
