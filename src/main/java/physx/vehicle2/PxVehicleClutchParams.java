package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleClutchParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleClutchParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleClutchParams(address) : null;
   }

   public static PxVehicleClutchParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleClutchParams(long address) {
      super(address);
   }

   public static PxVehicleClutchParams createAt(long address) {
      __placement_new_PxVehicleClutchParams(address);
      PxVehicleClutchParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleClutchParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleClutchParams(address);
      PxVehicleClutchParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleClutchParams(long var0);

   public PxVehicleClutchParams() {
      this.address = _PxVehicleClutchParams();
   }

   private static native long _PxVehicleClutchParams();

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

   public PxVehicleClutchAccuracyModeEnum getAccuracyMode() {
      this.checkNotNull();
      return PxVehicleClutchAccuracyModeEnum.forValue(_getAccuracyMode(this.address));
   }

   private static native int _getAccuracyMode(long var0);

   public void setAccuracyMode(PxVehicleClutchAccuracyModeEnum value) {
      this.checkNotNull();
      _setAccuracyMode(this.address, value.value);
   }

   private static native void _setAccuracyMode(long var0, int var2);

   public int getEstimateIterations() {
      this.checkNotNull();
      return _getEstimateIterations(this.address);
   }

   private static native int _getEstimateIterations(long var0);

   public void setEstimateIterations(int value) {
      this.checkNotNull();
      _setEstimateIterations(this.address, value);
   }

   private static native void _setEstimateIterations(long var0, int var2);

   public PxVehicleClutchParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
