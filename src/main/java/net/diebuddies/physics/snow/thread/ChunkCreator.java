package net.diebuddies.physics.snow.thread;

import net.diebuddies.physics.snow.ChunkContouring;

public interface ChunkCreator {
   int getX();

   int getY();

   int getZ();

   ChunkContouring create();
}
