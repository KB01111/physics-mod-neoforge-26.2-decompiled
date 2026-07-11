package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleDirectDriveTransmissionCommandState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleDirectDriveTransmissionCommandState wrapPointer(long address) {
      return address != 0L ? new PxVehicleDirectDriveTransmissionCommandState(address) : null;
   }

   public static PxVehicleDirectDriveTransmissionCommandState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleDirectDriveTransmissionCommandState(long address) {
      super(address);
   }

   public PxVehicleDirectDriveTransmissionCommandState() {
      this.address = _PxVehicleDirectDriveTransmissionCommandState();
   }

   private static native long _PxVehicleDirectDriveTransmissionCommandState();

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

   public PxVehicleDirectDriveTransmissionCommandStateEnum getGear() {
      this.checkNotNull();
      return PxVehicleDirectDriveTransmissionCommandStateEnum.forValue(_getGear(this.address));
   }

   private static native int _getGear(long var0);

   public void setGear(PxVehicleDirectDriveTransmissionCommandStateEnum value) {
      this.checkNotNull();
      _setGear(this.address, value.value);
   }

   private static native void _setGear(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
