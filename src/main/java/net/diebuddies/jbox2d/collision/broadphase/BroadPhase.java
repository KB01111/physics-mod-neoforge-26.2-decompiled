package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.PairCallback;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.Vec2;

public interface BroadPhase {
   int NULL_PROXY = -1;

   int createProxy(AABB var1, Object var2);

   void destroyProxy(int var1);

   void moveProxy(int var1, AABB var2, Vec2 var3);

   void touchProxy(int var1);

   Object getUserData(int var1);

   AABB getFatAABB(int var1);

   boolean testOverlap(int var1, int var2);

   int getProxyCount();

   void drawTree(DebugDraw var1);

   void updatePairs(PairCallback var1);

   void query(TreeCallback var1, AABB var2);

   void raycast(TreeRayCastCallback var1, RayCastInput var2);

   int getTreeHeight();

   int getTreeBalance();

   float getTreeQuality();
}
