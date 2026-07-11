package physx.physics;

import physx.NativeObject;

public class PxArticulationTendonLimit extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationTendonLimit() {
   }

   private static native int __sizeOf();

   public static PxArticulationTendonLimit wrapPointer(long address) {
      return address != 0L ? new PxArticulationTendonLimit(address) : null;
   }

   public static PxArticulationTendonLimit arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationTendonLimit(long address) {
      super(address);
   }

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

   public float getLowLimit() {
      this.checkNotNull();
      return _getLowLimit(this.address);
   }

   private static native float _getLowLimit(long var0);

   public void setLowLimit(float value) {
      this.checkNotNull();
      _setLowLimit(this.address, value);
   }

   private static native void _setLowLimit(long var0, float var2);

   public float getHighLimit() {
      this.checkNotNull();
      return _getHighLimit(this.address);
   }

   private static native float _getHighLimit(long var0);

   public void setHighLimit(float value) {
      this.checkNotNull();
      _setHighLimit(this.address, value);
   }

   private static native void _setHighLimit(long var0, float var2);
}
