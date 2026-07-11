package physx.geometry;

import physx.common.PxBase;

public class PxBVH extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxBVH() {
   }

   private static native int __sizeOf();

   public static PxBVH wrapPointer(long address) {
      return address != 0L ? new PxBVH(address) : null;
   }

   public static PxBVH arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBVH(long address) {
      super(address);
   }
}
