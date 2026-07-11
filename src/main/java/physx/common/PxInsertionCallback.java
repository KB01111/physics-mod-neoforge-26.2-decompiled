package physx.common;

import physx.NativeObject;

public class PxInsertionCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxInsertionCallback() {
   }

   private static native int __sizeOf();

   public static PxInsertionCallback wrapPointer(long address) {
      return address != 0L ? new PxInsertionCallback(address) : null;
   }

   public static PxInsertionCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxInsertionCallback(long address) {
      super(address);
   }
}
