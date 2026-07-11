package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;

public class PxVehicleRigidBodyState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleRigidBodyState wrapPointer(long address) {
      return address != 0L ? new PxVehicleRigidBodyState(address) : null;
   }

   public static PxVehicleRigidBodyState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleRigidBodyState(long address) {
      super(address);
   }

   public PxVehicleRigidBodyState() {
      this.address = _PxVehicleRigidBodyState();
   }

   private static native long _PxVehicleRigidBodyState();

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

   public PxTransform getPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getPose(this.address));
   }

   private static native long _getPose(long var0);

   public void setPose(PxTransform value) {
      this.checkNotNull();
      _setPose(this.address, value.getAddress());
   }

   private static native void _setPose(long var0, long var2);

   public PxVec3 getLinearVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getLinearVelocity(this.address));
   }

   private static native long _getLinearVelocity(long var0);

   public void setLinearVelocity(PxVec3 value) {
      this.checkNotNull();
      _setLinearVelocity(this.address, value.getAddress());
   }

   private static native void _setLinearVelocity(long var0, long var2);

   public PxVec3 getAngularVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getAngularVelocity(this.address));
   }

   private static native long _getAngularVelocity(long var0);

   public void setAngularVelocity(PxVec3 value) {
      this.checkNotNull();
      _setAngularVelocity(this.address, value.getAddress());
   }

   private static native void _setAngularVelocity(long var0, long var2);

   public PxVec3 getPreviousLinearVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPreviousLinearVelocity(this.address));
   }

   private static native long _getPreviousLinearVelocity(long var0);

   public void setPreviousLinearVelocity(PxVec3 value) {
      this.checkNotNull();
      _setPreviousLinearVelocity(this.address, value.getAddress());
   }

   private static native void _setPreviousLinearVelocity(long var0, long var2);

   public PxVec3 getPreviousAngularVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPreviousAngularVelocity(this.address));
   }

   private static native long _getPreviousAngularVelocity(long var0);

   public void setPreviousAngularVelocity(PxVec3 value) {
      this.checkNotNull();
      _setPreviousAngularVelocity(this.address, value.getAddress());
   }

   private static native void _setPreviousAngularVelocity(long var0, long var2);

   public PxVec3 getExternalForce() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getExternalForce(this.address));
   }

   private static native long _getExternalForce(long var0);

   public void setExternalForce(PxVec3 value) {
      this.checkNotNull();
      _setExternalForce(this.address, value.getAddress());
   }

   private static native void _setExternalForce(long var0, long var2);

   public PxVec3 getExternalTorque() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getExternalTorque(this.address));
   }

   private static native long _getExternalTorque(long var0);

   public void setExternalTorque(PxVec3 value) {
      this.checkNotNull();
      _setExternalTorque(this.address, value.getAddress());
   }

   private static native void _setExternalTorque(long var0, long var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public float getVerticalSpeed(PxVehicleFrame frame) {
      this.checkNotNull();
      return _getVerticalSpeed(this.address, frame.getAddress());
   }

   private static native float _getVerticalSpeed(long var0, long var2);

   public float getLateralSpeed(PxVehicleFrame frame) {
      this.checkNotNull();
      return _getLateralSpeed(this.address, frame.getAddress());
   }

   private static native float _getLateralSpeed(long var0, long var2);

   public float getLongitudinalSpeed(PxVehicleFrame frame) {
      this.checkNotNull();
      return _getLongitudinalSpeed(this.address, frame.getAddress());
   }

   private static native float _getLongitudinalSpeed(long var0, long var2);
}
