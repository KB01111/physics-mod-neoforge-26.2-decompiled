package physx.physics;

import physx.NativeObject;

public class PxQueryFilterData extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxQueryFilterData wrapPointer(long address) {
      return address != 0L ? new PxQueryFilterData(address) : null;
   }

   public static PxQueryFilterData arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxQueryFilterData(long address) {
      super(address);
   }

   public static PxQueryFilterData createAt(long address) {
      __placement_new_PxQueryFilterData(address);
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxQueryFilterData createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxQueryFilterData(address);
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxQueryFilterData(long var0);

   public static PxQueryFilterData createAt(long address, PxFilterData fd, PxQueryFlags f) {
      __placement_new_PxQueryFilterData(address, fd.getAddress(), f.getAddress());
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxQueryFilterData createAt(T allocator, NativeObject.Allocator<T> allocate, PxFilterData fd, PxQueryFlags f) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxQueryFilterData(address, fd.getAddress(), f.getAddress());
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxQueryFilterData(long var0, long var2, long var4);

   public static PxQueryFilterData createAt(long address, PxQueryFlags f) {
      __placement_new_PxQueryFilterData(address, f.getAddress());
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxQueryFilterData createAt(T allocator, NativeObject.Allocator<T> allocate, PxQueryFlags f) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxQueryFilterData(address, f.getAddress());
      PxQueryFilterData createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxQueryFilterData(long var0, long var2);

   public PxQueryFilterData() {
      this.address = _PxQueryFilterData();
   }

   private static native long _PxQueryFilterData();

   public PxQueryFilterData(PxFilterData fd, PxQueryFlags f) {
      this.address = _PxQueryFilterData(fd.getAddress(), f.getAddress());
   }

   private static native long _PxQueryFilterData(long var0, long var2);

   public PxQueryFilterData(PxQueryFlags f) {
      this.address = _PxQueryFilterData(f.getAddress());
   }

   private static native long _PxQueryFilterData(long var0);

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

   public PxFilterData getData() {
      this.checkNotNull();
      return PxFilterData.wrapPointer(_getData(this.address));
   }

   private static native long _getData(long var0);

   public void setData(PxFilterData value) {
      this.checkNotNull();
      _setData(this.address, value.getAddress());
   }

   private static native void _setData(long var0, long var2);

   public PxQueryFlags getFlags() {
      this.checkNotNull();
      return PxQueryFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxQueryFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);
}
