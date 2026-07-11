package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleSuspensionStateCalculationParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleSuspensionStateCalculationParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleSuspensionStateCalculationParams(address) : null;
   }

   public static PxVehicleSuspensionStateCalculationParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleSuspensionStateCalculationParams(long address) {
      super(address);
   }

   public PxVehicleSuspensionStateCalculationParams() {
      this.address = _PxVehicleSuspensionStateCalculationParams();
   }

   private static native long _PxVehicleSuspensionStateCalculationParams();

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

   public PxVehicleSuspensionJounceCalculationTypeEnum getSuspensionJounceCalculationType() {
      this.checkNotNull();
      return PxVehicleSuspensionJounceCalculationTypeEnum.forValue(_getSuspensionJounceCalculationType(this.address));
   }

   private static native int _getSuspensionJounceCalculationType(long var0);

   public void setSuspensionJounceCalculationType(PxVehicleSuspensionJounceCalculationTypeEnum value) {
      this.checkNotNull();
      _setSuspensionJounceCalculationType(this.address, value.value);
   }

   private static native void _setSuspensionJounceCalculationType(long var0, int var2);

   public boolean getLimitSuspensionExpansionVelocity() {
      this.checkNotNull();
      return _getLimitSuspensionExpansionVelocity(this.address);
   }

   private static native boolean _getLimitSuspensionExpansionVelocity(long var0);

   public void setLimitSuspensionExpansionVelocity(boolean value) {
      this.checkNotNull();
      _setLimitSuspensionExpansionVelocity(this.address, value);
   }

   private static native void _setLimitSuspensionExpansionVelocity(long var0, boolean var2);

   public PxVehicleSuspensionStateCalculationParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
