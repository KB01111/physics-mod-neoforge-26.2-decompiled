package net.diebuddies.physics.snow.thread;

import net.diebuddies.physics.snow.SnowWorld;
import org.joml.Vector3i;

public class ChunkFreeMesh implements Runnable {
   public SnowWorld snowWorld;
   public Vector3i position;
   public int yOffset;

   public ChunkFreeMesh(SnowWorld snowWorld, Vector3i position, int yOffset) {
      this.snowWorld = snowWorld;
      this.position = new Vector3i(position);
      this.yOffset = yOffset;
   }

   @Override
   public void run() {
      this.snowWorld.removeChunkEntity(this.position.x, this.position.y + this.yOffset, this.position.z);
   }
}
