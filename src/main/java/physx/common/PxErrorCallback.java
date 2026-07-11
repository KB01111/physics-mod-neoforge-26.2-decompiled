package physx.common;

import physx.NativeObject;

public class PxErrorCallback extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxErrorCallback() {
   }

   private static native int __sizeOf();

   public static PxErrorCallback wrapPointer(long address) {
      return address != 0L ? new PxErrorCallback(address) : null;
   }

   public static PxErrorCallback arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxErrorCallback(long address) {
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

   public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
      this.checkNotNull();
      _reportError(this.address, code.value, message, file, line);
   }

   private static native void _reportError(long var0, int var2, String var3, String var4, int var5);
}
