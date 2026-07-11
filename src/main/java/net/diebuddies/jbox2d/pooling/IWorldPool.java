package net.diebuddies.jbox2d.pooling;

import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.Collision;
import net.diebuddies.jbox2d.collision.Distance;
import net.diebuddies.jbox2d.collision.TimeOfImpact;
import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.Mat33;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.common.Vec3;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;

public interface IWorldPool {
   IDynamicStack<Contact> getPolyContactStack();

   IDynamicStack<Contact> getCircleContactStack();

   IDynamicStack<Contact> getPolyCircleContactStack();

   IDynamicStack<Contact> getEdgeCircleContactStack();

   IDynamicStack<Contact> getEdgePolyContactStack();

   IDynamicStack<Contact> getChainCircleContactStack();

   IDynamicStack<Contact> getChainPolyContactStack();

   Vec2 popVec2();

   Vec2[] popVec2(int var1);

   void pushVec2(int var1);

   Vec3 popVec3();

   Vec3[] popVec3(int var1);

   void pushVec3(int var1);

   Mat22 popMat22();

   Mat22[] popMat22(int var1);

   void pushMat22(int var1);

   Mat33 popMat33();

   void pushMat33(int var1);

   AABB popAABB();

   AABB[] popAABB(int var1);

   void pushAABB(int var1);

   Rot popRot();

   void pushRot(int var1);

   Collision getCollision();

   TimeOfImpact getTimeOfImpact();

   Distance getDistance();

   float[] getFloatArray(int var1);

   int[] getIntArray(int var1);

   Vec2[] getVec2Array(int var1);
}
