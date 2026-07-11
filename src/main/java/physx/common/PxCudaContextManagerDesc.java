package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxCudaContextManagerDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxCudaContextManagerDesc wrapPointer(long address) {
      return address != 0L ? new PxCudaContextManagerDesc(address) : null;
   }

   public static PxCudaContextManagerDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCudaContextManagerDesc(long address) {
      super(address);
   }

   public static PxCudaContextManagerDesc createAt(long address) {
      __placement_new_PxCudaContextManagerDesc(address);
      PxCudaContextManagerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxCudaContextManagerDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxCudaContextManagerDesc(address);
      PxCudaContextManagerDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxCudaContextManagerDesc(long var0);

   public PxCudaContextManagerDesc() {
      this.address = _PxCudaContextManagerDesc();
   }

   private static native long _PxCudaContextManagerDesc();

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

   public CUcontext getCtx() {
      this.checkNotNull();
      return CUcontext.wrapPointer(_getCtx(this.address));
   }

   private static native long _getCtx(long var0);

   public void setCtx(CUcontext value) {
      this.checkNotNull();
      _setCtx(this.address, value.getAddress());
   }

   private static native void _setCtx(long var0, long var2);

   public NativeObject getGraphicsDevice() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getGraphicsDevice(this.address));
   }

   private static native long _getGraphicsDevice(long var0);

   public void setGraphicsDevice(NativeObject value) {
      this.checkNotNull();
      _setGraphicsDevice(this.address, value.getAddress());
   }

   private static native void _setGraphicsDevice(long var0, long var2);

   public String getAppGUID() {
      this.checkNotNull();
      return _getAppGUID(this.address);
   }

   private static native String _getAppGUID(long var0);

   public void setAppGUID(String value) {
      this.checkNotNull();
      _setAppGUID(this.address, value);
   }

   private static native void _setAppGUID(long var0, String var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.common.PxCudaContextManagerDesc");
   }
}
