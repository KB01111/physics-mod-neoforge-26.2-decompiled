package physx.vehicle2;

import physx.NativeObject;

public class BaseVehicleState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static BaseVehicleState wrapPointer(long address) {
      return address != 0L ? new BaseVehicleState(address) : null;
   }

   public static BaseVehicleState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected BaseVehicleState(long address) {
      super(address);
   }

   public BaseVehicleState() {
      this.address = _BaseVehicleState();
   }

   private static native long _BaseVehicleState();

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

   public float getBrakeCommandResponseStates(int index) {
      this.checkNotNull();
      return _getBrakeCommandResponseStates(this.address, index);
   }

   private static native float _getBrakeCommandResponseStates(long var0, int var2);

   public void setBrakeCommandResponseStates(int index, float value) {
      this.checkNotNull();
      _setBrakeCommandResponseStates(this.address, index, value);
   }

   private static native void _setBrakeCommandResponseStates(long var0, int var2, float var3);

   public float getSteerCommandResponseStates(int index) {
      this.checkNotNull();
      return _getSteerCommandResponseStates(this.address, index);
   }

   private static native float _getSteerCommandResponseStates(long var0, int var2);

   public void setSteerCommandResponseStates(int index, float value) {
      this.checkNotNull();
      _setSteerCommandResponseStates(this.address, index, value);
   }

   private static native void _setSteerCommandResponseStates(long var0, int var2, float var3);

   public PxVehicleWheelActuationState getActuationStates(int index) {
      this.checkNotNull();
      return PxVehicleWheelActuationState.wrapPointer(_getActuationStates(this.address, index));
   }

   private static native long _getActuationStates(long var0, int var2);

   public void setActuationStates(int index, PxVehicleWheelActuationState value) {
      this.checkNotNull();
      _setActuationStates(this.address, index, value.getAddress());
   }

   private static native void _setActuationStates(long var0, int var2, long var3);

   public PxVehicleRoadGeometryState getRoadGeomStates(int index) {
      this.checkNotNull();
      return PxVehicleRoadGeometryState.wrapPointer(_getRoadGeomStates(this.address, index));
   }

   private static native long _getRoadGeomStates(long var0, int var2);

   public void setRoadGeomStates(int index, PxVehicleRoadGeometryState value) {
      this.checkNotNull();
      _setRoadGeomStates(this.address, index, value.getAddress());
   }

   private static native void _setRoadGeomStates(long var0, int var2, long var3);

   public PxVehicleSuspensionState getSuspensionStates(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionState.wrapPointer(_getSuspensionStates(this.address, index));
   }

   private static native long _getSuspensionStates(long var0, int var2);

   public void setSuspensionStates(int index, PxVehicleSuspensionState value) {
      this.checkNotNull();
      _setSuspensionStates(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionStates(long var0, int var2, long var3);

   public PxVehicleSuspensionComplianceState getSuspensionComplianceStates(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionComplianceState.wrapPointer(_getSuspensionComplianceStates(this.address, index));
   }

   private static native long _getSuspensionComplianceStates(long var0, int var2);

   public void setSuspensionComplianceStates(int index, PxVehicleSuspensionComplianceState value) {
      this.checkNotNull();
      _setSuspensionComplianceStates(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionComplianceStates(long var0, int var2, long var3);

   public PxVehicleSuspensionForce getSuspensionForces(int index) {
      this.checkNotNull();
      return PxVehicleSuspensionForce.wrapPointer(_getSuspensionForces(this.address, index));
   }

   private static native long _getSuspensionForces(long var0, int var2);

   public void setSuspensionForces(int index, PxVehicleSuspensionForce value) {
      this.checkNotNull();
      _setSuspensionForces(this.address, index, value.getAddress());
   }

   private static native void _setSuspensionForces(long var0, int var2, long var3);

   public PxVehicleAntiRollTorque getAntiRollTorque() {
      this.checkNotNull();
      return PxVehicleAntiRollTorque.wrapPointer(_getAntiRollTorque(this.address));
   }

   private static native long _getAntiRollTorque(long var0);

   public void setAntiRollTorque(PxVehicleAntiRollTorque value) {
      this.checkNotNull();
      _setAntiRollTorque(this.address, value.getAddress());
   }

   private static native void _setAntiRollTorque(long var0, long var2);

   public PxVehicleTireGripState getTireGripStates(int index) {
      this.checkNotNull();
      return PxVehicleTireGripState.wrapPointer(_getTireGripStates(this.address, index));
   }

   private static native long _getTireGripStates(long var0, int var2);

   public void setTireGripStates(int index, PxVehicleTireGripState value) {
      this.checkNotNull();
      _setTireGripStates(this.address, index, value.getAddress());
   }

   private static native void _setTireGripStates(long var0, int var2, long var3);

   public PxVehicleTireDirectionState getTireDirectionStates(int index) {
      this.checkNotNull();
      return PxVehicleTireDirectionState.wrapPointer(_getTireDirectionStates(this.address, index));
   }

   private static native long _getTireDirectionStates(long var0, int var2);

   public void setTireDirectionStates(int index, PxVehicleTireDirectionState value) {
      this.checkNotNull();
      _setTireDirectionStates(this.address, index, value.getAddress());
   }

   private static native void _setTireDirectionStates(long var0, int var2, long var3);

   public PxVehicleTireSpeedState getTireSpeedStates(int index) {
      this.checkNotNull();
      return PxVehicleTireSpeedState.wrapPointer(_getTireSpeedStates(this.address, index));
   }

   private static native long _getTireSpeedStates(long var0, int var2);

   public void setTireSpeedStates(int index, PxVehicleTireSpeedState value) {
      this.checkNotNull();
      _setTireSpeedStates(this.address, index, value.getAddress());
   }

   private static native void _setTireSpeedStates(long var0, int var2, long var3);

   public PxVehicleTireSlipState getTireSlipStates(int index) {
      this.checkNotNull();
      return PxVehicleTireSlipState.wrapPointer(_getTireSlipStates(this.address, index));
   }

   private static native long _getTireSlipStates(long var0, int var2);

   public void setTireSlipStates(int index, PxVehicleTireSlipState value) {
      this.checkNotNull();
      _setTireSlipStates(this.address, index, value.getAddress());
   }

   private static native void _setTireSlipStates(long var0, int var2, long var3);

   public PxVehicleTireCamberAngleState getTireCamberAngleStates(int index) {
      this.checkNotNull();
      return PxVehicleTireCamberAngleState.wrapPointer(_getTireCamberAngleStates(this.address, index));
   }

   private static native long _getTireCamberAngleStates(long var0, int var2);

   public void setTireCamberAngleStates(int index, PxVehicleTireCamberAngleState value) {
      this.checkNotNull();
      _setTireCamberAngleStates(this.address, index, value.getAddress());
   }

   private static native void _setTireCamberAngleStates(long var0, int var2, long var3);

   public PxVehicleTireStickyState getTireStickyStates(int index) {
      this.checkNotNull();
      return PxVehicleTireStickyState.wrapPointer(_getTireStickyStates(this.address, index));
   }

   private static native long _getTireStickyStates(long var0, int var2);

   public void setTireStickyStates(int index, PxVehicleTireStickyState value) {
      this.checkNotNull();
      _setTireStickyStates(this.address, index, value.getAddress());
   }

   private static native void _setTireStickyStates(long var0, int var2, long var3);

   public PxVehicleTireForce getTireForces(int index) {
      this.checkNotNull();
      return PxVehicleTireForce.wrapPointer(_getTireForces(this.address, index));
   }

   private static native long _getTireForces(long var0, int var2);

   public void setTireForces(int index, PxVehicleTireForce value) {
      this.checkNotNull();
      _setTireForces(this.address, index, value.getAddress());
   }

   private static native void _setTireForces(long var0, int var2, long var3);

   public PxVehicleWheelRigidBody1dState getWheelRigidBody1dStates(int index) {
      this.checkNotNull();
      return PxVehicleWheelRigidBody1dState.wrapPointer(_getWheelRigidBody1dStates(this.address, index));
   }

   private static native long _getWheelRigidBody1dStates(long var0, int var2);

   public void setWheelRigidBody1dStates(int index, PxVehicleWheelRigidBody1dState value) {
      this.checkNotNull();
      _setWheelRigidBody1dStates(this.address, index, value.getAddress());
   }

   private static native void _setWheelRigidBody1dStates(long var0, int var2, long var3);

   public PxVehicleWheelLocalPose getWheelLocalPoses(int index) {
      this.checkNotNull();
      return PxVehicleWheelLocalPose.wrapPointer(_getWheelLocalPoses(this.address, index));
   }

   private static native long _getWheelLocalPoses(long var0, int var2);

   public void setWheelLocalPoses(int index, PxVehicleWheelLocalPose value) {
      this.checkNotNull();
      _setWheelLocalPoses(this.address, index, value.getAddress());
   }

   private static native void _setWheelLocalPoses(long var0, int var2, long var3);

   public PxVehicleRigidBodyState getRigidBodyState() {
      this.checkNotNull();
      return PxVehicleRigidBodyState.wrapPointer(_getRigidBodyState(this.address));
   }

   private static native long _getRigidBodyState(long var0);

   public void setRigidBodyState(PxVehicleRigidBodyState value) {
      this.checkNotNull();
      _setRigidBodyState(this.address, value.getAddress());
   }

   private static native void _setRigidBodyState(long var0, long var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
