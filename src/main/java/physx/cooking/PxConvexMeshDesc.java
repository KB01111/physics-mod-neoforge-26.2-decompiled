package physx.cooking;

import physx.NativeObject;
import physx.common.PxBoundedData;

public class PxConvexMeshDesc extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxConvexMeshDesc wrapPointer(long address) {
      return address != 0L ? new PxConvexMeshDesc(address) : null;
   }

   public static PxConvexMeshDesc arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxConvexMeshDesc(long address) {
      super(address);
   }

   public static PxConvexMeshDesc createAt(long address) {
      __placement_new_PxConvexMeshDesc(address);
      PxConvexMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxConvexMeshDesc createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxConvexMeshDesc(address);
      PxConvexMeshDesc createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxConvexMeshDesc(long var0);

   public PxConvexMeshDesc() {
      this.address = _PxConvexMeshDesc();
   }

   private static native long _PxConvexMeshDesc();

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

   public PxBoundedData getPoints() {
      this.checkNotNull();
      return PxBoundedData.wrapPointer(_getPoints(this.address));
   }

   private static native long _getPoints(long var0);

   public void setPoints(PxBoundedData value) {
      this.checkNotNull();
      _setPoints(this.address, value.getAddress());
   }

   private static native void _setPoints(long var0, long var2);

   public PxConvexFlags getFlags() {
      this.checkNotNull();
      return PxConvexFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setFlags(PxConvexFlags value) {
      this.checkNotNull();
      _setFlags(this.address, value.getAddress());
   }

   private static native void _setFlags(long var0, long var2);
}
