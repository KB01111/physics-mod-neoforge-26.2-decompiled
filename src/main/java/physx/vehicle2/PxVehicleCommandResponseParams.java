package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleCommandResponseParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleCommandResponseParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleCommandResponseParams(address) : null;
   }

   public static PxVehicleCommandResponseParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleCommandResponseParams(long address) {
      super(address);
   }

   public PxVehicleCommandResponseParams() {
      this.address = _PxVehicleCommandResponseParams();
   }

   private static native long _PxVehicleCommandResponseParams();

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

   public PxVehicleCommandNonLinearResponseParams getNonlinearResponse() {
      this.checkNotNull();
      return PxVehicleCommandNonLinearResponseParams.wrapPointer(_getNonlinearResponse(this.address));
   }

   private static native long _getNonlinearResponse(long var0);

   public void setNonlinearResponse(PxVehicleCommandNonLinearResponseParams value) {
      this.checkNotNull();
      _setNonlinearResponse(this.address, value.getAddress());
   }

   private static native void _setNonlinearResponse(long var0, long var2);

   public float getWheelResponseMultipliers(int index) {
      this.checkNotNull();
      return _getWheelResponseMultipliers(this.address, index);
   }

   private static native float _getWheelResponseMultipliers(long var0, int var2);

   public void setWheelResponseMultipliers(int index, float value) {
      this.checkNotNull();
      _setWheelResponseMultipliers(this.address, index, value);
   }

   private static native void _setWheelResponseMultipliers(long var0, int var2, float var3);

   public float getMaxResponse() {
      this.checkNotNull();
      return _getMaxResponse(this.address);
   }

   private static native float _getMaxResponse(long var0);

   public void setMaxResponse(float value) {
      this.checkNotNull();
      _setMaxResponse(this.address, value);
   }

   private static native void _setMaxResponse(long var0, float var2);
}
