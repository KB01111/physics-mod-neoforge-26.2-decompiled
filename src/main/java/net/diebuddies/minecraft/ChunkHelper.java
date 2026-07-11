package net.diebuddies.minecraft;

import net.minecraft.core.SectionPos;

public class ChunkHelper {
   public static long calcChunkIndex(int chunkX, int chunkZ) {
      return ((long)chunkX & 4294967295L) << 32 | (long)chunkZ & 4294967295L;
   }

   public static int getChunkX(long index) {
      return (int)(index >> 32) & -1;
   }

   public static int getChunkZ(long index) {
      return (int)index & -1;
   }

   public static long calcIndex(int x, int y, int z) {
      return (long)x << 60 | (long)z << 56 | (long)y & 4294967295L;
   }

   public static long calcRelativeIndex(int x, int y, int z) {
      return (long)SectionPos.sectionRelative(x) << 60 | (long)SectionPos.sectionRelative(z) << 56 | (long)y & 4294967295L;
   }

   public static int calcBlockX(long index) {
      return (int)(index >> 60) & 15;
   }

   public static int calcBlockY(long index) {
      return (int)(index & 4294967295L);
   }

   public static int calcBlockZ(long index) {
      return (int)(index >> 56) & 15;
   }

   public static void main(String[] args) {
      int x = 13;
      int y = 42845;
      int z = 9;
      long index = calcIndex(x, y, z);
      System.out.println(calcBlockX(index) + ", " + calcBlockY(index) + ", " + calcBlockZ(index));
   }
}
