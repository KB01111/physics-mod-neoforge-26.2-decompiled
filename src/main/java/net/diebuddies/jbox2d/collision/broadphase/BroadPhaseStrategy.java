package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.Vec2;

public interface BroadPhaseStrategy {
   int createProxy(AABB var1, Object var2);

   void destroyProxy(int var1);

   boolean moveProxy(int var1, AABB var2, Vec2 var3);

   Object getUserData(int var1);

   AABB getFatAABB(int var1);

   void query(TreeCallback var1, AABB var2);

   void raycast(TreeRayCastCallback var1, RayCastInput var2);

   int computeHeight();

   int getHeight();

   int getMaxBalance();

   float getAreaRatio();

   void drawTree(DebugDraw var1);
}
