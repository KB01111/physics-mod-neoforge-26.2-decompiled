package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleEngineParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleEngineParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleEngineParams(address) : null;
   }

   public static PxVehicleEngineParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleEngineParams(long address) {
      super(address);
   }

   public static PxVehicleEngineParams createAt(long address) {
      __placement_new_PxVehicleEngineParams(address);
      PxVehicleEngineParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleEngineParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleEngineParams(address);
      PxVehicleEngineParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleEngineParams(long var0);

   public PxVehicleEngineParams() {
      this.address = _PxVehicleEngineParams();
   }

   private static native long _PxVehicleEngineParams();

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

   public PxVehicleTorqueCurveLookupTable getTorqueCurve() {
      this.checkNotNull();
      return PxVehicleTorqueCurveLookupTable.wrapPointer(_getTorqueCurve(this.address));
   }

   private static native long _getTorqueCurve(long var0);

   public void setTorqueCurve(PxVehicleTorqueCurveLookupTable value) {
      this.checkNotNull();
      _setTorqueCurve(this.address, value.getAddress());
   }

   private static native void _setTorqueCurve(long var0, long var2);

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

   public float getPeakTorque() {
      this.checkNotNull();
      return _getPeakTorque(this.address);
   }

   private static native float _getPeakTorque(long var0);

   public void setPeakTorque(float value) {
      this.checkNotNull();
      _setPeakTorque(this.address, value);
   }

   private static native void _setPeakTorque(long var0, float var2);

   public float getIdleOmega() {
      this.checkNotNull();
      return _getIdleOmega(this.address);
   }

   private static native float _getIdleOmega(long var0);

   public void setIdleOmega(float value) {
      this.checkNotNull();
      _setIdleOmega(this.address, value);
   }

   private static native void _setIdleOmega(long var0, float var2);

   public float getMaxOmega() {
      this.checkNotNull();
      return _getMaxOmega(this.address);
   }

   private static native float _getMaxOmega(long var0);

   public void setMaxOmega(float value) {
      this.checkNotNull();
      _setMaxOmega(this.address, value);
   }

   private static native void _setMaxOmega(long var0, float var2);

   public float getDampingRateFullThrottle() {
      this.checkNotNull();
      return _getDampingRateFullThrottle(this.address);
   }

   private static native float _getDampingRateFullThrottle(long var0);

   public void setDampingRateFullThrottle(float value) {
      this.checkNotNull();
      _setDampingRateFullThrottle(this.address, value);
   }

   private static native void _setDampingRateFullThrottle(long var0, float var2);

   public float getDampingRateZeroThrottleClutchEngaged() {
      this.checkNotNull();
      return _getDampingRateZeroThrottleClutchEngaged(this.address);
   }

   private static native float _getDampingRateZeroThrottleClutchEngaged(long var0);

   public void setDampingRateZeroThrottleClutchEngaged(float value) {
      this.checkNotNull();
      _setDampingRateZeroThrottleClutchEngaged(this.address, value);
   }

   private static native void _setDampingRateZeroThrottleClutchEngaged(long var0, float var2);

   public float getDampingRateZeroThrottleClutchDisengaged() {
      this.checkNotNull();
      return _getDampingRateZeroThrottleClutchDisengaged(this.address);
   }

   private static native float _getDampingRateZeroThrottleClutchDisengaged(long var0);

   public void setDampingRateZeroThrottleClutchDisengaged(float value) {
      this.checkNotNull();
      _setDampingRateZeroThrottleClutchDisengaged(this.address, value);
   }

   private static native void _setDampingRateZeroThrottleClutchDisengaged(long var0, float var2);

   public PxVehicleEngineParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
