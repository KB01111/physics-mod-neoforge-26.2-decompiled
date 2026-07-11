package physx.support;

import physx.NativeObject;
import physx.physics.PxContactPairPoint;

public class PxArray_PxContactPairPoint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxArray_PxContactPairPoint wrapPointer(long address) {
      return address != 0L ? new PxArray_PxContactPairPoint(address) : null;
   }

   public static PxArray_PxContactPairPoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArray_PxContactPairPoint(long address) {
      super(address);
   }

   public static PxArray_PxContactPairPoint createAt(long address) {
      __placement_new_PxArray_PxContactPairPoint(address);
      PxArray_PxContactPairPoint createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArray_PxContactPairPoint createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArray_PxContactPairPoint(address);
      PxArray_PxContactPairPoint createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArray_PxContactPairPoint(long var0);

   public static PxArray_PxContactPairPoint createAt(long address, int size) {
      __placement_new_PxArray_PxContactPairPoint(address, size);
      PxArray_PxContactPairPoint createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxArray_PxContactPairPoint createAt(T allocator, NativeObject.Allocator<T> allocate, int size) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxArray_PxContactPairPoint(address, size);
      PxArray_PxContactPairPoint createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxArray_PxContactPairPoint(long var0, int var2);

   public PxArray_PxContactPairPoint() {
      this.address = _PxArray_PxContactPairPoint();
   }

   private static native long _PxArray_PxContactPairPoint();

   public PxArray_PxContactPairPoint(int size) {
      this.address = _PxArray_PxContactPairPoint(size);
   }

   private static native long _PxArray_PxContactPairPoint(int var0);

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

   public PxContactPairPoint get(int index) {
      this.checkNotNull();
      return PxContactPairPoint.wrapPointer(_get(this.address, index));
   }

   private static native long _get(long var0, int var2);

   public void set(int index, PxContactPairPoint value) {
      this.checkNotNull();
      _set(this.address, index, value.getAddress());
   }

   private static native void _set(long var0, int var2, long var3);

   public PxContactPairPoint begin() {
      this.checkNotNull();
      return PxContactPairPoint.wrapPointer(_begin(this.address));
   }

   private static native long _begin(long var0);

   public int size() {
      this.checkNotNull();
      return _size(this.address);
   }

   private static native int _size(long var0);

   public void pushBack(PxContactPairPoint value) {
      this.checkNotNull();
      _pushBack(this.address, value.getAddress());
   }

   private static native void _pushBack(long var0, long var2);

   public void clear() {
      this.checkNotNull();
      _clear(this.address);
   }

   private static native void _clear(long var0);
}
