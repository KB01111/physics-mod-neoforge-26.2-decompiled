package physx.extensions;

import physx.NativeObject;

public class PxSerializationRegistry extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSerializationRegistry() {
   }

   private static native int __sizeOf();

   public static PxSerializationRegistry wrapPointer(long address) {
      return address != 0L ? new PxSerializationRegistry(address) : null;
   }

   public static PxSerializationRegistry arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSerializationRegistry(long address) {
      super(address);
   }

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
