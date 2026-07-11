package physx.physics;

import physx.NativeObject;

public class PxOverlapCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxOverlapCallback() {
   }

   private static native int __sizeOf();

   public static PxOverlapCallback wrapPointer(long address) {
      return address != 0L ? new PxOverlapCallback(address) : null;
   }

   public static PxOverlapCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxOverlapCallback(long address) {
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

   public boolean hasAnyHits() {
      this.checkNotNull();
      return _hasAnyHits(this.address);
   }

   private static native boolean _hasAnyHits(long var0);
}
