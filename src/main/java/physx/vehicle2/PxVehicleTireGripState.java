package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireGripState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehicleTireGripState() {
   }

   private static native int __sizeOf();

   public static PxVehicleTireGripState wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireGripState(address) : null;
   }

   public static PxVehicleTireGripState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireGripState(long address) {
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

   public float getLoad() {
      this.checkNotNull();
      return _getLoad(this.address);
   }

   private static native float _getLoad(long var0);

   public void setLoad(float value) {
      this.checkNotNull();
      _setLoad(this.address, value);
   }

   private static native void _setLoad(long var0, float var2);

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

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
