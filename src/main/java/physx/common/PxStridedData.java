package physx.common;

import physx.NativeObject;

public class PxStridedData extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxStridedData() {
   }

   private static native int __sizeOf();

   public static PxStridedData wrapPointer(long address) {
      return address != 0L ? new PxStridedData(address) : null;
   }

   public static PxStridedData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxStridedData(long address) {
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

   public int getStride() {
      this.checkNotNull();
      return _getStride(this.address);
   }

   private static native int _getStride(long var0);

   public void setStride(int value) {
      this.checkNotNull();
      _setStride(this.address, value);
   }

   private static native void _setStride(long var0, int var2);

   public NativeObject getData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getData(this.address));
   }

   private static native long _getData(long var0);

   public void setData(NativeObject value) {
      this.checkNotNull();
      _setData(this.address, value.getAddress());
   }

   private static native void _setData(long var0, long var2);
}
