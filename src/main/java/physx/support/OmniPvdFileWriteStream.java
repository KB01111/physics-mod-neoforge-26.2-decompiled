package physx.support;

import physx.PlatformChecks;

public class OmniPvdFileWriteStream extends OmniPvdWriteStream {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected OmniPvdFileWriteStream() {
   }

   private static native int __sizeOf();

   public static OmniPvdFileWriteStream wrapPointer(long address) {
      return address != 0L ? new OmniPvdFileWriteStream(address) : null;
   }

   public static OmniPvdFileWriteStream arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected OmniPvdFileWriteStream(long address) {
      super(address);
   }

   public void setFileName(String fileName) {
      this.checkNotNull();
      _setFileName(this.address, fileName);
   }

   private static native void _setFileName(long var0, String var2);

   static {
      PlatformChecks.requirePlatform(7, "physx.support.OmniPvdFileWriteStream");
   }
}
