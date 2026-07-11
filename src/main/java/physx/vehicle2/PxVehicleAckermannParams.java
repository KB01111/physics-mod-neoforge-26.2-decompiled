package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleAckermannParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleAckermannParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleAckermannParams(address) : null;
   }

   public static PxVehicleAckermannParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleAckermannParams(long address) {
      super(address);
   }

   public PxVehicleAckermannParams() {
      this.address = _PxVehicleAckermannParams();
   }

   private static native long _PxVehicleAckermannParams();

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

   public int getWheelIds(int index) {
      this.checkNotNull();
      return _getWheelIds(this.address, index);
   }

   private static native int _getWheelIds(long var0, int var2);

   public void setWheelIds(int index, int value) {
      this.checkNotNull();
      _setWheelIds(this.address, index, value);
   }

   private static native void _setWheelIds(long var0, int var2, int var3);

   public float getWheelBase() {
      this.checkNotNull();
      return _getWheelBase(this.address);
   }

   private static native float _getWheelBase(long var0);

   public void setWheelBase(float value) {
      this.checkNotNull();
      _setWheelBase(this.address, value);
   }

   private static native void _setWheelBase(long var0, float var2);

   public float getTrackWidth() {
      this.checkNotNull();
      return _getTrackWidth(this.address);
   }

   private static native float _getTrackWidth(long var0);

   public void setTrackWidth(float value) {
      this.checkNotNull();
      _setTrackWidth(this.address, value);
   }

   private static native void _setTrackWidth(long var0, float var2);

   public float getStrength() {
      this.checkNotNull();
      return _getStrength(this.address);
   }

   private static native float _getStrength(long var0);

   public void setStrength(float value) {
      this.checkNotNull();
      _setStrength(this.address, value);
   }

   private static native void _setStrength(long var0, float var2);

   public boolean isValid(PxVehicleAxleDescription axleDesc) {
      this.checkNotNull();
      return _isValid(this.address, axleDesc.getAddress());
   }

   private static native boolean _isValid(long var0, long var2);

   public PxVehicleAckermannParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);
}
