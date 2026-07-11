package net.diebuddies.physics.ocean;

import java.util.Arrays;
import net.diebuddies.physics.snow.math.AABB3D;
import org.joml.FrustumIntersection;

public final class DynamicFrustumBVH<T extends DynamicFrustumBVH.BVHNode> {
   private static final int NULL = -1;
   private int root = -1;
   private int nodeCapacity;
   private int nodeCount;
   private int freeList;
   private int[] parent;
   private int[] left;
   private int[] right;
   private int[] height;
   private double[] minX;
   private double[] minY;
   private double[] minZ;
   private double[] maxX;
   private double[] maxY;
   private double[] maxZ;
   private Object[] userData;
   private int[] stackNodes = new int[64];
   private byte[] stackInside = new byte[64];

   public DynamicFrustumBVH(int initialLeafCapacity) {
      int cap = Math.max(16, initialLeafCapacity * 2);
      this.initStorage(cap);
   }

   public DynamicFrustumBVH() {
      this(16);
   }

   public void add(AABB3D aabb, T payload) {
      int id = this.allocateNode();
      this.setFromAabb3D(id, aabb);
      this.userData[id] = payload;
      this.height[id] = 0;
      this.left[id] = this.right[id] = -1;
      this.insertLeaf(id);
      payload.setId(id);
   }

   public void remove(T payload) {
      int proxyId = payload.getId();
      if (proxyId != -1) {
         payload.setId(-1);
         this.removeLeaf(proxyId);
         this.userData[proxyId] = null;
         this.freeNode(proxyId);
      }
   }

   public void query(FrustumIntersection fi, double camX, double camY, double camZ, DynamicFrustumBVH.HitCallback<T> cb) {
      if (this.root != -1) {
         int sp = 0;
         this.stackNodes[sp] = this.root;
         this.stackInside[sp] = 0;
         sp++;

         while (sp != 0) {
            int node = this.stackNodes[--sp];
            boolean inside = this.stackInside[sp] != 0;
            if (!inside) {
               float mnX = (float)(this.minX[node] - camX);
               float mnY = (float)(this.minY[node] - camY);
               float mnZ = (float)(this.minZ[node] - camZ);
               float mxX = (float)(this.maxX[node] - camX);
               float mxY = (float)(this.maxY[node] - camY);
               float mxZ = (float)(this.maxZ[node] - camZ);
               int res = fi.intersectAab(mnX, mnY, mnZ, mxX, mxY, mxZ);
               if (res == -2) {
                  inside = true;
               } else if (res != -1) {
                  continue;
               }
            }

            if (this.isLeaf(node)) {
               T payload = (T)this.userData[node];
               if (!cb.report(node, payload)) {
                  return;
               }
            } else {
               int l = this.left[node];
               int r = this.right[node];
               if (sp + 2 > this.stackNodes.length) {
                  this.growStack();
               }

               this.stackNodes[sp] = l;
               this.stackInside[sp] = (byte)(inside ? 1 : 0);
               sp++;
               this.stackNodes[sp] = r;
               this.stackInside[sp] = (byte)(inside ? 1 : 0);
               sp++;
            }
         }
      }
   }

   public void query(FrustumIntersection fi, DynamicFrustumBVH.HitCallback<T> cb) {
      this.query(fi, 0.0, 0.0, 0.0, cb);
   }

   private void initStorage(int cap) {
      this.nodeCapacity = cap;
      this.parent = new int[cap];
      this.left = new int[cap];
      this.right = new int[cap];
      this.height = new int[cap];
      this.minX = new double[cap];
      this.minY = new double[cap];
      this.minZ = new double[cap];
      this.maxX = new double[cap];
      this.maxY = new double[cap];
      this.maxZ = new double[cap];
      this.userData = new Object[cap];

      for (int i = 0; i < cap - 1; i++) {
         this.parent[i] = i + 1;
      }

      this.parent[cap - 1] = -1;
      this.freeList = 0;
      this.nodeCount = 0;
      this.root = -1;
   }

   private void growStack() {
      this.stackNodes = Arrays.copyOf(this.stackNodes, this.stackNodes.length * 2);
      this.stackInside = Arrays.copyOf(this.stackInside, this.stackInside.length * 2);
   }

   private int allocateNode() {
      if (this.freeList == -1) {
         this.growNodes();
      }

      int id = this.freeList;
      this.freeList = this.parent[id];
      this.parent[id] = -1;
      this.left[id] = this.right[id] = -1;
      this.height[id] = -1;
      this.userData[id] = null;
      this.nodeCount++;
      return id;
   }

   private void freeNode(int id) {
      this.parent[id] = this.freeList;
      this.freeList = id;
      this.height[id] = -1;
      this.nodeCount--;
   }

