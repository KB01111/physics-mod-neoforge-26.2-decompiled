package net.diebuddies.jbox2d.collision.broadphase;

import net.diebuddies.jbox2d.callbacks.DebugDraw;
import net.diebuddies.jbox2d.callbacks.TreeCallback;
import net.diebuddies.jbox2d.callbacks.TreeRayCastCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.common.Color3f;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;

public class DynamicTree implements BroadPhaseStrategy {
   public static final int MAX_STACK_SIZE = 64;
   public static final int NULL_NODE = -1;
   private DynamicTreeNode m_root;
   private DynamicTreeNode[] m_nodes;
   private int m_nodeCount;
   private int m_nodeCapacity;
   private int m_freeList;
   private final Vec2[] drawVecs = new Vec2[4];
   private DynamicTreeNode[] nodeStack = new DynamicTreeNode[20];
   private int nodeStackIndex = 0;
   private final Vec2 r = new Vec2();
   private final AABB aabb = new AABB();
   private final RayCastInput subInput = new RayCastInput();
   private final AABB combinedAABB = new AABB();
   private final Color3f color = new Color3f();
   private final Vec2 textVec = new Vec2();

   public DynamicTree() {
      this.m_root = null;
      this.m_nodeCount = 0;
      this.m_nodeCapacity = 16;
      this.m_nodes = new DynamicTreeNode[16];

      for (int i = this.m_nodeCapacity - 1; i >= 0; i--) {
         this.m_nodes[i] = new DynamicTreeNode(i);
         this.m_nodes[i].parent = i == this.m_nodeCapacity - 1 ? null : this.m_nodes[i + 1];
         this.m_nodes[i].height = -1;
      }

      this.m_freeList = 0;

      for (int i = 0; i < this.drawVecs.length; i++) {
         this.drawVecs[i] = new Vec2();
      }
   }

   @Override
   public final int createProxy(AABB aabb, Object userData) {
      assert aabb.isValid();

      DynamicTreeNode node = this.allocateNode();
      int proxyId = node.id;
      AABB nodeAABB = node.aabb;
      nodeAABB.lowerBound.x = aabb.lowerBound.x - Settings.aabbExtension;
      nodeAABB.lowerBound.y = aabb.lowerBound.y - Settings.aabbExtension;
      nodeAABB.upperBound.x = aabb.upperBound.x + Settings.aabbExtension;
      nodeAABB.upperBound.y = aabb.upperBound.y + Settings.aabbExtension;
      node.userData = userData;
      this.insertLeaf(proxyId);
      return proxyId;
   }

   @Override
   public final void destroyProxy(int proxyId) {
      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      DynamicTreeNode node = this.m_nodes[proxyId];

      assert node.child1 == null;

      this.removeLeaf(node);
      this.freeNode(node);
   }

