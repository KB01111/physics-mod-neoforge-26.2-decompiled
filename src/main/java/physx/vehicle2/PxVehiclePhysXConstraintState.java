package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehiclePhysXConstraintState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehiclePhysXConstraintState wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXConstraintState(address) : null;
   }

   public static PxVehiclePhysXConstraintState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXConstraintState(long address) {
      super(address);
   }

   public PxVehiclePhysXConstraintState() {
      this.address = _PxVehiclePhysXConstraintState();
   }

   private static native long _PxVehiclePhysXConstraintState();

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

   public boolean getTireActiveStatus(int index) {
      this.checkNotNull();
      return _getTireActiveStatus(this.address, index);
   }

   private static native boolean _getTireActiveStatus(long var0, int var2);

   public void setTireActiveStatus(int index, boolean value) {
      this.checkNotNull();
      _setTireActiveStatus(this.address, index, value);
   }

   private static native void _setTireActiveStatus(long var0, int var2, boolean var3);

   public PxVec3 getTireLinears(int index) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTireLinears(this.address, index));
   }

   private static native long _getTireLinears(long var0, int var2);

   public void setTireLinears(int index, PxVec3 value) {
      this.checkNotNull();
      _setTireLinears(this.address, index, value.getAddress());
   }

   private static native void _setTireLinears(long var0, int var2, long var3);

   public PxVec3 getTireAngulars(int index) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTireAngulars(this.address, index));
   }

   private static native long _getTireAngulars(long var0, int var2);

   public void setTireAngulars(int index, PxVec3 value) {
      this.checkNotNull();
      _setTireAngulars(this.address, index, value.getAddress());
   }

   private static native void _setTireAngulars(long var0, int var2, long var3);

   public float getTireDamping(int index) {
      this.checkNotNull();
      return _getTireDamping(this.address, index);
   }

   private static native float _getTireDamping(long var0, int var2);

   public void setTireDamping(int index, float value) {
      this.checkNotNull();
      _setTireDamping(this.address, index, value);
   }

   private static native void _setTireDamping(long var0, int var2, float var3);

   public boolean getSuspActiveStatus() {
      this.checkNotNull();
      return _getSuspActiveStatus(this.address);
   }

   private static native boolean _getSuspActiveStatus(long var0);

   public void setSuspActiveStatus(boolean value) {
      this.checkNotNull();
      _setSuspActiveStatus(this.address, value);
   }

   private static native void _setSuspActiveStatus(long var0, boolean var2);

   public PxVec3 getSuspLinear() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getSuspLinear(this.address));
   }

   private static native long _getSuspLinear(long var0);

   public void setSuspLinear(PxVec3 value) {
      this.checkNotNull();
      _setSuspLinear(this.address, value.getAddress());
   }

   private static native void _setSuspLinear(long var0, long var2);

   public PxVec3 getSuspAngular() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getSuspAngular(this.address));
   }

   private static native long _getSuspAngular(long var0);

   public void setSuspAngular(PxVec3 value) {
      this.checkNotNull();
      _setSuspAngular(this.address, value.getAddress());
   }

   private static native void _setSuspAngular(long var0, long var2);

   public float getSuspGeometricError() {
      this.checkNotNull();
      return _getSuspGeometricError(this.address);
   }

   private static native float _getSuspGeometricError(long var0);

   public void setSuspGeometricError(float value) {
      this.checkNotNull();
      _setSuspGeometricError(this.address, value);
   }

   private static native void _setSuspGeometricError(long var0, float var2);

   public float getRestitution() {
      this.checkNotNull();
      return _getRestitution(this.address);
   }

   private static native float _getRestitution(long var0);

   public void setRestitution(float value) {
      this.checkNotNull();
      _setRestitution(this.address, value);
   }

   private static native void _setRestitution(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
