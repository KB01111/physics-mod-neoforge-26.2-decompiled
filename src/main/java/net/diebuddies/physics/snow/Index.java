package net.diebuddies.physics.snow;

import org.joml.Vector3i;

public class Index {
   public static int xOffset;
   public static int zOffset;
   public static int bitmask;
   public static final int vanillaXOffset = 8;
   public static final int vanillaZOffset = 4;

   public static void updateMasks() {
      xOffset = (int)(Math.log((double)(IChunk.CHUNK_SIZE * IChunk.CHUNK_SIZE)) / Math.log(2.0));
      zOffset = (int)(Math.log((double)IChunk.CHUNK_SIZE) / Math.log(2.0));
      bitmask = (int)Math.round(Math.pow(2.0, (double)zOffset)) - 1;
   }

   public static long chunk(int x, int y, int z) {
      return ((long)intTo28Bit(x) & 268435455L) << 28 | (long)intTo28Bit(z) & 268435455L | ((long)y & 255L) << 56;
   }

   private static int intTo28Bit(int value) {
      return value & 268435455;
   }

   private static int intFrom28Bit(int bit28) {
      return (bit28 & 251658240) << 4 | bit28;
   }

   public static int getXFromIndexChunk(long index) {
      return intFrom28Bit((int)(index >> 28 & 268435455L));
   }

   public static int getYFromIndexChunk(long index) {
      return (byte)((int)(index >> 56 & 255L));
   }

   public static int getZFromIndexChunk(long index) {
      return intFrom28Bit((int)(index & 268435455L));
   }

   public static Vector3i getPosFromIndexChunk(long index, Vector3i dst) {
      return dst.set(getXFromIndexChunk(index), getYFromIndexChunk(index), getZFromIndexChunk(index));
   }

   public static int chunkStorage(int x, int y, int z) {
      return x << xOffset | z << zOffset | y;
   }

   public static int vanillaChunkStorage(int x, int y, int z) {
      return x << 8 | z << 4 | y;
   }

   public static int chunkStorageXPos(int index) {
      return index >> xOffset & bitmask;
   }

   public static int chunkStorageYPos(int index) {
      return index & bitmask;
   }

   public static int chunkStorageZPos(int index) {
      return index >> zOffset & bitmask;
   }

   public static int chunkStorage(int lod, int x, int y, int z) {
      return x << xOffset - lod * 2 | z << zOffset - lod | y;
   }

   static {
      updateMasks();
   }
}
