package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleTorqueCurveLookupTable extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleTorqueCurveLookupTable wrapPointer(long address) {
      return address != 0L ? new PxVehicleTorqueCurveLookupTable(address) : null;
   }

   public static PxVehicleTorqueCurveLookupTable arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleTorqueCurveLookupTable(long address) {
      super(address);
   }

   public PxVehicleTorqueCurveLookupTable() {
      this.address = _PxVehicleTorqueCurveLookupTable();
   }

   private static native long _PxVehicleTorqueCurveLookupTable();

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

   public boolean addPair(float x, float y) {
      this.checkNotNull();
      return _addPair(this.address, x, y);
   }

   private static native boolean _addPair(long var0, float var2, float var3);

   public float interpolate(float x) {
      this.checkNotNull();
      return _interpolate(this.address, x);
   }

   private static native float _interpolate(long var0, float var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
