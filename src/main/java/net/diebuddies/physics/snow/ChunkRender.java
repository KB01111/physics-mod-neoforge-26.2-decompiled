package net.diebuddies.physics.snow;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.snow.contouring.OctreeNode;

public class ChunkRender {
   public List<OctreeNode> edgeNodes = new ObjectArrayList();
   public volatile int voxelLevelOfDetail = 3;

   public void setEdgeNodes(OctreeNode node) {
      this.edgeNodes.clear();
      this.searchEdgeNodes(node);
   }

   private void searchEdgeNodes(OctreeNode node) {
      if (node != null && node.edge) {
         if (node.leaf) {
            this.edgeNodes.add(node);
         } else {
            for (int i = 0; i < node.children.length; i++) {
               this.searchEdgeNodes(node.children[i]);
            }
         }
      }
   }
}
