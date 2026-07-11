package physx.vehicle2;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxVehicleFixedSizeLookupTableVec3_3 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleFixedSizeLookupTableVec3_3 wrapPointer(long address) {
      return address != 0L ? new PxVehicleFixedSizeLookupTableVec3_3(address) : null;
   }

   public static PxVehicleFixedSizeLookupTableVec3_3 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleFixedSizeLookupTableVec3_3(long address) {
      super(address);
   }

   public PxVehicleFixedSizeLookupTableVec3_3() {
      this.address = _PxVehicleFixedSizeLookupTableVec3_3();
   }

   private static native long _PxVehicleFixedSizeLookupTableVec3_3();

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

   public boolean addPair(float x, PxVec3 y) {
      this.checkNotNull();
      return _addPair(this.address, x, y.getAddress());
   }

   private static native boolean _addPair(long var0, float var2, long var3);

   public PxVec3 interpolate(float x) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_interpolate(this.address, x));
   }

   private static native long _interpolate(long var0, float var2);

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
