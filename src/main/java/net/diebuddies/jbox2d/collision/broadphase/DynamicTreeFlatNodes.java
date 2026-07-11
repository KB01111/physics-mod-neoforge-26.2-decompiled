package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.BufferUtils;
import net.diebuddies.jbox2d.common.Color3f;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class DynamicTreeFlatNodes implements BroadPhaseStrategy {
   public static final int MAX_STACK_SIZE = 64;
   public static final int NULL_NODE = -1;
   public static final int INITIAL_BUFFER_LENGTH = 16;
   public int m_root;
   public AABB[] m_aabb;
   public Object[] m_userData;
   protected int[] m_parent;
   protected int[] m_child1;
   protected int[] m_child2;
   protected int[] m_height;
   private int m_nodeCount;
   private int m_nodeCapacity;
   private int m_freeList;
   private final Vec2[] drawVecs = new Vec2[4];
   private int[] nodeStack = new int[20];
   private int nodeStackIndex;
   private final Vec2 r = new Vec2();
   private final AABB aabb = new AABB();
   private final RayCastInput subInput = new RayCastInput();
   private final AABB combinedAABB = new AABB();
   private final Color3f color = new Color3f();
   private final Vec2 textVec = new Vec2();

   public DynamicTreeFlatNodes() {
      this.m_root = -1;
      this.m_nodeCount = 0;
      this.m_nodeCapacity = 16;
      this.expandBuffers(0, this.m_nodeCapacity);

      for (int i = 0; i < this.drawVecs.length; i++) {
         this.drawVecs[i] = new Vec2();
      }
   }

   private void expandBuffers(int oldSize, int newSize) {
      this.m_aabb = BufferUtils.reallocateBuffer(AABB.class, this.m_aabb, oldSize, newSize);
      this.m_userData = BufferUtils.reallocateBuffer(Object.class, this.m_userData, oldSize, newSize);
      this.m_parent = BufferUtils.reallocateBuffer(this.m_parent, oldSize, newSize);
      this.m_child1 = BufferUtils.reallocateBuffer(this.m_child1, oldSize, newSize);
      this.m_child2 = BufferUtils.reallocateBuffer(this.m_child2, oldSize, newSize);
      this.m_height = BufferUtils.reallocateBuffer(this.m_height, oldSize, newSize);

      for (int i = oldSize; i < newSize; i++) {
         this.m_aabb[i] = new AABB();
         this.m_parent[i] = i == newSize - 1 ? -1 : i + 1;
         this.m_height[i] = -1;
         this.m_child1[i] = -1;
         this.m_child2[i] = -1;
      }

      this.m_freeList = oldSize;
   }

   @Override
   public final int createProxy(AABB aabb, Object userData) {
      int node = this.allocateNode();
      AABB nodeAABB = this.m_aabb[node];
      nodeAABB.lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
      nodeAABB.lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
      nodeAABB.upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
      nodeAABB.upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
      this.m_userData[node] = userData;
      this.insertLeaf(node);
      return node;
   }

   @Override
   public final void destroyProxy(int proxyId) {
      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      assert this.m_child1[proxyId] == -1;

      this.removeLeaf(proxyId);
      this.freeNode(proxyId);
   }

   @Override
   public final boolean moveProxy(int proxyId, AABB aabb, Vec2 displacement) {
      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      assert this.m_child1[proxyId] == -1;

      AABB nodeAABB = this.m_aabb[proxyId];
      if (nodeAABB.lowerBound.x <= aabb.lowerBound.x
         && nodeAABB.lowerBound.y <= aabb.lowerBound.y
         && aabb.upperBound.x <= nodeAABB.upperBound.x
         && aabb.upperBound.y <= nodeAABB.upperBound.y) {
         return false;
      } else {
         this.removeLeaf(proxyId);
         Vec2 lowerBound = nodeAABB.lowerBound;
         Vec2 upperBound = nodeAABB.upperBound;
         lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
         lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
         upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
         upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
         float dx = displacement.x * Settings.aabbMultiplier;
         float dy = displacement.y * Settings.aabbMultiplier;
         if (dx < 0.0F) {
            lowerBound.x += dx;
         } else {
            upperBound.x += dx;
         }

         if (dy < 0.0F) {
            lowerBound.y += dy;
         } else {
            upperBound.y += dy;
         }

         this.insertLeaf(proxyId);
         return true;
      }
   }

   @Override
   public final Object getUserData(int proxyId) {
      assert 0 <= proxyId && proxyId < this.m_nodeCount;

      return this.m_userData[proxyId];
   }

   @Override
   public final AABB getFatAABB(int proxyId) {
      assert 0 <= proxyId && proxyId < this.m_nodeCount;

      return this.m_aabb[proxyId];
   }

   @Override
   public final void query(TreeCallback callback, AABB aabb) {
      this.nodeStackIndex = 0;
      this.nodeStack[this.nodeStackIndex++] = this.m_root;

      while (this.nodeStackIndex > 0) {
         int node = this.nodeStack[--this.nodeStackIndex];
         if (node != -1 && AABB.testOverlap(this.m_aabb[node], aabb)) {
            int child1 = this.m_child1[node];
            if (child1 == -1) {
               boolean proceed = callback.treeCallback(node);
               if (!proceed) {
                  return;
               }
            } else {
               if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                  this.nodeStack = BufferUtils.reallocateBuffer(this.nodeStack, this.nodeStack.length, this.nodeStack.length * 2);
               }

               this.nodeStack[this.nodeStackIndex++] = child1;
               this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
            }
         }
      }
   }

   @Override
   public void raycast(TreeRayCastCallback callback, RayCastInput input) {
      Vec2 p1 = input.p1;
      Vec2 p2 = input.p2;
      float p1x = p1.x;
      float p2x = p2.x;
      float p1y = p1.y;
      float p2y = p2.y;
      this.r.x = p2x - p1x;
      this.r.y = p2y - p1y;

      assert this.r.x * this.r.x + this.r.y * this.r.y > 0.0F;

      this.r.normalize();
      float rx = this.r.x;
      float ry = this.r.y;
      float vx = -1.0F * ry;
      float vy = 1.0F * rx;
      float absVx = MathUtils.abs(vx);
      float absVy = MathUtils.abs(vy);
      float maxFraction = input.maxFraction;
      AABB segAABB = this.aabb;
      float tempx = (p2x - p1x) * maxFraction + p1x;
      float tempy = (p2y - p1y) * maxFraction + p1y;
      segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
      segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
      segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
      segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
      this.nodeStackIndex = 0;
      this.nodeStack[this.nodeStackIndex++] = this.m_root;

      while (this.nodeStackIndex > 0) {
         int node = this.nodeStack[--this.nodeStackIndex] = this.m_root;
         if (node != -1) {
            AABB nodeAABB = this.m_aabb[node];
            if (AABB.testOverlap(nodeAABB, segAABB)) {
               float cx = (nodeAABB.lowerBound.x + nodeAABB.upperBound.x) * 0.5F;
               float cy = (nodeAABB.lowerBound.y + nodeAABB.upperBound.y) * 0.5F;
               float hx = (nodeAABB.upperBound.x - nodeAABB.lowerBound.x) * 0.5F;
               float hy = (nodeAABB.upperBound.y - nodeAABB.lowerBound.y) * 0.5F;
               tempx = p1x - cx;
               tempy = p1y - cy;
               float separation = MathUtils.abs(vx * tempx + vy * tempy) - (absVx * hx + absVy * hy);
               if (!(separation > 0.0F)) {
                  int child1 = this.m_child1[node];
                  if (child1 == -1) {
                     this.subInput.p1.x = p1x;
                     this.subInput.p1.y = p1y;
                     this.subInput.p2.x = p2x;
                     this.subInput.p2.y = p2y;
                     this.subInput.maxFraction = maxFraction;
                     float value = callback.raycastCallback(this.subInput, node);
                     if (value == 0.0F) {
                        return;
                     }

                     if (value > 0.0F) {
                        maxFraction = value;
                        tempx = (p2x - p1x) * value + p1x;
                        tempy = (p2y - p1y) * value + p1y;
                        segAABB.lowerBound.x = p1x < tempx ? p1x : tempx;
                        segAABB.lowerBound.y = p1y < tempy ? p1y : tempy;
                        segAABB.upperBound.x = p1x > tempx ? p1x : tempx;
                        segAABB.upperBound.y = p1y > tempy ? p1y : tempy;
                     }
                  } else {
                     this.nodeStack[this.nodeStackIndex++] = child1;
                     this.nodeStack[this.nodeStackIndex++] = this.m_child2[node];
                  }
               }
            }
         }
      }
   }

   @Override
   public final int computeHeight() {
      return this.computeHeight(this.m_root);
   }

   private final int computeHeight(int node) {
      assert 0 <= node && node < this.m_nodeCapacity;

      if (this.m_child1[node] == -1) {
         return 0;
      } else {
         int height1 = this.computeHeight(this.m_child1[node]);
         int height2 = this.computeHeight(this.m_child2[node]);
         return 1 + MathUtils.max(height1, height2);
      }
   }

   public void validate() {
      this.validateStructure(this.m_root);
      this.validateMetrics(this.m_root);
      int freeCount = 0;

      for (int freeNode = this.m_freeList; freeNode != -1; freeCount++) {
         assert 0 <= freeNode && freeNode < this.m_nodeCapacity;

         freeNode = this.m_parent[freeNode];
      }

      assert this.getHeight() == this.computeHeight();

      assert this.m_nodeCount + freeCount == this.m_nodeCapacity;
   }

   @Override
   public int getHeight() {
      return this.m_root == -1 ? 0 : this.m_height[this.m_root];
   }

   @Override
   public int getMaxBalance() {
      int maxBalance = 0;

      for (int i = 0; i < this.m_nodeCapacity; i++) {
         if (this.m_height[i] > 1) {
            assert this.m_child1[i] != -1;

            int child1 = this.m_child1[i];
            int child2 = this.m_child2[i];
            int balance = MathUtils.abs(this.m_height[child2] - this.m_height[child1]);
            maxBalance = MathUtils.max(maxBalance, balance);
         }
      }

      return maxBalance;
   }

   @Override
   public float getAreaRatio() {
      if (this.m_root == -1) {
         return 0.0F;
      } else {
         int root = this.m_root;
         float rootArea = this.m_aabb[root].getPerimeter();
         float totalArea = 0.0F;

         for (int i = 0; i < this.m_nodeCapacity; i++) {
            if (this.m_height[i] >= 0) {
               totalArea += this.m_aabb[i].getPerimeter();
            }
         }

         return totalArea / rootArea;
      }
   }

   private final int allocateNode() {
      if (this.m_freeList == -1) {
         assert this.m_nodeCount == this.m_nodeCapacity;

         this.m_nodeCapacity *= 2;
         this.expandBuffers(this.m_nodeCount, this.m_nodeCapacity);
      }

      assert this.m_freeList != -1;

      int node = this.m_freeList;
      this.m_freeList = this.m_parent[node];
      this.m_parent[node] = -1;
      this.m_child1[node] = -1;
      this.m_height[node] = 0;
      this.m_nodeCount++;
      return node;
   }

   private final void freeNode(int node) {
      assert node != -1;

      assert 0 < this.m_nodeCount;

      this.m_parent[node] = this.m_freeList != -1 ? this.m_freeList : -1;
      this.m_height[node] = -1;
      this.m_freeList = node;
      this.m_nodeCount--;
   }

   private final void insertLeaf(int leaf) {
      if (this.m_root == -1) {
         this.m_root = leaf;
         this.m_parent[this.m_root] = -1;
      } else {
         AABB leafAABB = this.m_aabb[leaf];
         int index = this.m_root;

         while (this.m_child1[index] != -1) {
            int child1 = this.m_child1[index];
            int child2 = this.m_child2[index];
            AABB nodeAABB = this.m_aabb[index];
            float area = nodeAABB.getPerimeter();
            this.combinedAABB.combine(nodeAABB, leafAABB);
            float combinedArea = this.combinedAABB.getPerimeter();
            float cost = 2.0F * combinedArea;
            float inheritanceCost = 2.0F * (combinedArea - area);
            AABB child1AABB = this.m_aabb[child1];
            float cost1;
            if (this.m_child1[child1] == -1) {
               this.combinedAABB.combine(leafAABB, child1AABB);
               cost1 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
               this.combinedAABB.combine(leafAABB, child1AABB);
               float oldArea = child1AABB.getPerimeter();
               float newArea = this.combinedAABB.getPerimeter();
               cost1 = newArea - oldArea + inheritanceCost;
            }

            AABB child2AABB = this.m_aabb[child2];
            float cost2;
            if (this.m_child1[child2] == -1) {
               this.combinedAABB.combine(leafAABB, child2AABB);
               cost2 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
               this.combinedAABB.combine(leafAABB, child2AABB);
               float oldArea = child2AABB.getPerimeter();
               float newArea = this.combinedAABB.getPerimeter();
               cost2 = newArea - oldArea + inheritanceCost;
            }

            if (cost < cost1 && cost < cost2) {
               break;
            }

            if (cost1 < cost2) {
               index = child1;
            } else {
               index = child2;
            }
         }

         int oldParent = this.m_parent[index];
         int newParent = this.allocateNode();
         this.m_parent[newParent] = oldParent;
         this.m_userData[newParent] = null;
         this.m_aabb[newParent].combine(leafAABB, this.m_aabb[index]);
         this.m_height[newParent] = this.m_height[index] + 1;
         if (oldParent != -1) {
            if (this.m_child1[oldParent] == index) {
               this.m_child1[oldParent] = newParent;
            } else {
               this.m_child2[oldParent] = newParent;
            }

            this.m_child1[newParent] = index;
            this.m_child2[newParent] = leaf;
            this.m_parent[index] = newParent;
            this.m_parent[leaf] = newParent;
         } else {
            this.m_child1[newParent] = index;
            this.m_child2[newParent] = leaf;
            this.m_parent[index] = newParent;
            this.m_parent[leaf] = newParent;
            this.m_root = newParent;
         }

         index = this.m_parent[leaf];

         while (index != -1) {
            int child1 = this.m_child1[index];
            int child2 = this.m_child2[index];

            this.m_height[index] = 1 + MathUtils.max(this.m_height[child1], this.m_height[child2]);
            this.m_aabb[index].combine(this.m_aabb[child1], this.m_aabb[child2]);
            index = this.m_parent[index];
         }
      }
   }

   private final void removeLeaf(int leaf) {
      if (leaf == this.m_root) {
         this.m_root = -1;
      } else {
         int parent = this.m_parent[leaf];
         int grandParent = this.m_parent[parent];
         int parentChild1 = this.m_child1[parent];
         int parentChild2 = this.m_child2[parent];
         int sibling;
         if (parentChild1 == leaf) {
            sibling = parentChild2;
         } else {
            sibling = parentChild1;
         }

         if (grandParent != -1) {
            if (this.m_child1[grandParent] == parent) {
               this.m_child1[grandParent] = sibling;
            } else {
               this.m_child2[grandParent] = sibling;
            }

            this.m_parent[sibling] = grandParent;
            this.freeNode(parent);
            int index = grandParent;

            while (index != -1) {
               index = this.balance(index);
               int child1 = this.m_child1[index];
               int child2 = this.m_child2[index];
               this.m_aabb[index].combine(this.m_aabb[child1], this.m_aabb[child2]);
               this.m_height[index] = 1 + MathUtils.max(this.m_height[child1], this.m_height[child2]);
               index = this.m_parent[index];
            }
         } else {
            this.m_root = sibling;
            this.m_parent[sibling] = -1;
            this.freeNode(parent);
         }
      }
   }

   private int balance(int iA) {
      assert iA != -1;

      if (this.m_child1[iA] != -1 && this.m_height[iA] >= 2) {
         int iB = this.m_child1[iA];
         int iC = this.m_child2[iA];

         assert 0 <= iB && iB < this.m_nodeCapacity;

         assert 0 <= iC && iC < this.m_nodeCapacity;

         int balance = this.m_height[iC] - this.m_height[iB];
         if (balance > 1) {
            int iF = this.m_child1[iC];
            int iG = this.m_child2[iC];

            assert 0 <= iF && iF < this.m_nodeCapacity;

            assert 0 <= iG && iG < this.m_nodeCapacity;

            this.m_child1[iC] = iA;
            int cParent = this.m_parent[iC] = this.m_parent[iA];
            this.m_parent[iA] = iC;
            if (cParent != -1) {
               if (this.m_child1[cParent] == iA) {
                  this.m_child1[cParent] = iC;
               } else {
                  assert this.m_child2[cParent] == iA;

                  this.m_child2[cParent] = iC;
               }
            } else {
               this.m_root = iC;
            }

            if (this.m_height[iF] > this.m_height[iG]) {
               this.m_child2[iC] = iF;
               this.m_child2[iA] = iG;
               this.m_parent[iG] = iA;
               this.m_aabb[iA].combine(this.m_aabb[iB], this.m_aabb[iG]);
               this.m_aabb[iC].combine(this.m_aabb[iA], this.m_aabb[iF]);
               this.m_height[iA] = 1 + MathUtils.max(this.m_height[iB], this.m_height[iG]);
               this.m_height[iC] = 1 + MathUtils.max(this.m_height[iA], this.m_height[iF]);
            } else {
               this.m_child2[iC] = iG;
               this.m_child2[iA] = iF;
               this.m_parent[iF] = iA;
               this.m_aabb[iA].combine(this.m_aabb[iB], this.m_aabb[iF]);
               this.m_aabb[iC].combine(this.m_aabb[iA], this.m_aabb[iG]);
               this.m_height[iA] = 1 + MathUtils.max(this.m_height[iB], this.m_height[iF]);
               this.m_height[iC] = 1 + MathUtils.max(this.m_height[iA], this.m_height[iG]);
            }

            return iC;
         } else if (balance >= -1) {
            return iA;
         } else {
            int iD = this.m_child1[iB];
            int iE = this.m_child2[iB];

            assert 0 <= iD && iD < this.m_nodeCapacity;

            assert 0 <= iE && iE < this.m_nodeCapacity;

            this.m_child1[iB] = iA;
            int Bparent = this.m_parent[iB] = this.m_parent[iA];
            this.m_parent[iA] = iB;
            if (Bparent != -1) {
               if (this.m_child1[Bparent] == iA) {
                  this.m_child1[Bparent] = iB;
               } else {
                  assert this.m_child2[Bparent] == iA;

                  this.m_child2[Bparent] = iB;
               }
            } else {
               this.m_root = iB;
            }

            if (this.m_height[iD] > this.m_height[iE]) {
               this.m_child2[iB] = iD;
               this.m_child1[iA] = iE;
               this.m_parent[iE] = iA;
               this.m_aabb[iA].combine(this.m_aabb[iC], this.m_aabb[iE]);
               this.m_aabb[iB].combine(this.m_aabb[iA], this.m_aabb[iD]);
               this.m_height[iA] = 1 + MathUtils.max(this.m_height[iC], this.m_height[iE]);
               this.m_height[iB] = 1 + MathUtils.max(this.m_height[iA], this.m_height[iD]);
            } else {
               this.m_child2[iB] = iE;
               this.m_child1[iA] = iD;
               this.m_parent[iD] = iA;
               this.m_aabb[iA].combine(this.m_aabb[iC], this.m_aabb[iD]);
               this.m_aabb[iB].combine(this.m_aabb[iA], this.m_aabb[iE]);
               this.m_height[iA] = 1 + MathUtils.max(this.m_height[iC], this.m_height[iD]);
               this.m_height[iB] = 1 + MathUtils.max(this.m_height[iA], this.m_height[iE]);
            }

            return iB;
         }
      } else {
         return iA;
      }
   }

   private void validateStructure(int node) {
      if (node != -1) {
         assert node != this.m_root || this.m_parent[node] == -1;

         int child1 = this.m_child1[node];
         int child2 = this.m_child2[node];
         if (child1 == -1) {
            assert child1 == -1;

            assert child2 == -1;

            assert this.m_height[node] == 0;
         } else {
            assert child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity;

            assert child2 != -1 && 0 <= child2 && child2 < this.m_nodeCapacity;

            assert this.m_parent[child1] == node;

            assert this.m_parent[child2] == node;

            this.validateStructure(child1);
            this.validateStructure(child2);
         }
      }
   }

   private void validateMetrics(int node) {
      if (node != -1) {
         int child1 = this.m_child1[node];
         int child2 = this.m_child2[node];
         if (child1 == -1) {
            assert child1 == -1;

            assert child2 == -1;

            assert this.m_height[node] == 0;
         } else {
            assert child1 != -1 && 0 <= child1 && child1 < this.m_nodeCapacity;

            assert child2 != child1 && 0 <= child2 && child2 < this.m_nodeCapacity;

            int height1 = this.m_height[child1];
            int height2 = this.m_height[child2];
            int height = 1 + MathUtils.max(height1, height2);

            assert this.m_height[node] == height;

            AABB aabb = new AABB();
            aabb.combine(this.m_aabb[child1], this.m_aabb[child2]);

            assert aabb.lowerBound.equals(this.m_aabb[node].lowerBound);

            assert aabb.upperBound.equals(this.m_aabb[node].upperBound);

            this.validateMetrics(child1);
            this.validateMetrics(child2);
         }
      }
   }

   @Override
   public void drawTree(DebugDraw argDraw) {
      if (this.m_root != -1) {
         int height = this.computeHeight();
         this.drawTree(argDraw, this.m_root, 0, height);
      }
   }

   public void drawTree(DebugDraw argDraw, int node, int spot, int height) {
      AABB a = this.m_aabb[node];
      a.getVertices(this.drawVecs);
      this.color.set(1.0F, (float)(height - spot) * 1.0F / (float)height, (float)(height - spot) * 1.0F / (float)height);
      argDraw.drawPolygon(this.drawVecs, 4, this.color);
      argDraw.getViewportTranform().getWorldToScreen(a.upperBound, this.textVec);
      argDraw.drawString(this.textVec.x, this.textVec.y, node + "-" + (spot + 1) + "/" + height, this.color);
      int c1 = this.m_child1[node];
      int c2 = this.m_child2[node];
      if (c1 != -1) {
         this.drawTree(argDraw, c1, spot + 1, height);
      }

      if (c2 != -1) {
         this.drawTree(argDraw, c2, spot + 1, height);
      }
   }
}
