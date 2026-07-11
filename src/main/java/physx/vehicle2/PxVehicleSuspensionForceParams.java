package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleSuspensionForceParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleSuspensionForceParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleSuspensionForceParams(address) : null;
   }

   public static PxVehicleSuspensionForceParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleSuspensionForceParams(long address) {
      super(address);
   }

   public PxVehicleSuspensionForceParams() {
      this.address = _PxVehicleSuspensionForceParams();
   }

   private static native long _PxVehicleSuspensionForceParams();

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

   public float getStiffness() {
      this.checkNotNull();
      return _getStiffness(this.address);
   }

   private static native float _getStiffness(long var0);

   public void setStiffness(float value) {
      this.checkNotNull();
      _setStiffness(this.address, value);
   }

   private static native void _setStiffness(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);

   public float getSprungMass() {
      this.checkNotNull();
      return _getSprungMass(this.address);
   }

   private static native float _getSprungMass(long var0);

   public void setSprungMass(float value) {
      this.checkNotNull();
      _setSprungMass(this.address, value);
   }

   private static native void _setSprungMass(long var0, float var2);

   public PxVehicleSuspensionForceParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
