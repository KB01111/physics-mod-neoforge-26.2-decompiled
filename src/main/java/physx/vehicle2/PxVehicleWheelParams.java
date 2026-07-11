package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleWheelParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleWheelParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleWheelParams(address) : null;
   }

   public static PxVehicleWheelParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleWheelParams(long address) {
      super(address);
   }

   public PxVehicleWheelParams() {
      this.address = _PxVehicleWheelParams();
   }

   private static native long _PxVehicleWheelParams();

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

   public float getRadius() {
      this.checkNotNull();
      return _getRadius(this.address);
   }

   private static native float _getRadius(long var0);

   public void setRadius(float value) {
      this.checkNotNull();
      _setRadius(this.address, value);
   }

   private static native void _setRadius(long var0, float var2);

   public float getHalfWidth() {
      this.checkNotNull();
      return _getHalfWidth(this.address);
   }

   private static native float _getHalfWidth(long var0);

   public void setHalfWidth(float value) {
      this.checkNotNull();
      _setHalfWidth(this.address, value);
   }

   private static native void _setHalfWidth(long var0, float var2);

   public float getMass() {
      this.checkNotNull();
      return _getMass(this.address);
   }

   private static native float _getMass(long var0);

   public void setMass(float value) {
      this.checkNotNull();
      _setMass(this.address, value);
   }

   private static native void _setMass(long var0, float var2);

   public float getMoi() {
      this.checkNotNull();
      return _getMoi(this.address);
   }

   private static native float _getMoi(long var0);

   public void setMoi(float value) {
      this.checkNotNull();
      _setMoi(this.address, value);
   }

   private static native void _setMoi(long var0, float var2);

   public float getDampingRate() {
      this.checkNotNull();
      return _getDampingRate(this.address);
   }

   private static native float _getDampingRate(long var0);

   public void setDampingRate(float value) {
      this.checkNotNull();
      _setDampingRate(this.address, value);
   }

   private static native void _setDampingRate(long var0, float var2);

   public PxVehicleWheelParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
      this.checkNotNull();
      return wrapPointer(_transformAndScale(this.address, srcFrame.getAddress(), trgFrame.getAddress(), srcScale.getAddress(), trgScale.getAddress()));
   }

   private static native long _transformAndScale(long var0, long var2, long var4, long var6, long var8);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
