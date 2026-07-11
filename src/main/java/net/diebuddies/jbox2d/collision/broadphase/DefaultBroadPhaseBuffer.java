package net.diebuddies.jbox2d.collision.broadphase;

import java.util.Arrays;
import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.PairCallback;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.Vec2;

public class DefaultBroadPhaseBuffer implements TreeCallback, BroadPhase {
   private final BroadPhaseStrategy m_tree;
   private int m_proxyCount = 0;
   private int[] m_moveBuffer;
   private int m_moveCapacity;
   private int m_moveCount;
   private long[] m_pairBuffer;
   private int m_pairCapacity = 16;
   private int m_pairCount = 0;
   private int m_queryProxyId;

   public DefaultBroadPhaseBuffer(BroadPhaseStrategy strategy) {
      this.m_pairBuffer = new long[this.m_pairCapacity];

      for (int i = 0; i < this.m_pairCapacity; i++) {
         this.m_pairBuffer[i] = 0L;
      }

      this.m_moveCapacity = 16;
      this.m_moveCount = 0;
      this.m_moveBuffer = new int[this.m_moveCapacity];
      this.m_tree = strategy;
      this.m_queryProxyId = -1;
   }

   @Override
   public final int createProxy(AABB aabb, Object userData) {
      int proxyId = this.m_tree.createProxy(aabb, userData);
      this.m_proxyCount++;
      this.bufferMove(proxyId);
      return proxyId;
   }

   @Override
   public final void destroyProxy(int proxyId) {
      this.unbufferMove(proxyId);
      this.m_proxyCount--;
      this.m_tree.destroyProxy(proxyId);
   }

   @Override
   public final void moveProxy(int proxyId, AABB aabb, Vec2 displacement) {
      boolean buffer = this.m_tree.moveProxy(proxyId, aabb, displacement);
      if (buffer) {
         this.bufferMove(proxyId);
      }
   }

   @Override
   public void touchProxy(int proxyId) {
      this.bufferMove(proxyId);
   }

   @Override
   public Object getUserData(int proxyId) {
      return this.m_tree.getUserData(proxyId);
   }

   @Override
   public AABB getFatAABB(int proxyId) {
      return this.m_tree.getFatAABB(proxyId);
   }

   @Override
   public boolean testOverlap(int proxyIdA, int proxyIdB) {
      AABB a = this.m_tree.getFatAABB(proxyIdA);
      AABB b = this.m_tree.getFatAABB(proxyIdB);
      return b.lowerBound.x - a.upperBound.x > 0.0F || b.lowerBound.y - a.upperBound.y > 0.0F
         ? false
         : !(a.lowerBound.x - b.upperBound.x > 0.0F) && !(a.lowerBound.y - b.upperBound.y > 0.0F);
   }

   @Override
   public final int getProxyCount() {
      return this.m_proxyCount;
   }

   @Override
   public void drawTree(DebugDraw argDraw) {
      this.m_tree.drawTree(argDraw);
   }

   @Override
   public final void updatePairs(PairCallback callback) {
      this.m_pairCount = 0;

      for (int i = 0; i < this.m_moveCount; i++) {
         this.m_queryProxyId = this.m_moveBuffer[i];
         if (this.m_queryProxyId != -1) {
            AABB fatAABB = this.m_tree.getFatAABB(this.m_queryProxyId);
            this.m_tree.query(this, fatAABB);
         }
      }

      this.m_moveCount = 0;
      Arrays.sort(this.m_pairBuffer, 0, this.m_pairCount);
      int ix = 0;

      while (ix < this.m_pairCount) {
         long primaryPair = this.m_pairBuffer[ix];
         Object userDataA = this.m_tree.getUserData((int)(primaryPair >> 32));
         Object userDataB = this.m_tree.getUserData((int)primaryPair);
         callback.addPair(userDataA, userDataB);
         ix++;

         while (ix < this.m_pairCount) {
            long pair = this.m_pairBuffer[ix];
            if (pair != primaryPair) {
               break;
            }

            ix++;
         }
      }
   }

   @Override
   public final void query(TreeCallback callback, AABB aabb) {
      this.m_tree.query(callback, aabb);
   }

   @Override
   public final void raycast(TreeRayCastCallback callback, RayCastInput input) {
      this.m_tree.raycast(callback, input);
   }

   @Override
   public final int getTreeHeight() {
      return this.m_tree.getHeight();
   }

   @Override
   public int getTreeBalance() {
      return this.m_tree.getMaxBalance();
   }

   @Override
   public float getTreeQuality() {
      return this.m_tree.getAreaRatio();
   }

   protected final void bufferMove(int proxyId) {
      if (this.m_moveCount == this.m_moveCapacity) {
         int[] old = this.m_moveBuffer;
         this.m_moveCapacity *= 2;
         this.m_moveBuffer = new int[this.m_moveCapacity];
         System.arraycopy(old, 0, this.m_moveBuffer, 0, old.length);
      }

      this.m_moveBuffer[this.m_moveCount] = proxyId;
      this.m_moveCount++;
   }

   protected final void unbufferMove(int proxyId) {
      for (int i = 0; i < this.m_moveCount; i++) {
         if (this.m_moveBuffer[i] == proxyId) {
            this.m_moveBuffer[i] = -1;
         }
      }
   }

   @Override
   public final boolean treeCallback(int proxyId) {
      if (proxyId == this.m_queryProxyId) {
         return true;
      } else {
         if (this.m_pairCount == this.m_pairCapacity) {
            long[] oldBuffer = this.m_pairBuffer;
            this.m_pairCapacity *= 2;
            this.m_pairBuffer = new long[this.m_pairCapacity];
            System.arraycopy(oldBuffer, 0, this.m_pairBuffer, 0, oldBuffer.length);

            for (int i = oldBuffer.length; i < this.m_pairCapacity; i++) {
               this.m_pairBuffer[i] = 0L;
            }
         }

         if (proxyId < this.m_queryProxyId) {
            this.m_pairBuffer[this.m_pairCount] = (long)proxyId << 32 | (long)this.m_queryProxyId;
         } else {
            this.m_pairBuffer[this.m_pairCount] = (long)this.m_queryProxyId << 32 | (long)proxyId;
         }

         this.m_pairCount++;
         return true;
      }
   }
}
