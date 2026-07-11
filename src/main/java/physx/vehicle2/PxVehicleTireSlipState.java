package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireSlipState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireSlipState wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireSlipState(address) : null;
   }

   public static PxVehicleTireSlipState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireSlipState(long address) {
      super(address);
   }

   public PxVehicleTireSlipState() {
      this.address = _PxVehicleTireSlipState();
   }

   private static native long _PxVehicleTireSlipState();

   public void destroy() {
      if (this.address == 0L) {
         throw new IllegalStateException(this + " is already deleted");
      } else if (this.isExternallyAllocated) {
         throw new IllegalStateException(this + " is externally allocated and cannot be manually destroyed");
      } else {
         _delete_native_instance(this.address);
         this.address = 0L;
      }
   }

   private static native long _delete_native_instance(long var0);

   public float getSlips(int index) {
      this.checkNotNull();
      return _getSlips(this.address, index);
   }

   private static native float _getSlips(long var0, int var2);

   public void setSlips(int index, float value) {
      this.checkNotNull();
      _setSlips(this.address, index, value);
   }

   private static native void _setSlips(long var0, int var2, float var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
