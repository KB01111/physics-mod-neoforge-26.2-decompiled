package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.collision.AABB;

public class FixtureProxy {
   final AABB aabb = new AABB();
   Fixture fixture;
   int childIndex;
   int proxyId;
}
