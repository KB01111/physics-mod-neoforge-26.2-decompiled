package physx.extensions;

import physx.common.PxVec3;

public class CustomSupport extends Support {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected CustomSupport() {
   }

   private static native int __sizeOf();

   public static CustomSupport wrapPointer(long address) {
      return address != 0L ? new CustomSupport(address) : null;
   }

   public static CustomSupport arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected CustomSupport(long address) {
      super(address);
   }

   @Override
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

   public float getCustomMargin() {
      this.checkNotNull();
      return _getCustomMargin(this.address);
   }

   private static native float _getCustomMargin(long var0);

   public void getCustomSupportLocal(PxVec3 dir, PxVec3 result) {
      this.checkNotNull();
      _getCustomSupportLocal(this.address, dir.getAddress(), result.getAddress());
   }

   private static native void _getCustomSupportLocal(long var0, long var2, long var4);
}