   private void growNodes() {
      int old = this.nodeCapacity;
      int neu = old * 2;
      this.parent = Arrays.copyOf(this.parent, neu);
      this.left = Arrays.copyOf(this.left, neu);
      this.right = Arrays.copyOf(this.right, neu);
      this.height = Arrays.copyOf(this.height, neu);
      this.minX = Arrays.copyOf(this.minX, neu);
      this.minY = Arrays.copyOf(this.minY, neu);
      this.minZ = Arrays.copyOf(this.minZ, neu);
      this.maxX = Arrays.copyOf(this.maxX, neu);
      this.maxY = Arrays.copyOf(this.maxY, neu);
      this.maxZ = Arrays.copyOf(this.maxZ, neu);
      this.userData = Arrays.copyOf(this.userData, neu);

      for (int i = old; i < neu - 1; i++) {
         this.parent[i] = i + 1;
      }

      this.parent[neu - 1] = -1;
      this.freeList = old;
      this.nodeCapacity = neu;
   }

   private boolean isLeaf(int node) {
      return this.left[node] == -1;
   }

   private void setFromAabb3D(int id, AABB3D aabb) {
      double ax0 = aabb.start.x;
      double ay0 = aabb.start.y;
      double az0 = aabb.start.z;
      double ax1 = aabb.end.x;
      double ay1 = aabb.end.y;
      double az1 = aabb.end.z;
      this.minX[id] = Math.min(ax0, ax1);
      this.minY[id] = Math.min(ay0, ay1);
      this.minZ[id] = Math.min(az0, az1);
      this.maxX[id] = Math.max(ax0, ax1);
      this.maxY[id] = Math.max(ay0, ay1);
      this.maxZ[id] = Math.max(az0, az1);
   }

   private static double surfaceArea(double mnX, double mnY, double mnZ, double mxX, double mxY, double mxZ) {
      double dx = mxX - mnX;
      double dy = mxY - mnY;
      double dz = mxZ - mnZ;
      return 2.0 * (dx * dy + dx * dz + dy * dz);
   }

   private void combine(int a, int b, int out) {
      this.minX[out] = Math.min(this.minX[a], this.minX[b]);
      this.minY[out] = Math.min(this.minY[a], this.minY[b]);
      this.minZ[out] = Math.min(this.minZ[a], this.minZ[b]);
      this.maxX[out] = Math.max(this.maxX[a], this.maxX[b]);
      this.maxY[out] = Math.max(this.maxY[a], this.maxY[b]);
      this.maxZ[out] = Math.max(this.maxZ[a], this.maxZ[b]);
   }

   private int chooseBestSibling(int leaf) {
      int index = this.root;

      while (!this.isLeaf(index)) {
         int l = this.left[index];
         int r = this.right[index];
         double area = surfaceArea(this.minX[index], this.minY[index], this.minZ[index], this.maxX[index], this.maxY[index], this.maxZ[index]);
         double cMnX = Math.min(this.minX[index], this.minX[leaf]);
         double cMnY = Math.min(this.minY[index], this.minY[leaf]);
         double cMnZ = Math.min(this.minZ[index], this.minZ[leaf]);
         double cMxX = Math.max(this.maxX[index], this.maxX[leaf]);
         double cMxY = Math.max(this.maxY[index], this.maxY[leaf]);
         double cMxZ = Math.max(this.maxZ[index], this.maxZ[leaf]);
         double combinedArea = surfaceArea(cMnX, cMnY, cMnZ, cMxX, cMxY, cMxZ);
         double inheritanceCost = 2.0 * (combinedArea - area);
         double costLeft = this.descendCost(l, leaf, inheritanceCost);
         double costRight = this.descendCost(r, leaf, inheritanceCost);
         index = costLeft < costRight ? l : r;
      }

      return index;
   }

   private double descendCost(int child, int leaf, double inheritanceCost) {
      double cMnX = Math.min(this.minX[child], this.minX[leaf]);
      double cMnY = Math.min(this.minY[child], this.minY[leaf]);
      double cMnZ = Math.min(this.minZ[child], this.minZ[leaf]);
      double cMxX = Math.max(this.maxX[child], this.maxX[leaf]);
      double cMxY = Math.max(this.maxY[child], this.maxY[leaf]);
      double cMxZ = Math.max(this.maxZ[child], this.maxZ[leaf]);
      double combinedArea = surfaceArea(cMnX, cMnY, cMnZ, cMxX, cMxY, cMxZ);
      if (this.isLeaf(child)) {
         return combinedArea + inheritanceCost;
      } else {
         double oldArea = surfaceArea(this.minX[child], this.minY[child], this.minZ[child], this.maxX[child], this.maxY[child], this.maxZ[child]);
         return combinedArea - oldArea + inheritanceCost;
      }
   }

