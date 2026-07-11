package physx.geometry;

import physx.NativeObject;

public class PxTetrahedronMeshFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTetrahedronMeshFlags() {
   }

   private static native int __sizeOf();

   public static PxTetrahedronMeshFlags wrapPointer(long address) {
      return address != 0L ? new PxTetrahedronMeshFlags(address) : null;
   }

   public static PxTetrahedronMeshFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetrahedronMeshFlags(long address) {
      super(address);
   }

   public static PxTetrahedronMeshFlags createAt(long address, byte flags) {
      __placement_new_PxTetrahedronMeshFlags(address, flags);
      PxTetrahedronMeshFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTetrahedronMeshFlags createAt(T allocator, NativeObject.Allocator<T> allocate, byte flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTetrahedronMeshFlags(address, flags);
      PxTetrahedronMeshFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTetrahedronMeshFlags(long var0, byte var2);

   public PxTetrahedronMeshFlags(byte flags) {
      this.address = _PxTetrahedronMeshFlags(flags);
   }

   private static native long _PxTetrahedronMeshFlags(byte var0);

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

   public boolean isSet(PxTetrahedronMeshFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxTetrahedronMeshFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxTetrahedronMeshFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
