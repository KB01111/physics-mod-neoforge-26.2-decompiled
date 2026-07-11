package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleTireForce extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireForce wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireForce(address) : null;
   }

   public static PxVehicleTireForce arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireForce(long address) {
      super(address);
   }

   public PxVehicleTireForce() {
      this.address = _PxVehicleTireForce();
   }

   private static native long _PxVehicleTireForce();

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

   public PxVec3 getForces(int index) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getForces(this.address, index));
   }

   private static native long _getForces(long var0, int var2);

   public void setForces(int index, PxVec3 value) {
      this.checkNotNull();
      _setForces(this.address, index, value.getAddress());
   }

   private static native void _setForces(long var0, int var2, long var3);

   public PxVec3 getTorques(int index) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTorques(this.address, index));
   }

   private static native long _getTorques(long var0, int var2);

   public void setTorques(int index, PxVec3 value) {
      this.checkNotNull();
      _setTorques(this.address, index, value.getAddress());
   }

   private static native void _setTorques(long var0, int var2, long var3);

   public float getAligningMoment() {
      this.checkNotNull();
      return _getAligningMoment(this.address);
   }

   private static native float _getAligningMoment(long var0);

   public void setAligningMoment(float value) {
      this.checkNotNull();
      _setAligningMoment(this.address, value);
   }

   private static native void _setAligningMoment(long var0, float var2);

   public float getWheelTorque() {
      this.checkNotNull();
      return _getWheelTorque(this.address);
   }

   private static native float _getWheelTorque(long var0);

   public void setWheelTorque(float value) {
      this.checkNotNull();
      _setWheelTorque(this.address, value);
   }

   private static native void _setWheelTorque(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
