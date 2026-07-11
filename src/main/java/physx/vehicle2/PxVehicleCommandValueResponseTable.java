package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleCommandValueResponseTable extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleCommandValueResponseTable wrapPointer(long address) {
      return address != 0L ? new PxVehicleCommandValueResponseTable(address) : null;
   }

   public static PxVehicleCommandValueResponseTable arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleCommandValueResponseTable(long address) {
      super(address);
   }

   public PxVehicleCommandValueResponseTable() {
      this.address = _PxVehicleCommandValueResponseTable();
   }

   private static native long _PxVehicleCommandValueResponseTable();

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

   public float getCommandValue() {
      this.checkNotNull();
      return _getCommandValue(this.address);
   }

   private static native float _getCommandValue(long var0);

   public void setCommandValue(float value) {
      this.checkNotNull();
      _setCommandValue(this.address, value);
   }

   private static native void _setCommandValue(long var0, float var2);
}
