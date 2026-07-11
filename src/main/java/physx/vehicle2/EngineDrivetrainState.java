package physx.vehicle2;

import physx.NativeObject;

public class EngineDrivetrainState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static EngineDrivetrainState wrapPointer(long address) {
      return address != 0L ? new EngineDrivetrainState(address) : null;
   }

   public static EngineDrivetrainState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected EngineDrivetrainState(long address) {
      super(address);
   }

   public EngineDrivetrainState() {
      this.address = _EngineDrivetrainState();
   }

   private static native long _EngineDrivetrainState();

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

   public PxVehicleEngineDriveThrottleCommandResponseState getThrottleCommandResponseState() {
      this.checkNotNull();
      return PxVehicleEngineDriveThrottleCommandResponseState.wrapPointer(_getThrottleCommandResponseState(this.address));
   }

   private static native long _getThrottleCommandResponseState(long var0);

   public void setThrottleCommandResponseState(PxVehicleEngineDriveThrottleCommandResponseState value) {
      this.checkNotNull();
      _setThrottleCommandResponseState(this.address, value.getAddress());
   }

   private static native void _setThrottleCommandResponseState(long var0, long var2);

   public PxVehicleAutoboxState getAutoboxState() {
      this.checkNotNull();
      return PxVehicleAutoboxState.wrapPointer(_getAutoboxState(this.address));
   }

   private static native long _getAutoboxState(long var0);

   public void setAutoboxState(PxVehicleAutoboxState value) {
      this.checkNotNull();
      _setAutoboxState(this.address, value.getAddress());
   }

   private static native void _setAutoboxState(long var0, long var2);

   public PxVehicleClutchCommandResponseState getClutchCommandResponseState() {
      this.checkNotNull();
      return PxVehicleClutchCommandResponseState.wrapPointer(_getClutchCommandResponseState(this.address));
   }

   private static native long _getClutchCommandResponseState(long var0);

   public void setClutchCommandResponseState(PxVehicleClutchCommandResponseState value) {
      this.checkNotNull();
      _setClutchCommandResponseState(this.address, value.getAddress());
   }

   private static native void _setClutchCommandResponseState(long var0, long var2);

   public PxVehicleDifferentialState getDifferentialState() {
      this.checkNotNull();
      return PxVehicleDifferentialState.wrapPointer(_getDifferentialState(this.address));
   }

   private static native long _getDifferentialState(long var0);

   public void setDifferentialState(PxVehicleDifferentialState value) {
      this.checkNotNull();
      _setDifferentialState(this.address, value.getAddress());
   }

   private static native void _setDifferentialState(long var0, long var2);

   public PxVehicleWheelConstraintGroupState getWheelConstraintGroupState() {
      this.checkNotNull();
      return PxVehicleWheelConstraintGroupState.wrapPointer(_getWheelConstraintGroupState(this.address));
   }

   private static native long _getWheelConstraintGroupState(long var0);

   public void setWheelConstraintGroupState(PxVehicleWheelConstraintGroupState value) {
      this.checkNotNull();
      _setWheelConstraintGroupState(this.address, value.getAddress());
   }

   private static native void _setWheelConstraintGroupState(long var0, long var2);

   public PxVehicleEngineState getEngineState() {
      this.checkNotNull();
      return PxVehicleEngineState.wrapPointer(_getEngineState(this.address));
   }

   private static native long _getEngineState(long var0);

   public void setEngineState(PxVehicleEngineState value) {
      this.checkNotNull();
      _setEngineState(this.address, value.getAddress());
   }

   private static native void _setEngineState(long var0, long var2);

   public PxVehicleGearboxState getGearboxState() {
      this.checkNotNull();
      return PxVehicleGearboxState.wrapPointer(_getGearboxState(this.address));
   }

   private static native long _getGearboxState(long var0);

   public void setGearboxState(PxVehicleGearboxState value) {
      this.checkNotNull();
      _setGearboxState(this.address, value.getAddress());
   }

   private static native void _setGearboxState(long var0, long var2);

   public PxVehicleClutchSlipState getClutchState() {
      this.checkNotNull();
      return PxVehicleClutchSlipState.wrapPointer(_getClutchState(this.address));
   }

   private static native long _getClutchState(long var0);

   public void setClutchState(PxVehicleClutchSlipState value) {
      this.checkNotNull();
      _setClutchState(this.address, value.getAddress());
   }

   private static native void _setClutchState(long var0, long var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
