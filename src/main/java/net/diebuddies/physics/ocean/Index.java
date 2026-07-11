package net.diebuddies.physics.ocean;

public class Index {
   public static final int xOffset = (int)(Math.log(256.0) / Math.log(2.0));
   public static final int zOffset = (int)(Math.log(16.0) / Math.log(2.0));

   public static long chunk(int x, int y, int z) {
      return ((long)intTo28Bit(x) & 268435455L) << 28 | (long)intTo28Bit(z) & 268435455L | ((long)y & 255L) << 56;
   }

   public static long oceanLayerChunk(int x, int z) {
      return (long)x << 32 | (long)z & 4294967295L;
   }

   public static int getXFromOceanLayer(long index) {
      return (int)(index >> 32);
   }

   public static int getZFromOceanLayer(long index) {
      return (int)index;
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

   public static int chunkStorage(int x, int y, int z) {
      return y << xOffset | z << zOffset | x;
   }

   public static int chunkStorage(int x, int z) {
      return z << zOffset | x;
   }

   public static byte getWaterDepth(short data) {
      return (byte)(data & '\uffff');
   }

   public static byte getWaterHeight(short data) {
      return (byte)(data >> 8 & 65535);
   }

   public static short getWaterDepthAndHeight(byte depth, byte height) {
      return (short)(depth | height << 8);
   }
}
