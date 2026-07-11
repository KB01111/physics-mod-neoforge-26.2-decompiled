package physx.vehicle2;

public class PxVehicleBrakeCommandResponseParams extends PxVehicleCommandResponseParams {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleBrakeCommandResponseParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleBrakeCommandResponseParams(address) : null;
   }

   public static PxVehicleBrakeCommandResponseParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleBrakeCommandResponseParams(long address) {
      super(address);
   }

   public PxVehicleBrakeCommandResponseParams() {
      this.address = _PxVehicleBrakeCommandResponseParams();
   }

   private static native long _PxVehicleBrakeCommandResponseParams();

   @Override
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

   public PxVehicleBrakeCommandResponseParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
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
