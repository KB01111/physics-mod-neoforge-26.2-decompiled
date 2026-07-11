package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireForceParamsExt extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehicleTireForceParamsExt() {
   }

   private static native int __sizeOf();

   public static PxVehicleTireForceParamsExt wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireForceParamsExt(address) : null;
   }

   public static PxVehicleTireForceParamsExt arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireForceParamsExt(long address) {
      super(address);
   }

   public static void setFrictionVsSlip(PxVehicleTireForceParams tireForceParams, int i, int j, float value) {
      _setFrictionVsSlip(tireForceParams.getAddress(), i, j, value);
   }

   private static native void _setFrictionVsSlip(long var0, int var2, int var3, float var4);

   public static void setLoadFilter(PxVehicleTireForceParams tireForceParams, int i, int j, float value) {
      _setLoadFilter(tireForceParams.getAddress(), i, j, value);
   }

   private static native void _setLoadFilter(long var0, int var2, int var3, float var4);
}
