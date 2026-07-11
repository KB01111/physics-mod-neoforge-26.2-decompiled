package physx.character;

import physx.NativeObject;

public class PxControllerFilterCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxControllerFilterCallback() {
   }

   private static native int __sizeOf();

   public static PxControllerFilterCallback wrapPointer(long address) {
      return address != 0L ? new PxControllerFilterCallback(address) : null;
   }

   public static PxControllerFilterCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerFilterCallback(long address) {
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

   public boolean filter(PxController a, PxController b) {
      this.checkNotNull();
      return _filter(this.address, a.getAddress(), b.getAddress());
   }

   private static native boolean _filter(long var0, long var2, long var4);
}
