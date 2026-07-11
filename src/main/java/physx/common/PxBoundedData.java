package physx.common;

import physx.NativeObject;

public class PxBoundedData extends PxStridedData {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxBoundedData wrapPointer(long address) {
      return address != 0L ? new PxBoundedData(address) : null;
   }

   public static PxBoundedData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxBoundedData(long address) {
      super(address);
   }

   public static PxBoundedData createAt(long address) {
      __placement_new_PxBoundedData(address);
      PxBoundedData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxBoundedData createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxBoundedData(address);
      PxBoundedData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxBoundedData(long var0);

   public PxBoundedData() {
      this.address = _PxBoundedData();
   }

   private static native long _PxBoundedData();

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

   public int getCount() {
      this.checkNotNull();
      return _getCount(this.address);
   }

   private static native int _getCount(long var0);

   public void setCount(int value) {
      this.checkNotNull();
      _setCount(this.address, value);
   }

   private static native void _setCount(long var0, int var2);
}
