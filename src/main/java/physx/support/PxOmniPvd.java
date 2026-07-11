package physx.support;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxOmniPvd extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxOmniPvd() {
   }

   private static native int __sizeOf();

   public static PxOmniPvd wrapPointer(long address) {
      return address != 0L ? new PxOmniPvd(address) : null;
   }

   public static PxOmniPvd arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxOmniPvd(long address) {
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

   public OmniPvdWriter getWriter() {
      this.checkNotNull();
      PlatformChecks.requirePlatform(7, "physx.support.PxOmniPvd");
      return OmniPvdWriter.wrapPointer(_getWriter(this.address));
   }

   private static native long _getWriter(long var0);

   public OmniPvdFileWriteStream getFileWriteStream() {
      this.checkNotNull();
      PlatformChecks.requirePlatform(7, "physx.support.PxOmniPvd");
      return OmniPvdFileWriteStream.wrapPointer(_getFileWriteStream(this.address));
   }

   private static native long _getFileWriteStream(long var0);

   public boolean startSampling() {
      this.checkNotNull();
      return _startSampling(this.address);
   }

   private static native boolean _startSampling(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);
}
