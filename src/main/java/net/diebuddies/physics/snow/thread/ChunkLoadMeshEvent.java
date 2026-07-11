package net.diebuddies.physics.snow.thread;

import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.snow.ChunkContouring;
import net.diebuddies.physics.snow.IChunk;
import net.diebuddies.physics.snow.SnowBatch;
import net.diebuddies.physics.snow.WorldContouring;
import net.diebuddies.physics.snow.contouring.Vertex;
import org.joml.Vector3d;

public class ChunkLoadMeshEvent implements Runnable {
   private WorldContouring world;
   private int x;
   private int y;
   private int z;
   private RawMesh model;
   private boolean seam;
   private int lod;

   public ChunkLoadMeshEvent(WorldContouring world, int x, int y, int z, List<Vertex> vertices, IntList indices, int lod, boolean seam) {
      this.world = world;
      this.x = x;
      this.y = y;
      this.z = z;
      this.seam = seam;
      this.lod = lod;
      Vector3d offset = new Vector3d(
         (double)(SnowBatch.shiftPos(x) * IChunk.CHUNK_SIZE),
         (double)(SnowBatch.shiftPos(y) * IChunk.CHUNK_SIZE),
         (double)(SnowBatch.shiftPos(z) * IChunk.CHUNK_SIZE)
      );
      this.model = world.dualContouring.generateMesh(offset, vertices, indices);
   }

   @Override
   public void run() {
      ChunkContouring chunk = this.world.getChunk(this.x, this.y, this.z);
      if (chunk != null) {
         chunk.setTriangles(this.world, this.model, this.lod, this.seam);
      } else if (this.model != null) {
         this.model.destroy();
      }
   }
}
