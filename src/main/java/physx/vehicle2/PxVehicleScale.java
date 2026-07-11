package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleScale extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleScale wrapPointer(long address) {
      return address != 0L ? new PxVehicleScale(address) : null;
   }

   public static PxVehicleScale arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleScale(long address) {
      super(address);
   }

   public PxVehicleScale() {
      this.address = _PxVehicleScale();
   }

   private static native long _PxVehicleScale();

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

   public float getScale() {
      this.checkNotNull();
      return _getScale(this.address);
   }

   private static native float _getScale(long var0);

   public void setScale(float value) {
      this.checkNotNull();
      _setScale(this.address, value);
   }

   private static native void _setScale(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
