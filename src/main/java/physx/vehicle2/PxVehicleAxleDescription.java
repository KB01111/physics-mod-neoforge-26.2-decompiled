package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleAxleDescription extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleAxleDescription wrapPointer(long address) {
      return address != 0L ? new PxVehicleAxleDescription(address) : null;
   }

   public static PxVehicleAxleDescription arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleAxleDescription(long address) {
      super(address);
   }

   public PxVehicleAxleDescription() {
      this.address = _PxVehicleAxleDescription();
   }

   private static native long _PxVehicleAxleDescription();

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

   public int getNbAxles() {
      this.checkNotNull();
      return _getNbAxles(this.address);
   }

   private static native int _getNbAxles(long var0);

   public void setNbAxles(int value) {
      this.checkNotNull();
      _setNbAxles(this.address, value);
   }

   private static native void _setNbAxles(long var0, int var2);

   public int getNbWheelsPerAxle(int index) {
      this.checkNotNull();
      return _getNbWheelsPerAxle(this.address, index);
   }

   private static native int _getNbWheelsPerAxle(long var0, int var2);

   public void setNbWheelsPerAxle(int index, int value) {
      this.checkNotNull();
      _setNbWheelsPerAxle(this.address, index, value);
   }

   private static native void _setNbWheelsPerAxle(long var0, int var2, int var3);

   public int getAxleToWheelIds(int index) {
      this.checkNotNull();
      return _getAxleToWheelIds(this.address, index);
   }

   private static native int _getAxleToWheelIds(long var0, int var2);

   public void setAxleToWheelIds(int index, int value) {
      this.checkNotNull();
      _setAxleToWheelIds(this.address, index, value);
   }

   private static native void _setAxleToWheelIds(long var0, int var2, int var3);

   public int getWheelIdsInAxleOrder(int index) {
      this.checkNotNull();
      return _getWheelIdsInAxleOrder(this.address, index);
   }

   private static native int _getWheelIdsInAxleOrder(long var0, int var2);

   public void setWheelIdsInAxleOrder(int index, int value) {
      this.checkNotNull();
      _setWheelIdsInAxleOrder(this.address, index, value);
   }

   private static native void _setWheelIdsInAxleOrder(long var0, int var2, int var3);

   public int getNbWheels() {
      this.checkNotNull();
      return _getNbWheels(this.address);
   }

   private static native int _getNbWheels(long var0);

   public void setNbWheels(int value) {
      this.checkNotNull();
      _setNbWheels(this.address, value);
   }

   private static native void _setNbWheels(long var0, int var2);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);

   public int getNbWheelsOnAxle(int i) {
      this.checkNotNull();
      return _getNbWheelsOnAxle(this.address, i);
   }

   private static native int _getNbWheelsOnAxle(long var0, int var2);

   public int getWheelOnAxle(int j, int i) {
      this.checkNotNull();
      return _getWheelOnAxle(this.address, j, i);
   }

   private static native int _getWheelOnAxle(long var0, int var2, int var3);

   public int getAxle(int wheelId) {
      this.checkNotNull();
      return _getAxle(this.address, wheelId);
   }

   private static native int _getAxle(long var0, int var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
