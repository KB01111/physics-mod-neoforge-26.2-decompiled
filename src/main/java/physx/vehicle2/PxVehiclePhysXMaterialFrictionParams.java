package physx.vehicle2;

import physx.NativeObject;

public class PxVehiclePhysXMaterialFrictionParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehiclePhysXMaterialFrictionParams() {
   }

   private static native int __sizeOf();

   public static PxVehiclePhysXMaterialFrictionParams wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXMaterialFrictionParams(address) : null;
   }

   public static PxVehiclePhysXMaterialFrictionParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXMaterialFrictionParams(long address) {
      super(address);
   }

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

   public PxVehiclePhysXMaterialFriction getMaterialFrictions() {
      this.checkNotNull();
      return PxVehiclePhysXMaterialFriction.wrapPointer(_getMaterialFrictions(this.address));
   }

   private static native long _getMaterialFrictions(long var0);

   public void setMaterialFrictions(PxVehiclePhysXMaterialFriction value) {
      this.checkNotNull();
      _setMaterialFrictions(this.address, value.getAddress());
   }

   private static native void _setMaterialFrictions(long var0, long var2);

   public int getNbMaterialFrictions() {
      this.checkNotNull();
      return _getNbMaterialFrictions(this.address);
   }

   private static native int _getNbMaterialFrictions(long var0);

   public void setNbMaterialFrictions(int value) {
      this.checkNotNull();
      _setNbMaterialFrictions(this.address, value);
   }

   private static native void _setNbMaterialFrictions(long var0, int var2);

   public float getDefaultFriction() {
      this.checkNotNull();
      return _getDefaultFriction(this.address);
   }

   private static native float _getDefaultFriction(long var0);

   public void setDefaultFriction(float value) {
      this.checkNotNull();
      _setDefaultFriction(this.address, value);
   }

   private static native void _setDefaultFriction(long var0, float var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
