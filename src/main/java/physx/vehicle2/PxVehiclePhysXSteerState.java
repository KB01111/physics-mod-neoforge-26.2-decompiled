package physx.vehicle2;

import physx.NativeObject;

public class PxVehiclePhysXSteerState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxVehiclePhysXSteerState() {
   }

   private static native int __sizeOf();

   public static PxVehiclePhysXSteerState wrapPointer(long address) {
      return address != 0L ? new PxVehiclePhysXSteerState(address) : null;
   }

   public static PxVehiclePhysXSteerState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehiclePhysXSteerState(long address) {
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

   public float getPreviousSteerCommand() {
      this.checkNotNull();
      return _getPreviousSteerCommand(this.address);
   }

   private static native float _getPreviousSteerCommand(long var0);

   public void setPreviousSteerCommand(float value) {
      this.checkNotNull();
      _setPreviousSteerCommand(this.address, value);
   }

   private static native void _setPreviousSteerCommand(long var0, float var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
