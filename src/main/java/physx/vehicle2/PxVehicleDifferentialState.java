package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleDifferentialState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleDifferentialState wrapPointer(long address) {
      return address != 0L ? new PxVehicleDifferentialState(address) : null;
   }

   public static PxVehicleDifferentialState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleDifferentialState(long address) {
      super(address);
   }

   public PxVehicleDifferentialState() {
      this.address = _PxVehicleDifferentialState();
   }

   private static native long _PxVehicleDifferentialState();

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

   public int getConnectedWheels(int index) {
      this.checkNotNull();
      return _getConnectedWheels(this.address, index);
   }

   private static native int _getConnectedWheels(long var0, int var2);

   public void setConnectedWheels(int index, int value) {
      this.checkNotNull();
      _setConnectedWheels(this.address, index, value);
   }

   private static native void _setConnectedWheels(long var0, int var2, int var3);

   public int getNbConnectedWheels() {
      this.checkNotNull();
      return _getNbConnectedWheels(this.address);
   }

   private static native int _getNbConnectedWheels(long var0);

   public void setNbConnectedWheels(int value) {
      this.checkNotNull();
      _setNbConnectedWheels(this.address, value);
   }

   private static native void _setNbConnectedWheels(long var0, int var2);

   public float getTorqueRatiosAllWheels(int index) {
      this.checkNotNull();
      return _getTorqueRatiosAllWheels(this.address, index);
   }

   private static native float _getTorqueRatiosAllWheels(long var0, int var2);

   public void setTorqueRatiosAllWheels(int index, float value) {
      this.checkNotNull();
      _setTorqueRatiosAllWheels(this.address, index, value);
   }

   private static native void _setTorqueRatiosAllWheels(long var0, int var2, float var3);

   public float getAveWheelSpeedContributionAllWheels(int index) {
      this.checkNotNull();
      return _getAveWheelSpeedContributionAllWheels(this.address, index);
   }

   private static native float _getAveWheelSpeedContributionAllWheels(long var0, int var2);

   public void setAveWheelSpeedContributionAllWheels(int index, float value) {
      this.checkNotNull();
      _setAveWheelSpeedContributionAllWheels(this.address, index, value);
   }

   private static native void _setAveWheelSpeedContributionAllWheels(long var0, int var2, float var3);

   public void setToDefault() {
      this.checkNotNull();
      _setToDefault(this.address);
   }

   private static native void _setToDefault(long var0);
}
