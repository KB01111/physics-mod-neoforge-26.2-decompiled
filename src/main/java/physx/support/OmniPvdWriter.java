package physx.support;

import physx.NativeObject;
import physx.PlatformChecks;

public class OmniPvdWriter extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected OmniPvdWriter() {
   }

   private static native int __sizeOf();

   public static OmniPvdWriter wrapPointer(long address) {
      return address != 0L ? new OmniPvdWriter(address) : null;
   }

   public static OmniPvdWriter arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected OmniPvdWriter(long address) {
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

   public void setWriteStream(OmniPvdWriteStream writeStream) {
      this.checkNotNull();
      _setWriteStream(this.address, writeStream.getAddress());
   }

   private static native void _setWriteStream(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(7, "physx.support.OmniPvdWriter");
   }
}
