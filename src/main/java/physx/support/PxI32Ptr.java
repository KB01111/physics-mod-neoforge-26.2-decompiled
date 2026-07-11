package physx.support;

public class PxI32Ptr extends PxI32ConstPtr {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxI32Ptr() {
   }

   private static native int __sizeOf();

   public static PxI32Ptr wrapPointer(long address) {
      return address != 0L ? new PxI32Ptr(address) : null;
   }

   public static PxI32Ptr arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxI32Ptr(long address) {
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
}
