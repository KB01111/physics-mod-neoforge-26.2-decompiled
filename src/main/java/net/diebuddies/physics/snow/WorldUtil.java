package net.diebuddies.physics.snow;

import net.diebuddies.math.Math;

public class WorldUtil {
   public static int calculateVoxelPosX(int x) {
      return x & IChunk.CHUNK_SIZE_BITS;
   }

   public static int calculateVoxelPosY(int y) {
      return y & IChunk.CHUNK_SIZE_BITS;
   }

   public static int calculateVoxelPosZ(int z) {
      return z & IChunk.CHUNK_SIZE_BITS;
   }

   public static float calculateVoxelPosX(double x) {
      long iPart = (long)x;
      float result = (float)((double)(iPart & (long)IChunk.CHUNK_SIZE_BITS) + x - (double)iPart);
      return result < 0.0F ? result + (float)IChunk.CHUNK_SIZE : result;
   }

   public static float calculateVoxelPosY(double y) {
      long iPart = (long)y;
      float result = (float)((double)(iPart & (long)IChunk.CHUNK_SIZE_BITS) + y - (double)iPart);
      return result < 0.0F ? result + (float)IChunk.CHUNK_SIZE : result;
   }

   public static float calculateVoxelPosZ(double z) {
      long iPart = (long)z;
      float result = (float)((double)(iPart & (long)IChunk.CHUNK_SIZE_BITS) + z - (double)iPart);
      return result < 0.0F ? result + (float)IChunk.CHUNK_SIZE : result;
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
      int chunkX = (int)(x / (double)IChunk.CHUNK_SIZE);
      if (x < 0.0) {
         chunkX--;
      }

      return chunkX;
   }

   public static int calculateChunkPosY(IWorld<?> world, double y) {
      int chunkY = (int)(y / (double)IChunk.CHUNK_SIZE);
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
      int chunkZ = (int)(z / (double)IChunk.CHUNK_SIZE);
      if (z < 0.0) {
         chunkZ--;
      }

      return chunkZ;
   }
}
