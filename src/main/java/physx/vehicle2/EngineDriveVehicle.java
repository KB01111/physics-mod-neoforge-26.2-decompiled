package physx.vehicle2;

import physx.cooking.PxCookingParams;
import physx.physics.PxMaterial;
import physx.physics.PxPhysics;

public class EngineDriveVehicle extends PhysXActorVehicle {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static EngineDriveVehicle wrapPointer(long address) {
      return address != 0L ? new EngineDriveVehicle(address) : null;
   }

   public static EngineDriveVehicle arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected EngineDriveVehicle(long address) {
      super(address);
   }

   public EngineDriveVehicle() {
      this.address = _EngineDriveVehicle();
   }

   private static native long _EngineDriveVehicle();

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

   public EngineDrivetrainParams getEngineDriveParams() {
      this.checkNotNull();
      return EngineDrivetrainParams.wrapPointer(_getEngineDriveParams(this.address));
   }

   private static native long _getEngineDriveParams(long var0);

   public void setEngineDriveParams(EngineDrivetrainParams value) {
      this.checkNotNull();
      _setEngineDriveParams(this.address, value.getAddress());
   }

   private static native void _setEngineDriveParams(long var0, long var2);

   public EngineDrivetrainState getEngineDriveState() {
      this.checkNotNull();
      return EngineDrivetrainState.wrapPointer(_getEngineDriveState(this.address));
   }

   private static native long _getEngineDriveState(long var0);

   public void setEngineDriveState(EngineDrivetrainState value) {
      this.checkNotNull();
      _setEngineDriveState(this.address, value.getAddress());
   }

   private static native void _setEngineDriveState(long var0, long var2);

   public PxVehicleEngineDriveTransmissionCommandState getTransmissionCommandState() {
      this.checkNotNull();
      return PxVehicleEngineDriveTransmissionCommandState.wrapPointer(_getTransmissionCommandState(this.address));
   }

   private static native long _getTransmissionCommandState(long var0);

   public void setTransmissionCommandState(PxVehicleEngineDriveTransmissionCommandState value) {
      this.checkNotNull();
      _setTransmissionCommandState(this.address, value.getAddress());
   }

   private static native void _setTransmissionCommandState(long var0, long var2);

   public PxVehicleTankDriveTransmissionCommandState getTankDriveTransmissionCommandState() {
      this.checkNotNull();
      return PxVehicleTankDriveTransmissionCommandState.wrapPointer(_getTankDriveTransmissionCommandState(this.address));
   }

   private static native long _getTankDriveTransmissionCommandState(long var0);

   public void setTankDriveTransmissionCommandState(PxVehicleTankDriveTransmissionCommandState value) {
      this.checkNotNull();
      _setTankDriveTransmissionCommandState(this.address, value.getAddress());
   }

   private static native void _setTankDriveTransmissionCommandState(long var0, long var2);

   public EngineDriveVehicleEnum getDifferentialType() {
      this.checkNotNull();
      return EngineDriveVehicleEnum.forValue(_getDifferentialType(this.address));
   }

   private static native int _getDifferentialType(long var0);

   public void setDifferentialType(EngineDriveVehicleEnum value) {
      this.checkNotNull();
      _setDifferentialType(this.address, value.value);
   }

   private static native void _setDifferentialType(long var0, int var2);

   public boolean initialize(PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial, EngineDriveVehicleEnum differentialType) {
      this.checkNotNull();
      return _initialize(this.address, physics.getAddress(), params.getAddress(), defaultMaterial.getAddress(), differentialType.value);
   }

   private static native boolean _initialize(long var0, long var2, long var4, long var6, int var8);

   public boolean initialize(
      PxPhysics physics, PxCookingParams params, PxMaterial defaultMaterial, EngineDriveVehicleEnum differentialType, boolean addPhysXBeginEndComponents
   ) {
      this.checkNotNull();
      return _initialize(
         this.address, physics.getAddress(), params.getAddress(), defaultMaterial.getAddress(), differentialType.value, addPhysXBeginEndComponents
      );
   }

   private static native boolean _initialize(long var0, long var2, long var4, long var6, int var8, boolean var9);

   @Override
   public void initComponentSequence(boolean addPhysXBeginEndComponents) {
      this.checkNotNull();
      _initComponentSequence(this.address, addPhysXBeginEndComponents);
   }

   private static native void _initComponentSequence(long var0, boolean var2);
}
