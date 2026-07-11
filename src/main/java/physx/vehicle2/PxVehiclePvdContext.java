package physx.vehicle2;

import physx.NativeObject;

public class PxVehiclePvdContext extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehiclePvdContext() {
   }

   private static native int __sizeOf();

   public static PxVehiclePvdContext wrapPointer(long address) {
      return address != 0L ? new PxVehiclePvdContext(address) : null;
   }

   public static PxVehiclePvdContext arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePvdContext(long address) {
      super(address);
   }
}
