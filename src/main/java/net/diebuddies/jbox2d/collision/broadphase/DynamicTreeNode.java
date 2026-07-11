package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.collision.AABB;

public class DynamicTreeNode {
   public final AABB aabb = new AABB();
   public Object userData;
   protected DynamicTreeNode parent;
   protected DynamicTreeNode child1;
   protected DynamicTreeNode child2;
   protected final int id;
   protected int height;

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object argData) {
      this.userData = argData;
   }

   protected DynamicTreeNode(int id) {
      this.id = id;
   }
}
