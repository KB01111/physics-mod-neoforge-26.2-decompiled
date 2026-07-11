package physx.support;

import physx.NativeObject;

public class PxPvd extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPvd() {
   }

   private static native int __sizeOf();

   public static PxPvd wrapPointer(long address) {
      return address != 0L ? new PxPvd(address) : null;
   }

   public static PxPvd arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPvd(long address) {
      super(address);
   }

   public boolean connect(PxPvdTransport transport, PxPvdInstrumentationFlags flags) {
      this.checkNotNull();
      return _connect(this.address, transport.getAddress(), flags.getAddress());
   }

   private static native boolean _connect(long var0, long var2, long var4);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
