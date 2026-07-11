package physx.vehicle2;

public class PxVehicleTankDriveTransmissionCommandState extends PxVehicleEngineDriveTransmissionCommandState {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTankDriveTransmissionCommandState wrapPointer(long address) {
      return address != 0L ? new PxVehicleTankDriveTransmissionCommandState(address) : null;
   }

   public static PxVehicleTankDriveTransmissionCommandState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTankDriveTransmissionCommandState(long address) {
      super(address);
   }

   public PxVehicleTankDriveTransmissionCommandState() {
      this.address = _PxVehicleTankDriveTransmissionCommandState();
   }

   private static native long _PxVehicleTankDriveTransmissionCommandState();

   @Override
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

   public float getThrusts(int index) {
      this.checkNotNull();
      return _getThrusts(this.address, index);
   }

   private static native float _getThrusts(long var0, int var2);

   public void setThrusts(int index, float value) {
      this.checkNotNull();
      _setThrusts(this.address, index, value);
   }

   private static native void _setThrusts(long var0, int var2, float var3);

   @Override
   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
