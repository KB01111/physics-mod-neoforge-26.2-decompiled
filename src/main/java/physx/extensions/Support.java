package physx.extensions;

import physx.NativeObject;
import physx.common.PxVec3;

public class Support extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected Support() {
   }

   private static native int __sizeOf();

   public static Support wrapPointer(long address) {
      return address != 0L ? new Support(address) : null;
   }

   public static Support arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected Support(long address) {
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

   public float getMargin() {
      this.checkNotNull();
      return _getMargin(this.address);
   }

   private static native float _getMargin(long var0);

   public PxVec3 supportLocal(PxVec3 dir) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_supportLocal(this.address, dir.getAddress()));
   }

   private static native long _supportLocal(long var0, long var2);
}
