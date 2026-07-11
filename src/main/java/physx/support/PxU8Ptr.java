package physx.support;

public class PxU8Ptr extends PxU8ConstPtr {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxU8Ptr() {
   }

   private static native int __sizeOf();

   public static PxU8Ptr wrapPointer(long address) {
      return address != 0L ? new PxU8Ptr(address) : null;
   }

   public static PxU8Ptr arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxU8Ptr(long address) {
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
