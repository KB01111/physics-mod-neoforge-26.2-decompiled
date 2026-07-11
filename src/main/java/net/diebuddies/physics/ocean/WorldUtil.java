package net.diebuddies.physics.ocean;

import net.diebuddies.math.Math;

public class WorldUtil {
   public static int calculateVoxelPosX(int x) {
      return x & 15;
   }

   public static int calculateVoxelPosY(int y) {
      return y & 15;
   }

   public static int calculateVoxelPosZ(int z) {
      return z & 15;
   }

   public static float calculateVoxelPosX(double x) {
      long iPart = (long)x;
      float result = (float)((double)(iPart & 15L) + x - (double)iPart);
      return result < 0.0F ? result + 16.0F : result;
   }

   public static float calculateVoxelPosY(double y) {
      long iPart = (long)y;
      float result = (float)((double)(iPart & 15L) + y - (double)iPart);
      return result < 0.0F ? result + 16.0F : result;
   }

   public static float calculateVoxelPosZ(double z) {
      long iPart = (long)z;
      float result = (float)((double)(iPart & 15L) + z - (double)iPart);
      return result < 0.0F ? result + 16.0F : result;
   }

   public static int calculateChunkPosX(int x) {
      return x >> IChunk.CHUNK_SIZE_USED_BITS;
   }

   public static int calculateChunkPosY(IWorld<?> world, int y) {
      return Math.clamp(y >> IChunk.CHUNK_SIZE_USED_BITS, world.minChunkY, world.maxChunkY);
   }

   public static int calculateChunkPosZ(int z) {
      return z >> IChunk.CHUNK_SIZE_USED_BITS;
   }

   public static int calculateChunkPosX(double x) {
      int chunkX = (int)(x / 16.0);
      if (x < 0.0) {
         chunkX--;
      }

      return chunkX;
   }

   public static int calculateChunkPosY(IWorld<?> world, double y) {
      int chunkY = (int)(y / 16.0);
      if (y < 0.0) {
         chunkY--;
      }

      if (chunkY < world.minChunkY) {
         chunkY = world.minChunkY;
      }

      if (chunkY > world.maxChunkY) {
         chunkY = world.maxChunkY;
      }

      return chunkY;
   }

   public static int calculateChunkPosZ(double z) {
      int chunkZ = (int)(z / 16.0);
      if (z < 0.0) {
         chunkZ--;
      }

      return chunkZ;
   }
}
