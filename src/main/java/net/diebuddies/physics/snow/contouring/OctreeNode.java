package net.diebuddies.physics.snow.contouring;

public class OctreeNode {
   public OctreeNode[] children;
   public OctreeDrawInfo drawInfo;
   public int minX = Integer.MAX_VALUE;
   public int minY = Integer.MAX_VALUE;
   public int minZ = Integer.MAX_VALUE;
   public short size;
   public boolean leaf;
   public boolean edge;

   public OctreeNode(boolean leaf) {
      if (!leaf) {
         this.children = new OctreeNode[8];
      }

      this.leaf = leaf;
   }

   public OctreeNode() {
      this.children = new OctreeNode[8];
      this.leaf = false;
   }

   @Override
   public String toString() {
      return this.generateString(0);
   }

   public void reset(boolean leaf) {
      this.children = new OctreeNode[8];
      this.minX = Integer.MAX_VALUE;
      this.minY = Integer.MAX_VALUE;
      this.minZ = Integer.MAX_VALUE;
      this.size = 0;
      this.drawInfo = null;
      this.leaf = leaf;
      this.edge = false;
   }

   public String generateString(int spaces) {
      String spacesString = "";

      for (int i = 0; i < spaces; i++) {
         spacesString = spacesString + " ";
      }

      int childrenCount = 0;

      for (int i = 0; i < this.children.length; i++) {
         if (this.children[i] != null) {
            childrenCount++;
         }
      }

      spacesString = spacesString
         + this.minX
         + ", size: "
         + this.size
         + ", draw info: "
         + this.drawInfo
         + ", children: "
         + childrenCount
         + ", leaf: "
         + this.leaf
         + "\n";

      for (int ix = 0; ix < this.children.length; ix++) {
         if (this.children[ix] != null) {
            spacesString = spacesString + this.children[ix].generateString(spaces + 1);
         }
      }

      return spacesString;
   }

   public int getChildrenSize() {
      int childrenSize = 0;

      for (int i = 0; this.children != null && i < this.children.length; i++) {
         if (this.children[i] != null) {
            childrenSize = ++childrenSize + this.children[i].getChildrenSize();
         }
      }

      return childrenSize;
   }

   public int getChildrenSize(boolean leaf) {
      int childrenSize = 0;

      for (int i = 0; this.children != null && i < this.children.length; i++) {
         if (this.children[i] != null) {
            if (this.children[i].leaf == leaf) {
               childrenSize++;
            }

            childrenSize += this.children[i].getChildrenSize(leaf);
         }
      }

      return childrenSize;
   }

   public OctreeNode copy() {
      OctreeNode node = new OctreeNode(this.leaf);
      node.children = this.children;
      node.leaf = this.leaf;
      node.drawInfo = this.drawInfo;
      node.minX = this.minX;
      node.minY = this.minY;
      node.minZ = this.minZ;
      node.size = this.size;
      return node;
   }
}
