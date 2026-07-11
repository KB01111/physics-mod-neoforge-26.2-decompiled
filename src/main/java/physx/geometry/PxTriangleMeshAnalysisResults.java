package physx.geometry;

import physx.NativeObject;

public class PxTriangleMeshAnalysisResults extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTriangleMeshAnalysisResults() {
   }

   private static native int __sizeOf();

   public static PxTriangleMeshAnalysisResults wrapPointer(long address) {
      return address != 0L ? new PxTriangleMeshAnalysisResults(address) : null;
   }

   public static PxTriangleMeshAnalysisResults arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTriangleMeshAnalysisResults(long address) {
      super(address);
   }

   public static PxTriangleMeshAnalysisResults createAt(long address, int flags) {
      __placement_new_PxTriangleMeshAnalysisResults(address, flags);
      PxTriangleMeshAnalysisResults createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTriangleMeshAnalysisResults createAt(T allocator, NativeObject.Allocator<T> allocate, int flags) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTriangleMeshAnalysisResults(address, flags);
      PxTriangleMeshAnalysisResults createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTriangleMeshAnalysisResults(long var0, int var2);

   public PxTriangleMeshAnalysisResults(int flags) {
      this.address = _PxTriangleMeshAnalysisResults(flags);
   }

   private static native long _PxTriangleMeshAnalysisResults(int var0);

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

   public boolean isSet(PxTriangleMeshAnalysisResultEnum flag) {
      this.checkNotNull();
      return _isSet(this.address, flag.value);
   }

   private static native boolean _isSet(long var0, int var2);

   public void raise(PxTriangleMeshAnalysisResultEnum flag) {
      this.checkNotNull();
      _raise(this.address, flag.value);
   }

   private static native void _raise(long var0, int var2);

   public void clear(PxTriangleMeshAnalysisResultEnum flag) {
      this.checkNotNull();
      _clear(this.address, flag.value);
   }

   private static native void _clear(long var0, int var2);
}
