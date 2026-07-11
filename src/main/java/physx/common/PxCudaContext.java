package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxCudaContext extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCudaContext() {
   }

   private static native int __sizeOf();

   public static PxCudaContext wrapPointer(long address) {
      return address != 0L ? new PxCudaContext(address) : null;
   }

   public static PxCudaContext arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCudaContext(long address) {
      super(address);
   }

   public PxCUresult memcpyDtoH(NativeObject dstHost, long srcDevice, int byteCount) {
      this.checkNotNull();
      return PxCUresult.wrapPointer(_memcpyDtoH(this.address, dstHost.getAddress(), srcDevice, byteCount));
   }

   private static native long _memcpyDtoH(long var0, long var2, long var4, int var6);

   public PxCUresult memcpyHtoD(long dstDevice, NativeObject srcHost, int byteCount) {
      this.checkNotNull();
      return PxCUresult.wrapPointer(_memcpyHtoD(this.address, dstDevice, srcHost.getAddress(), byteCount));
   }

   private static native long _memcpyHtoD(long var0, long var2, long var4, int var6);

   static {
      PlatformChecks.requirePlatform(3, "physx.common.PxCudaContext");
   }
}
