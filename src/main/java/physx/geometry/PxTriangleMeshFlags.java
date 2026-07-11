package physx.geometry;

import physx.NativeObject;

public class PxTriangleMeshFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTriangleMeshFlags() {
   }

   private static native int __sizeOf();

   public static PxTriangleMeshFlags wrapPointer(long address) {
      return address != 0L ? new PxTriangleMeshFlags(address) : null;
   }

   public static PxTriangleMeshFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTriangleMeshFlags(long address) {
      super(address);
   }

   public static PxTriangleMeshFlags createAt(long address, byte flags) {
      __placement_new_PxTriangleMeshFlags(address, flags);
      PxTriangleMeshFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTriangleMeshFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTriangleMeshFlags(address, flags);
      PxTriangleMeshFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTriangleMeshFlags(long var0, byte var2);

   public PxTriangleMeshFlags(byte flags) {
      this.address = _PxTriangleMeshFlags(flags);
   }

   private static native long _PxTriangleMeshFlags(byte var0);

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

   public boolean isSet(PxTriangleMeshFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxTriangleMeshFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxTriangleMeshFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
