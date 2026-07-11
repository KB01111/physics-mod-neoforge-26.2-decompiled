package physx.common;

public class PxErrorCallbackImpl extends PxErrorCallback {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxErrorCallbackImpl wrapPointer(long address) {
      return address != 0L ? new PxErrorCallbackImpl(address) : null;
   }

   public static PxErrorCallbackImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxErrorCallbackImpl(long address) {
      super(address);
   }

   protected PxErrorCallbackImpl() {
      this.address = this._PxErrorCallbackImpl();
   }

   private native long _PxErrorCallbackImpl();

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

   private void _reportError(int code, String message, String file, int line) {
      this.reportError(PxErrorCodeEnum.forValue(code), message, file, line);
   }

   @Override
   public void reportError(PxErrorCodeEnum code, String message, String file, int line) {
   }
}
