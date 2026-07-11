package physx.vehicle2;

import physx.NativeObject;

public class BaseVehicleParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static BaseVehicleParams wrapPointer(long address) {
      return address != 0L ? new BaseVehicleParams(address) : null;
   }

   public static BaseVehicleParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected BaseVehicleParams(long address) {
      super(address);
   }

   public BaseVehicleParams() {
      this.address = _BaseVehicleParams();
   }

   private static native long _BaseVehicleParams();

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

   public PxVehicleAxleDescription getAxleDescription() {
      this.checkNotNull();
      return PxVehicleAxleDescription.wrapPointer(_getAxleDescription(this.address));
   }

   private static native long _getAxleDescription(long var0);

   public void setAxleDescription(PxVehicleAxleDescription value) {
      this.checkNotNull();
      _setAxleDescription(this.address, value.getAddress());
   }

   private static native void _setAxleDescription(long var0, long var2);

   public PxVehicleFrame getFrame() {
      this.checkNotNull();
      return PxVehicleFrame.wrapPointer(_getFrame(this.address));
   }

   private static native long _getFrame(long var0);

   public void setFrame(PxVehicleFrame value) {
      this.checkNotNull();
      _setFrame(this.address, value.getAddress());
   }

   private static native void _setFrame(long var0, long var2);

   public PxVehicleScale getScale() {
      this.checkNotNull();
      return PxVehicleScale.wrapPointer(_getScale(this.address));
   }

   private static native long _getScale(long var0);

   public void setScale(PxVehicleScale value) {
      this.checkNotNull();
      _setScale(this.address, value.getAddress());
   }

   private static native void _setScale(long var0, long var2);

   public PxVehicleSuspensionStateCalculationParams getSuspensionStateCalculationParams() {
      this.checkNotNull();
      return PxVehicleSuspensionStateCalculationParams.wrapPointer(_getSuspensionStateCalculationParams(this.address));
   }

   private static native long _getSuspensionStateCalculationParams(long var0);

   public void setSuspensionStateCalculationParams(PxVehicleSuspensionStateCalculationParams value) {
      this.checkNotNull();
      _setSuspensionStateCalculationParams(this.address, value.getAddress());
   }

   private static native void _setSuspensionStateCalculationParams(long var0, long var2);

   public PxVehicleBrakeCommandResponseParams getBrakeResponseParams(int index) {
      this.checkNotNull();
      return PxVehicleBrakeCommandResponseParams.wrapPointer(_getBrakeResponseParams(this.address, index));
   }

   private static native long _getBrakeResponseParams(long var0, int var2);

   public void setBrakeResponseParams(int index, PxVehicleBrakeCommandResponseParams value) {
      this.checkNotNull();
      _setBrakeResponseParams(this.address, index, value.getAddress());
   }

   private static native void _setBrakeResponseParams(long var0, int var2, long var3);

   public PxVehicleSteerCommandResponseParams getSteerResponseParams() {
      this.checkNotNull();
      return PxVehicleSteerCommandResponseParams.wrapPointer(_getSteerResponseParams(this.address));
   }

   private static native long _getSteerResponseParams(long var0);

   public void setSteerResponseParams(PxVehicleSteerCommandResponseParams value) {
      this.checkNotNull();
      _setSteerResponseParams(this.address, value.getAddress());
   }

   private static native void _setSteerResponseParams(long var0, long var2);

   public PxVehicleAckermannParams getAckermannParams(int index) {
      this.checkNotNull();
      return PxVehicleAckermannParams.wrapPointer(_getAckermannParams(this.address, index));
   }

   private static native long _getAckermannParams(long var0, int var2);

   public void setAckermannParams(int index, PxVehicleAckermannParams value) {
      this.checkNotNull();
      _setAckermannParams(this.address, index, value.getAddress());
   }

   private static native void _setAckermannParams(long var0, int var2, long var3);

   public PxVehicleSuspensionParams getSuspensionParams(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionParams.wrapPointer(_getSuspensionParams(this.address, index));
   }

   private static native long _getSuspensionParams(long var0, int var2);

   public void setSuspensionParams(int index, PxVehicleSuspensionParams value) {
      this.checkNotNull();
      _setSuspensionParams(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionParams(long var0, int var2, long var3);

   public PxVehicleSuspensionComplianceParams getSuspensionComplianceParams(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionComplianceParams.wrapPointer(_getSuspensionComplianceParams(this.address, index));
   }

   private static native long _getSuspensionComplianceParams(long var0, int var2);

   public void setSuspensionComplianceParams(int index, PxVehicleSuspensionComplianceParams value) {
      this.checkNotNull();
      _setSuspensionComplianceParams(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionComplianceParams(long var0, int var2, long var3);

   public PxVehicleSuspensionForceParams getSuspensionForceParams(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionForceParams.wrapPointer(_getSuspensionForceParams(this.address, index));
   }

   private static native long _getSuspensionForceParams(long var0, int var2);

   public void setSuspensionForceParams(int index, PxVehicleSuspensionForceParams value) {
      this.checkNotNull();
      _setSuspensionForceParams(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionForceParams(long var0, int var2, long var3);

   public PxVehicleAntiRollForceParams getAntiRollForceParams(int index) {
      this.checkNotNull();
      return PxVehicleAntiRollForceParams.wrapPointer(_getAntiRollForceParams(this.address, index));
   }

   private static native long _getAntiRollForceParams(long var0, int var2);

   public void setAntiRollForceParams(int index, PxVehicleAntiRollForceParams value) {
      this.checkNotNull();
      _setAntiRollForceParams(this.address, index, value.getAddress());
   }

   private static native void _setAntiRollForceParams(long var0, int var2, long var3);

   public int getNbAntiRollForceParams() {
      this.checkNotNull();
      return _getNbAntiRollForceParams(this.address);
   }

   private static native int _getNbAntiRollForceParams(long var0);

   public void setNbAntiRollForceParams(int value) {
      this.checkNotNull();
      _setNbAntiRollForceParams(this.address, value);
   }

   private static native void _setNbAntiRollForceParams(long var0, int var2);

   public PxVehicleTireForceParams getTireForceParams(int index) {
      this.checkNotNull();
      return PxVehicleTireForceParams.wrapPointer(_getTireForceParams(this.address, index));
   }

   private static native long _getTireForceParams(long var0, int var2);

   public void setTireForceParams(int index, PxVehicleTireForceParams value) {
      this.checkNotNull();
      _setTireForceParams(this.address, index, value.getAddress());
   }

   private static native void _setTireForceParams(long var0, int var2, long var3);

   public PxVehicleWheelParams getWheelParams(int index) {
      this.checkNotNull();
      return PxVehicleWheelParams.wrapPointer(_getWheelParams(this.address, index));
   }

   private static native long _getWheelParams(long var0, int var2);

   public void setWheelParams(int index, PxVehicleWheelParams value) {
      this.checkNotNull();
      _setWheelParams(this.address, index, value.getAddress());
   }

   private static native void _setWheelParams(long var0, int var2, long var3);

   public PxVehicleRigidBodyParams getRigidBodyParams() {
      this.checkNotNull();
      return PxVehicleRigidBodyParams.wrapPointer(_getRigidBodyParams(this.address));
   }

   private static native long _getRigidBodyParams(long var0);

   public void setRigidBodyParams(PxVehicleRigidBodyParams value) {
      this.checkNotNull();
      _setRigidBodyParams(this.address, value.getAddress());
   }

   private static native void _setRigidBodyParams(long var0, long var2);

   public BaseVehicleParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
