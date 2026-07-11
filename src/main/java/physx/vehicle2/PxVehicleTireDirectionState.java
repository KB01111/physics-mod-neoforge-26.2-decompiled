package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleTireDirectionState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireDirectionState wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireDirectionState(address) : null;
   }

   public static PxVehicleTireDirectionState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireDirectionState(long address) {
      super(address);
   }

   public PxVehicleTireDirectionState() {
      this.address = _PxVehicleTireDirectionState();
   }

   private static native long _PxVehicleTireDirectionState();

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

   public PxVec3 getDirections(int index) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getDirections(this.address, index));
   }

   private static native long _getDirections(long var0, int var2);

   public void setDirections(int index, PxVec3 value) {
      this.checkNotNull();
      _setDirections(this.address, index, value.getAddress());
   }

   private static native void _setDirections(long var0, int var2, long var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
