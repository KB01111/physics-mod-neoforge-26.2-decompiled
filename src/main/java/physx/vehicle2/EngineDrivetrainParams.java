package physx.vehicle2;

import physx.NativeObject;

public class EngineDrivetrainParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static EngineDrivetrainParams wrapPointer(long address) {
      return address != 0L ? new EngineDrivetrainParams(address) : null;
   }

   public static EngineDrivetrainParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected EngineDrivetrainParams(long address) {
      super(address);
   }

   public EngineDrivetrainParams() {
      this.address = _EngineDrivetrainParams();
   }

   private static native long _EngineDrivetrainParams();

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

   public PxVehicleAutoboxParams getAutoboxParams() {
      this.checkNotNull();
      return PxVehicleAutoboxParams.wrapPointer(_getAutoboxParams(this.address));
   }

   private static native long _getAutoboxParams(long var0);

   public void setAutoboxParams(PxVehicleAutoboxParams value) {
      this.checkNotNull();
      _setAutoboxParams(this.address, value.getAddress());
   }

   private static native void _setAutoboxParams(long var0, long var2);

   public PxVehicleClutchCommandResponseParams getClutchCommandResponseParams() {
      this.checkNotNull();
      return PxVehicleClutchCommandResponseParams.wrapPointer(_getClutchCommandResponseParams(this.address));
   }

   private static native long _getClutchCommandResponseParams(long var0);

   public void setClutchCommandResponseParams(PxVehicleClutchCommandResponseParams value) {
      this.checkNotNull();
      _setClutchCommandResponseParams(this.address, value.getAddress());
   }

   private static native void _setClutchCommandResponseParams(long var0, long var2);

   public PxVehicleEngineParams getEngineParams() {
      this.checkNotNull();
      return PxVehicleEngineParams.wrapPointer(_getEngineParams(this.address));
   }

   private static native long _getEngineParams(long var0);

   public void setEngineParams(PxVehicleEngineParams value) {
      this.checkNotNull();
      _setEngineParams(this.address, value.getAddress());
   }

   private static native void _setEngineParams(long var0, long var2);

   public PxVehicleGearboxParams getGearBoxParams() {
      this.checkNotNull();
      return PxVehicleGearboxParams.wrapPointer(_getGearBoxParams(this.address));
   }

   private static native long _getGearBoxParams(long var0);

   public void setGearBoxParams(PxVehicleGearboxParams value) {
      this.checkNotNull();
      _setGearBoxParams(this.address, value.getAddress());
   }

   private static native void _setGearBoxParams(long var0, long var2);

   public PxVehicleMultiWheelDriveDifferentialParams getMultiWheelDifferentialParams() {
      this.checkNotNull();
      return PxVehicleMultiWheelDriveDifferentialParams.wrapPointer(_getMultiWheelDifferentialParams(this.address));
   }

   private static native long _getMultiWheelDifferentialParams(long var0);

   public void setMultiWheelDifferentialParams(PxVehicleMultiWheelDriveDifferentialParams value) {
      this.checkNotNull();
      _setMultiWheelDifferentialParams(this.address, value.getAddress());
   }

   private static native void _setMultiWheelDifferentialParams(long var0, long var2);

   public PxVehicleFourWheelDriveDifferentialParams getFourWheelDifferentialParams() {
      this.checkNotNull();
      return PxVehicleFourWheelDriveDifferentialParams.wrapPointer(_getFourWheelDifferentialParams(this.address));
   }

   private static native long _getFourWheelDifferentialParams(long var0);

   public void setFourWheelDifferentialParams(PxVehicleFourWheelDriveDifferentialParams value) {
      this.checkNotNull();
      _setFourWheelDifferentialParams(this.address, value.getAddress());
   }

   private static native void _setFourWheelDifferentialParams(long var0, long var2);

   public PxVehicleTankDriveDifferentialParams getTankDifferentialParams() {
      this.checkNotNull();
      return PxVehicleTankDriveDifferentialParams.wrapPointer(_getTankDifferentialParams(this.address));
   }

   private static native long _getTankDifferentialParams(long var0);

   public void setTankDifferentialParams(PxVehicleTankDriveDifferentialParams value) {
      this.checkNotNull();
      _setTankDifferentialParams(this.address, value.getAddress());
   }

   private static native void _setTankDifferentialParams(long var0, long var2);

   public PxVehicleClutchParams getClutchParams() {
      this.checkNotNull();
      return PxVehicleClutchParams.wrapPointer(_getClutchParams(this.address));
   }

   private static native long _getClutchParams(long var0);

   public void setClutchParams(PxVehicleClutchParams value) {
      this.checkNotNull();
      _setClutchParams(this.address, value.getAddress());
   }

   private static native void _setClutchParams(long var0, long var2);

   public EngineDrivetrainParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