   @Override
   public final boolean moveProxy(int proxyId, AABB aabb, Vec2 displacement) {
      assert aabb.isValid();

      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      DynamicTreeNode node = this.m_nodes[proxyId];

      assert node.child1 == null;

      AABB nodeAABB = node.aabb;
      if (nodeAABB.lowerBound.x <= aabb.lowerBound.x
         && nodeAABB.lowerBound.y <= aabb.lowerBound.y
         && aabb.upperBound.x <= nodeAABB.upperBound.x
         && aabb.upperBound.y <= nodeAABB.upperBound.y) {
         return false;
      } else {
         this.removeLeaf(node);
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
      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      return this.m_nodes[proxyId].userData;
   }

   @Override
   public final AABB getFatAABB(int proxyId) {
      assert 0 <= proxyId && proxyId < this.m_nodeCapacity;

      return this.m_nodes[proxyId].aabb;
   }

   @Override
   public final void query(TreeCallback callback, AABB aabb) {
      assert aabb.isValid();

      this.nodeStackIndex = 0;
      this.nodeStack[this.nodeStackIndex++] = this.m_root;

      while (this.nodeStackIndex > 0) {
         DynamicTreeNode node = this.nodeStack[--this.nodeStackIndex];
         if (node != null && AABB.testOverlap(node.aabb, aabb)) {
            if (node.child1 == null) {
               boolean proceed = callback.treeCallback(node.id);
               if (!proceed) {
                  return;
               }
            } else {
               if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                  DynamicTreeNode[] newBuffer = new DynamicTreeNode[this.nodeStack.length * 2];
                  System.arraycopy(this.nodeStack, 0, newBuffer, 0, this.nodeStack.length);
                  this.nodeStack = newBuffer;
               }

               this.nodeStack[this.nodeStackIndex++] = node.child1;
               this.nodeStack[this.nodeStackIndex++] = node.child2;
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
         DynamicTreeNode node = this.nodeStack[--this.nodeStackIndex];
         if (node != null) {
            AABB nodeAABB = node.aabb;
            if (AABB.testOverlap(nodeAABB, segAABB)) {
               float cx = (nodeAABB.lowerBound.x + nodeAABB.upperBound.x) * 0.5F;
               float cy = (nodeAABB.lowerBound.y + nodeAABB.upperBound.y) * 0.5F;
               float hx = (nodeAABB.upperBound.x - nodeAABB.lowerBound.x) * 0.5F;
               float hy = (nodeAABB.upperBound.y - nodeAABB.lowerBound.y) * 0.5F;
               tempx = p1x - cx;
               tempy = p1y - cy;
               float separation = MathUtils.abs(vx * tempx + vy * tempy) - (absVx * hx + absVy * hy);
               if (!(separation > 0.0F)) {
                  if (node.child1 == null) {
                     this.subInput.p1.x = p1x;
                     this.subInput.p1.y = p1y;
                     this.subInput.p2.x = p2x;
                     this.subInput.p2.y = p2y;
                     this.subInput.maxFraction = maxFraction;
                     float value = callback.raycastCallback(this.subInput, node.id);
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
                     if (this.nodeStack.length - this.nodeStackIndex - 2 <= 0) {
                        DynamicTreeNode[] newBuffer = new DynamicTreeNode[this.nodeStack.length * 2];
                        System.arraycopy(this.nodeStack, 0, newBuffer, 0, this.nodeStack.length);
                        this.nodeStack = newBuffer;
                     }

                     this.nodeStack[this.nodeStackIndex++] = node.child1;
                     this.nodeStack[this.nodeStackIndex++] = node.child2;
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

   private final int computeHeight(DynamicTreeNode node) {
      assert 0 <= node.id && node.id < this.m_nodeCapacity;

      if (node.child1 == null) {
         return 0;
      } else {
         int height1 = this.computeHeight(node.child1);
         int height2 = this.computeHeight(node.child2);
         return 1 + MathUtils.max(height1, height2);
      }
   }

   public void validate() {
      this.validateStructure(this.m_root);
      this.validateMetrics(this.m_root);
      int freeCount = 0;

      for (DynamicTreeNode freeNode = this.m_freeList != -1 ? this.m_nodes[this.m_freeList] : null; freeNode != null; freeCount++) {
         assert 0 <= freeNode.id && freeNode.id < this.m_nodeCapacity;

         assert freeNode == this.m_nodes[freeNode.id];

         freeNode = freeNode.parent;
      }

      assert this.getHeight() == this.computeHeight();

      assert this.m_nodeCount + freeCount == this.m_nodeCapacity;
   }

   @Override
   public int getHeight() {
      return this.m_root == null ? 0 : this.m_root.height;
   }

   @Override
   public int getMaxBalance() {
      int maxBalance = 0;

      for (int i = 0; i < this.m_nodeCapacity; i++) {
         DynamicTreeNode node = this.m_nodes[i];
         if (node.height > 1) {
            assert node.child1 != null;

            DynamicTreeNode child1 = node.child1;
            DynamicTreeNode child2 = node.child2;
            int balance = MathUtils.abs(child2.height - child1.height);
            maxBalance = MathUtils.max(maxBalance, balance);
         }
      }

      return maxBalance;
   }

   @Override
   public float getAreaRatio() {
      if (this.m_root == null) {
         return 0.0F;
      } else {
         DynamicTreeNode root = this.m_root;
         float rootArea = root.aabb.getPerimeter();
         float totalArea = 0.0F;

         for (int i = 0; i < this.m_nodeCapacity; i++) {
            DynamicTreeNode node = this.m_nodes[i];
            if (node.height >= 0) {
               totalArea += node.aabb.getPerimeter();
            }
         }

         return totalArea / rootArea;
      }
   }

   public void rebuildBottomUp() {
      int[] nodes = new int[this.m_nodeCount];
      int count = 0;

      for (int i = 0; i < this.m_nodeCapacity; i++) {
         if (this.m_nodes[i].height >= 0) {
            DynamicTreeNode node = this.m_nodes[i];
            if (node.child1 == null) {
               node.parent = null;
               nodes[count] = i;
               count++;
            } else {
               this.freeNode(node);
            }
         }
      }

      for (AABB b = new AABB(); count > 1; count--) {
         float minCost = Float.MAX_VALUE;
         int iMin = -1;
         int jMin = -1;

         for (int ix = 0; ix < count; ix++) {
            AABB aabbi = this.m_nodes[nodes[ix]].aabb;

            for (int j = ix + 1; j < count; j++) {
               AABB aabbj = this.m_nodes[nodes[j]].aabb;
               b.combine(aabbi, aabbj);
               float cost = b.getPerimeter();
               if (cost < minCost) {
                  iMin = ix;
                  jMin = j;
                  minCost = cost;
               }
            }
         }

         int index1 = nodes[iMin];
         int index2 = nodes[jMin];
         DynamicTreeNode child1 = this.m_nodes[index1];
         DynamicTreeNode child2 = this.m_nodes[index2];
         DynamicTreeNode parent = this.allocateNode();
         parent.child1 = child1;
         parent.child2 = child2;
         parent.height = 1 + MathUtils.max(child1.height, child2.height);
         parent.aabb.combine(child1.aabb, child2.aabb);
         parent.parent = null;
         child1.parent = parent;
         child2.parent = parent;
         nodes[jMin] = nodes[count - 1];
         nodes[iMin] = parent.id;
      }

      this.m_root = this.m_nodes[nodes[0]];
      this.validate();
   }

   private final DynamicTreeNode allocateNode() {
      if (this.m_freeList == -1) {
         assert this.m_nodeCount == this.m_nodeCapacity;

         DynamicTreeNode[] old = this.m_nodes;
         this.m_nodeCapacity *= 2;
         this.m_nodes = new DynamicTreeNode[this.m_nodeCapacity];
         System.arraycopy(old, 0, this.m_nodes, 0, old.length);

         for (int i = this.m_nodeCapacity - 1; i >= this.m_nodeCount; i--) {
            this.m_nodes[i] = new DynamicTreeNode(i);
            this.m_nodes[i].parent = i == this.m_nodeCapacity - 1 ? null : this.m_nodes[i + 1];
            this.m_nodes[i].height = -1;
         }

         this.m_freeList = this.m_nodeCount;
      }

      int nodeId = this.m_freeList;
      DynamicTreeNode treeNode = this.m_nodes[nodeId];
      this.m_freeList = treeNode.parent != null ? treeNode.parent.id : -1;
      treeNode.parent = null;
      treeNode.child1 = null;
      treeNode.child2 = null;
      treeNode.height = 0;
      treeNode.userData = null;
      this.m_nodeCount++;
      return treeNode;
   }

   private final void freeNode(DynamicTreeNode node) {
      assert node != null;

      assert 0 < this.m_nodeCount;

      node.parent = this.m_freeList != -1 ? this.m_nodes[this.m_freeList] : null;
      node.height = -1;
      this.m_freeList = node.id;
      this.m_nodeCount--;
   }

   private final void insertLeaf(int leaf_index) {
      DynamicTreeNode leaf = this.m_nodes[leaf_index];
      if (this.m_root == null) {
         this.m_root = leaf;
         this.m_root.parent = null;
      } else {
         AABB leafAABB = leaf.aabb;
         DynamicTreeNode index = this.m_root;

         while (index.child1 != null) {
            DynamicTreeNode child1 = index.child1;
            DynamicTreeNode child2 = index.child2;
            float area = index.aabb.getPerimeter();
            this.combinedAABB.combine(index.aabb, leafAABB);
            float combinedArea = this.combinedAABB.getPerimeter();
            float cost = 2.0F * combinedArea;
            float inheritanceCost = 2.0F * (combinedArea - area);
            float cost1;
            if (child1.child1 == null) {
               this.combinedAABB.combine(leafAABB, child1.aabb);
               cost1 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
               this.combinedAABB.combine(leafAABB, child1.aabb);
               float oldArea = child1.aabb.getPerimeter();
               float newArea = this.combinedAABB.getPerimeter();
               cost1 = newArea - oldArea + inheritanceCost;
            }

            float cost2;
            if (child2.child1 == null) {
               this.combinedAABB.combine(leafAABB, child2.aabb);
               cost2 = this.combinedAABB.getPerimeter() + inheritanceCost;
            } else {
               this.combinedAABB.combine(leafAABB, child2.aabb);
               float oldArea = child2.aabb.getPerimeter();
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

         DynamicTreeNode oldParent = this.m_nodes[index.id].parent;
         DynamicTreeNode newParent = this.allocateNode();
         newParent.parent = oldParent;
         newParent.userData = null;
         newParent.aabb.combine(leafAABB, index.aabb);
         newParent.height = index.height + 1;
         if (oldParent != null) {
            if (oldParent.child1 == index) {
               oldParent.child1 = newParent;
            } else {
               oldParent.child2 = newParent;
            }

            newParent.child1 = index;
            newParent.child2 = leaf;
            index.parent = newParent;
            leaf.parent = newParent;
         } else {
            newParent.child1 = index;
            newParent.child2 = leaf;
            index.parent = newParent;
            leaf.parent = newParent;
            this.m_root = newParent;
         }

         index = leaf.parent;

         while (index != null) {
            index = this.balance(index);
            DynamicTreeNode child1x = index.child1;
            DynamicTreeNode child2x = index.child2;

            assert child1x != null;

            assert child2x != null;

            index.height = 1 + MathUtils.max(child1x.height, child2x.height);
            index.aabb.combine(child1x.aabb, child2x.aabb);
            index = index.parent;
         }
      }
   }

   private final void removeLeaf(DynamicTreeNode leaf) {
      if (leaf == this.m_root) {
         this.m_root = null;
      } else {
         DynamicTreeNode parent = leaf.parent;
         DynamicTreeNode grandParent = parent.parent;
         DynamicTreeNode sibling;
         if (parent.child1 == leaf) {
            sibling = parent.child2;
         } else {
            sibling = parent.child1;
         }

         if (grandParent != null) {
            if (grandParent.child1 == parent) {
               grandParent.child1 = sibling;
            } else {
               grandParent.child2 = sibling;
            }

            sibling.parent = grandParent;
            this.freeNode(parent);
            DynamicTreeNode index = grandParent;

            while (index != null) {
               index = this.balance(index);
               DynamicTreeNode child1 = index.child1;
               DynamicTreeNode child2 = index.child2;
               index.aabb.combine(child1.aabb, child2.aabb);
               index.height = 1 + MathUtils.max(child1.height, child2.height);
               index = index.parent;
            }
         } else {
            this.m_root = sibling;
            sibling.parent = null;
            this.freeNode(parent);
         }
      }
   }

   private DynamicTreeNode balance(DynamicTreeNode iA) {
      assert iA != null;

      if (iA.child1 != null && iA.height >= 2) {
         DynamicTreeNode iB = iA.child1;
         DynamicTreeNode iC = iA.child2;

         assert 0 <= iB.id && iB.id < this.m_nodeCapacity;

         assert 0 <= iC.id && iC.id < this.m_nodeCapacity;

         int balance = iC.height - iB.height;
         if (balance > 1) {
            DynamicTreeNode iF = iC.child1;
            DynamicTreeNode iG = iC.child2;

            assert iF != null;

            assert iG != null;

            assert 0 <= iF.id && iF.id < this.m_nodeCapacity;

            assert 0 <= iG.id && iG.id < this.m_nodeCapacity;

            iC.child1 = iA;
            iC.parent = iA.parent;
            iA.parent = iC;
            if (iC.parent != null) {
               if (iC.parent.child1 == iA) {
                  iC.parent.child1 = iC;
               } else {
                  assert iC.parent.child2 == iA;

                  iC.parent.child2 = iC;
               }
            } else {
               this.m_root = iC;
            }

            if (iF.height > iG.height) {
               iC.child2 = iF;
               iA.child2 = iG;
               iG.parent = iA;
               iA.aabb.combine(iB.aabb, iG.aabb);
               iC.aabb.combine(iA.aabb, iF.aabb);
               iA.height = 1 + MathUtils.max(iB.height, iG.height);
               iC.height = 1 + MathUtils.max(iA.height, iF.height);
            } else {
               iC.child2 = iG;
               iA.child2 = iF;
               iF.parent = iA;
               iA.aabb.combine(iB.aabb, iF.aabb);
               iC.aabb.combine(iA.aabb, iG.aabb);
               iA.height = 1 + MathUtils.max(iB.height, iF.height);
               iC.height = 1 + MathUtils.max(iA.height, iG.height);
            }

            return iC;
         } else if (balance >= -1) {
            return iA;
         } else {
            DynamicTreeNode iD = iB.child1;
            DynamicTreeNode iE = iB.child2;

            assert 0 <= iD.id && iD.id < this.m_nodeCapacity;

            assert 0 <= iE.id && iE.id < this.m_nodeCapacity;

            iB.child1 = iA;
            iB.parent = iA.parent;
            iA.parent = iB;
            if (iB.parent != null) {
               if (iB.parent.child1 == iA) {
                  iB.parent.child1 = iB;
               } else {
                  assert iB.parent.child2 == iA;

                  iB.parent.child2 = iB;
               }
            } else {
               this.m_root = iB;
            }

            if (iD.height > iE.height) {
               iB.child2 = iD;
               iA.child1 = iE;
               iE.parent = iA;
               iA.aabb.combine(iC.aabb, iE.aabb);
               iB.aabb.combine(iA.aabb, iD.aabb);
               iA.height = 1 + MathUtils.max(iC.height, iE.height);
               iB.height = 1 + MathUtils.max(iA.height, iD.height);
            } else {
               iB.child2 = iE;
               iA.child1 = iD;
               iD.parent = iA;
               iA.aabb.combine(iC.aabb, iD.aabb);
               iB.aabb.combine(iA.aabb, iE.aabb);
               iA.height = 1 + MathUtils.max(iC.height, iD.height);
               iB.height = 1 + MathUtils.max(iA.height, iE.height);
            }

            return iB;
         }
      } else {
         return iA;
      }
   }

   private void validateStructure(DynamicTreeNode node) {
      if (node != null) {
         assert node == this.m_nodes[node.id];

         assert node != this.m_root || node.parent == null;

         DynamicTreeNode child1 = node.child1;
         DynamicTreeNode child2 = node.child2;
         if (node.child1 == null) {
            assert child1 == null;

            assert child2 == null;

            assert node.height == 0;
         } else {
            assert child1 != null && 0 <= child1.id && child1.id < this.m_nodeCapacity;

            assert child2 != null && 0 <= child2.id && child2.id < this.m_nodeCapacity;

            assert child1.parent == node;

            assert child2.parent == node;

            this.validateStructure(child1);
            this.validateStructure(child2);
         }
      }
   }

   private void validateMetrics(DynamicTreeNode node) {
      if (node != null) {
         DynamicTreeNode child1 = node.child1;
         DynamicTreeNode child2 = node.child2;
         if (node.child1 == null) {
            assert child1 == null;

            assert child2 == null;

            assert node.height == 0;
         } else {
            assert child1 != null && 0 <= child1.id && child1.id < this.m_nodeCapacity;

            assert child2 != null && 0 <= child2.id && child2.id < this.m_nodeCapacity;

            int height1 = child1.height;
            int height2 = child2.height;
            int height = 1 + MathUtils.max(height1, height2);

            assert node.height == height;

            AABB aabb = new AABB();
            aabb.combine(child1.aabb, child2.aabb);

            assert aabb.lowerBound.equals(node.aabb.lowerBound);

            assert aabb.upperBound.equals(node.aabb.upperBound);

            this.validateMetrics(child1);
            this.validateMetrics(child2);
         }
      }
   }

   @Override
   public void drawTree(DebugDraw argDraw) {
      if (this.m_root != null) {
         int height = this.computeHeight();
         this.drawTree(argDraw, this.m_root, 0, height);
      }
   }

   public void drawTree(DebugDraw argDraw, DynamicTreeNode node, int spot, int height) {
      node.aabb.getVertices(this.drawVecs);
      this.color.set(1.0F, (float)(height - spot) * 1.0F / (float)height, (float)(height - spot) * 1.0F / (float)height);
      argDraw.drawPolygon(this.drawVecs, 4, this.color);
      argDraw.getViewportTranform().getWorldToScreen(node.aabb.upperBound, this.textVec);
      argDraw.drawString(this.textVec.x, this.textVec.y, node.id + "-" + (spot + 1) + "/" + height, this.color);
      if (node.child1 != null) {
         this.drawTree(argDraw, node.child1, spot + 1, height);
      }

      if (node.child2 != null) {
         this.drawTree(argDraw, node.child2, spot + 1, height);
      }
   }
}
