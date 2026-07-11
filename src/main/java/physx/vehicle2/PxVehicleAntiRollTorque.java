package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleAntiRollTorque extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleAntiRollTorque wrapPointer(long address) {
      return address != 0L ? new PxVehicleAntiRollTorque(address) : null;
   }

   public static PxVehicleAntiRollTorque arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleAntiRollTorque(long address) {
      super(address);
   }

   public PxVehicleAntiRollTorque() {
      this.address = _PxVehicleAntiRollTorque();
   }

   private static native long _PxVehicleAntiRollTorque();

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

   public PxVec3 getAntiRollTorque() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getAntiRollTorque(this.address));
   }

   private static native long _getAntiRollTorque(long var0);

   public void setAntiRollTorque(PxVec3 value) {
      this.checkNotNull();
      _setAntiRollTorque(this.address, value.getAddress());
   }

   private static native void _setAntiRollTorque(long var0, long var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
