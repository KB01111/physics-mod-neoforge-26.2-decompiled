package net.diebuddies.physics.snow.thread;

import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.WorldContouring;

public class ChunkUnloadMeshEvent implements Runnable {
   private WorldContouring world;
   private int x;
   private int y;
   private int z;
   private boolean seam;

   public ChunkUnloadMeshEvent(WorldContouring world, int x, int y, int z, boolean seam) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.seam = seam;
   }

   @Override
   public void run() {
      ChunkContouring chunk = this.world.getChunk(this.x, this.y, this.z);
      if (chunk != null) {
         chunk.setTriangles(this.world, null, -1, this.seam);
      }
   }
}
