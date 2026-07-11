package net.diebuddies.physics;

public class PhysicsIndex {
   private static final int BITS_Y = 12;
   private static final int BITS_XZ = 26;
   private static final long MASK_Y = 4095L;
   private static final long MASK_XZ = 67108863L;
   private static final int BIAS_XZ = 33554432;
   private static final int BIAS_Y = 2048;

   public static long pack(int cx, int cy, int cz) {
      long ux = (long)(cx + 33554432) & 67108863L;
      long uy = (long)(cy + 2048) & 4095L;
      long uz = (long)(cz + 33554432) & 67108863L;
      return ux << 38 | uz << 12 | uy;
   }

   public static int unpackX(long key) {
      return (int)(key >>> 38 & 67108863L) - 33554432;
   }

   public static int unpackZ(long key) {
      return (int)(key >>> 12 & 67108863L) - 33554432;
   }

   public static int unpackY(long key) {
      return (int)(key & 4095L) - 2048;
   }
}