   private void insertLeaf(int leaf) {
      if (this.root == -1) {
         this.root = leaf;
         this.parent[this.root] = -1;
      } else {
         int sibling = this.chooseBestSibling(leaf);
         int oldParent = this.parent[sibling];
         int newParent = this.allocateNode();
         this.parent[newParent] = oldParent;
         this.left[newParent] = sibling;
         this.right[newParent] = leaf;
         this.parent[sibling] = newParent;
         this.parent[leaf] = newParent;
         this.userData[newParent] = null;
         this.combine(sibling, leaf, newParent);
         this.height[newParent] = this.height[sibling] + 1;
         if (oldParent == -1) {
            this.root = newParent;
         } else if (this.left[oldParent] == sibling) {
            this.left[oldParent] = newParent;
         } else {
            this.right[oldParent] = newParent;
         }

         int index = this.parent[leaf];

         while (index != -1) {
            index = this.balance(index);
            int l = this.left[index];
            int r = this.right[index];
            this.combine(l, r, index);
            this.height[index] = 1 + Math.max(this.height[l], this.height[r]);
            index = this.parent[index];
         }
      }
   }

   private void removeLeaf(int leaf) {
      if (leaf == this.root) {
         this.root = -1;
      } else {
         int parentNode = this.parent[leaf];
         int grandParent = this.parent[parentNode];
         int sibling = this.left[parentNode] == leaf ? this.right[parentNode] : this.left[parentNode];
         if (grandParent != -1) {
            if (this.left[grandParent] == parentNode) {
               this.left[grandParent] = sibling;
            } else {
               this.right[grandParent] = sibling;
            }

            this.parent[sibling] = grandParent;
            this.userData[parentNode] = null;
            this.freeNode(parentNode);
            int index = grandParent;

            while (index != -1) {
               index = this.balance(index);
               int l = this.left[index];
               int r = this.right[index];
               this.combine(l, r, index);
               this.height[index] = 1 + Math.max(this.height[l], this.height[r]);
               index = this.parent[index];
            }
         } else {
            this.root = sibling;
            this.parent[sibling] = -1;
            this.userData[parentNode] = null;
            this.freeNode(parentNode);
         }

         this.parent[leaf] = -1;
      }
   }

   private int balance(int iA) {
      if (iA != -1 && !this.isLeaf(iA) && this.height[iA] >= 2) {
         int iB = this.left[iA];
         int iC = this.right[iA];
         int bal = this.height[iC] - this.height[iB];
         if (bal > 1) {
            int iF = this.left[iC];
            int iG = this.right[iC];
            this.right[iC] = iA;
            int iParent = this.parent[iA];
            this.parent[iC] = iParent;
            this.parent[iA] = iC;
            if (iParent != -1) {
               if (this.left[iParent] == iA) {
                  this.left[iParent] = iC;
               } else {
                  this.right[iParent] = iC;
               }
            } else {
               this.root = iC;
            }

            if (this.height[iF] > this.height[iG]) {
               this.left[iC] = iF;
               this.right[iA] = iG;
               this.parent[iF] = iC;
               this.parent[iG] = iA;
            } else {
               this.left[iC] = iG;
               this.right[iA] = iF;
               this.parent[iG] = iC;
               this.parent[iF] = iA;
            }

            int lA = this.left[iA];
            int rA = this.right[iA];
            this.combine(lA, rA, iA);
            this.height[iA] = 1 + Math.max(this.height[lA], this.height[rA]);
            int lC = this.left[iC];
            int rC = this.right[iC];
            this.combine(lC, rC, iC);
            this.height[iC] = 1 + Math.max(this.height[lC], this.height[rC]);
            return iC;
         } else if (bal < -1) {
            int iD = this.left[iB];
            int iE = this.right[iB];
            this.left[iB] = iA;
            int iParentx = this.parent[iA];
            this.parent[iB] = iParentx;
            this.parent[iA] = iB;
            if (iParentx != -1) {
               if (this.left[iParentx] == iA) {
                  this.left[iParentx] = iB;
               } else {
                  this.right[iParentx] = iB;
               }
            } else {
               this.root = iB;
            }

            if (this.height[iD] > this.height[iE]) {
               this.right[iB] = iD;
               this.left[iA] = iE;
               this.parent[iD] = iB;
               this.parent[iE] = iA;
            } else {
               this.right[iB] = iE;
               this.left[iA] = iD;
               this.parent[iE] = iB;
               this.parent[iD] = iA;
            }

            int lA = this.left[iA];
            int rA = this.right[iA];
            this.combine(lA, rA, iA);
            this.height[iA] = 1 + Math.max(this.height[lA], this.height[rA]);
            int lB = this.left[iB];
            int rB = this.right[iB];
            this.combine(lB, rB, iB);
            this.height[iB] = 1 + Math.max(this.height[lB], this.height[rB]);
            return iB;
         } else {
            return iA;
         }
      } else {
         return iA;
      }
   }

   public interface BVHNode {
      void setId(int var1);

      int getId();
   }

   public interface HitCallback<T> {
      boolean report(int var1, T var2);
   }
}
