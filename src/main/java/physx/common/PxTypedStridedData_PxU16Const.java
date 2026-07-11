package physx.common;

import physx.NativeObject;
import physx.support.PxU16ConstPtr;

public class PxTypedStridedData_PxU16Const extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTypedStridedData_PxU16Const() {
   }

   private static native int __sizeOf();

   public static PxTypedStridedData_PxU16Const wrapPointer(long address) {
      return address != 0L ? new PxTypedStridedData_PxU16Const(address) : null;
   }

   public static PxTypedStridedData_PxU16Const arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTypedStridedData_PxU16Const(long address) {
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

   public PxU16ConstPtr getData() {
      this.checkNotNull();
      return PxU16ConstPtr.wrapPointer(_getData(this.address));
   }

   private static native long _getData(long var0);

   public void setData(PxU16ConstPtr value) {
      this.checkNotNull();
      _setData(this.address, value.getAddress());
   }

   private static native void _setData(long var0, long var2);
}
