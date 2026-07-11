package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTireAxisStickyParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTireAxisStickyParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleTireAxisStickyParams(address) : null;
   }

   public static PxVehicleTireAxisStickyParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTireAxisStickyParams(long address) {
      super(address);
   }

   public static PxVehicleTireAxisStickyParams createAt(long address) {
      __placement_new_PxVehicleTireAxisStickyParams(address);
      PxVehicleTireAxisStickyParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleTireAxisStickyParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleTireAxisStickyParams(address);
      PxVehicleTireAxisStickyParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleTireAxisStickyParams(long var0);

   public PxVehicleTireAxisStickyParams() {
      this.address = _PxVehicleTireAxisStickyParams();
   }

   private static native long _PxVehicleTireAxisStickyParams();

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

   public float getThresholdSpeed() {
      this.checkNotNull();
      return _getThresholdSpeed(this.address);
   }

   private static native float _getThresholdSpeed(long var0);

   public void setThresholdSpeed(float value) {
      this.checkNotNull();
      _setThresholdSpeed(this.address, value);
   }

   private static native void _setThresholdSpeed(long var0, float var2);

   public float getThresholdTime() {
      this.checkNotNull();
      return _getThresholdTime(this.address);
   }

   private static native float _getThresholdTime(long var0);

   public void setThresholdTime(float value) {
      this.checkNotNull();
      _setThresholdTime(this.address, value);
   }

   private static native void _setThresholdTime(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);

   public PxVehicleTireAxisStickyParams transformAndScale(PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale) {
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
