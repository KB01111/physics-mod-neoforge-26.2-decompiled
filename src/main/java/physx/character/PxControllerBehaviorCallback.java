package physx.character;

import physx.NativeObject;

public class PxControllerBehaviorCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxControllerBehaviorCallback() {
   }

   private static native int __sizeOf();

   public static PxControllerBehaviorCallback wrapPointer(long address) {
      return address != 0L ? new PxControllerBehaviorCallback(address) : null;
   }

   public static PxControllerBehaviorCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerBehaviorCallback(long address) {
      super(address);
   }
}
