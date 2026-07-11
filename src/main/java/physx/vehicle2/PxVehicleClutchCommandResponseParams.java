package physx.vehicle2;

import physx.NativeObject;

public class PxVehicleClutchCommandResponseParams extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVehicleClutchCommandResponseParams wrapPointer(long address) {
      return address != 0L ? new PxVehicleClutchCommandResponseParams(address) : null;
   }

   public static PxVehicleClutchCommandResponseParams arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVehicleClutchCommandResponseParams(long address) {
      super(address);
   }

   public static PxVehicleClutchCommandResponseParams createAt(long address) {
      __placement_new_PxVehicleClutchCommandResponseParams(address);
      PxVehicleClutchCommandResponseParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVehicleClutchCommandResponseParams createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVehicleClutchCommandResponseParams(address);
      PxVehicleClutchCommandResponseParams createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVehicleClutchCommandResponseParams(long var0);

   public PxVehicleClutchCommandResponseParams() {
      this.address = _PxVehicleClutchCommandResponseParams();
   }

   private static native long _PxVehicleClutchCommandResponseParams();

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

   public float getMaxResponse() {
      this.checkNotNull();
      return _getMaxResponse(this.address);
   }

   private static native float _getMaxResponse(long var0);

   public void setMaxResponse(float value) {
      this.checkNotNull();
      _setMaxResponse(this.address, value);
   }

   private static native void _setMaxResponse(long var0, float var2);

   public PxVehicleClutchCommandResponseParams transformAndScale(
      PxVehicleFrame srcFrame, PxVehicleFrame trgFrame, PxVehicleScale srcScale, PxVehicleScale trgScale
   ) {
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
