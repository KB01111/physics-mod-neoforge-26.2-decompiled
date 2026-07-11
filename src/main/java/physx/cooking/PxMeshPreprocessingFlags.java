package physx.cooking;

import physx.NativeObject;

public class PxMeshPreprocessingFlags extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxMeshPreprocessingFlags() {
   }

   private static native int __sizeOf();

   public static PxMeshPreprocessingFlags wrapPointer(long address) {
      return address != 0L ? new PxMeshPreprocessingFlags(address) : null;
   }

   public static PxMeshPreprocessingFlags arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMeshPreprocessingFlags(long address) {
      super(address);
   }

   public static PxMeshPreprocessingFlags createAt(long address, int flags) {
      __placement_new_PxMeshPreprocessingFlags(address, flags);
      PxMeshPreprocessingFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxMeshPreprocessingFlags createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxMeshPreprocessingFlags(address, flags);
      PxMeshPreprocessingFlags createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxMeshPreprocessingFlags(long var0, int var2);

   public PxMeshPreprocessingFlags(int flags) {
      this.address = _PxMeshPreprocessingFlags(flags);
   }

   private static native long _PxMeshPreprocessingFlags(int var0);

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

   public boolean isSet(PxMeshPreprocessingFlagEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxMeshPreprocessingFlagEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxMeshPreprocessingFlagEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
