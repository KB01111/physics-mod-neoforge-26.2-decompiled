package physx.support;

import physx.NativeObject;
import physx.PlatformChecks;

public class OmniPvdWriteStream extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected OmniPvdWriteStream() {
   }

   private static native int __sizeOf();

   public static OmniPvdWriteStream wrapPointer(long address) {
      return address != 0L ? new OmniPvdWriteStream(address) : null;
   }

   public static OmniPvdWriteStream arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected OmniPvdWriteStream(long address) {
      super(address);
   }

   static {
      PlatformChecks.requirePlatform(7, "physx.support.OmniPvdWriteStream");
   }
}
