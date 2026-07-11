package physx.vehicle2;

import physx.NativeObject;
import physx.physics.PxMaterial;

public class PxVehiclePhysXMaterialFriction extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehiclePhysXMaterialFriction wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXMaterialFriction(address) : null;
   }

   public static PxVehiclePhysXMaterialFriction arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXMaterialFriction(long address) {
      super(address);
   }

   public PxVehiclePhysXMaterialFriction() {
      this.address = _PxVehiclePhysXMaterialFriction();
   }

   private static native long _PxVehiclePhysXMaterialFriction();

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

   public PxMaterial getMaterial() {
      this.checkNotNull();
      return PxMaterial.wrapPointer(_getMaterial(this.address));
   }

   private static native long _getMaterial(long var0);

   public void setMaterial(PxMaterial value) {
      this.checkNotNull();
      _setMaterial(this.address, value.getAddress());
   }

   private static native void _setMaterial(long var0, long var2);

   public float getFriction() {
      this.checkNotNull();
      return _getFriction(this.address);
   }

   private static native float _getFriction(long var0);

   public void setFriction(float value) {
      this.checkNotNull();
      _setFriction(this.address, value);
   }

   private static native void _setFriction(long var0, float var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
