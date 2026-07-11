package net.diebuddies.physics.snow.thread;

import net.diebuddies.opengl.RawMesh;
import net.diebuddies.physics.snow.SnowWorld;
import org.joml.Vector3i;

public class ChunkUploadMesh implements Runnable {
   public SnowWorld snowWorld;
   public Vector3i position;
   public RawMesh mesh;
   public int yOffset;

   public ChunkUploadMesh(SnowWorld snowWorld, Vector3i position, RawMesh mesh, int yOffset) {
      this.snowWorld = snowWorld;
      this.position = new Vector3i(position);
      this.mesh = mesh;
      this.yOffset = yOffset;
   }

   @Override
   public void run() {
   }
}
