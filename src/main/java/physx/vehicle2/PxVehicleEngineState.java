package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleEngineState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleEngineState wrapPointer(long address) {
      return address != 0L ? new PxVehicleEngineState(address) : null;
   }

   public static PxVehicleEngineState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleEngineState(long address) {
      super(address);
   }

   public PxVehicleEngineState() {
      this.address = _PxVehicleEngineState();
   }

   private static native long _PxVehicleEngineState();

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

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
