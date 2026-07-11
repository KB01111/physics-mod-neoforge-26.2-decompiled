package physx.vehicle2;

import physx.NativeObject;

public class DirectDrivetrainParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static DirectDrivetrainParams wrapPointer(long address) {
      return address != 0L ? new DirectDrivetrainParams(address) : null;
   }

   public static DirectDrivetrainParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected DirectDrivetrainParams(long address) {
      super(address);
   }

   public DirectDrivetrainParams() {
      this.address = _DirectDrivetrainParams();
   }

   private static native long _DirectDrivetrainParams();

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

   public PxVehicleDirectDriveThrottleCommandResponseParams getDirectDriveThrottleResponseParams() {
      this.checkNotNull();
      return PxVehicleDirectDriveThrottleCommandResponseParams.wrapPointer(_getDirectDriveThrottleResponseParams(this.address));
   }

   private static native long _getDirectDriveThrottleResponseParams(long var0);

   public void setDirectDriveThrottleResponseParams(PxVehicleDirectDriveThrottleCommandResponseParams value) {
      this.checkNotNull();
      _setDirectDriveThrottleResponseParams(this.address, value.getAddress());
   }

   private static native void _setDirectDriveThrottleResponseParams(long var0, long var2);

   public DirectDrivetrainParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid(PxVehicleAxleDescription axleDesc) {
      this.checkNotNull();
      return _isValid(this.address, axleDesc.getAddress());
   }

   private static native boolean _isValid(long var0, long var2);
}
