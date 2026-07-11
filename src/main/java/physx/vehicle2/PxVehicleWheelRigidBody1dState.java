package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleWheelRigidBody1dState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleWheelRigidBody1dState wrapPointer(long address) {
      return address != 0L ? new PxVehicleWheelRigidBody1dState(address) : null;
   }

   public static PxVehicleWheelRigidBody1dState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleWheelRigidBody1dState(long address) {
      super(address);
   }

   public PxVehicleWheelRigidBody1dState() {
      this.address = _PxVehicleWheelRigidBody1dState();
   }

   private static native long _PxVehicleWheelRigidBody1dState();

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

   public float getRotationSpeed() {
      this.checkNotNull();
      return _getRotationSpeed(this.address);
   }

   private static native float _getRotationSpeed(long var0);

   public void setRotationSpeed(float value) {
      this.checkNotNull();
      _setRotationSpeed(this.address, value);
   }

   private static native void _setRotationSpeed(long var0, float var2);

   public float getCorrectedRotationSpeed() {
      this.checkNotNull();
      return _getCorrectedRotationSpeed(this.address);
   }

   private static native float _getCorrectedRotationSpeed(long var0);

   public void setCorrectedRotationSpeed(float value) {
      this.checkNotNull();
      _setCorrectedRotationSpeed(this.address, value);
   }

   private static native void _setCorrectedRotationSpeed(long var0, float var2);

   public float getRotationAngle() {
      this.checkNotNull();
      return _getRotationAngle(this.address);
   }

   private static native float _getRotationAngle(long var0);

   public void setRotationAngle(float value) {
      this.checkNotNull();
      _setRotationAngle(this.address, value);
   }

   private static native void _setRotationAngle(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
