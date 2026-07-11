package physx.geometry;

import physx.NativeObject;
import physx.common.PxQuat;
import physx.common.PxVec3;

public class PxMeshScale extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxMeshScale wrapPointer(long address) {
      return address != 0L ? new PxMeshScale(address) : null;
   }

   public static PxMeshScale arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMeshScale(long address) {
      super(address);
   }

   public static PxMeshScale createAt(long address) {
      __placement_new_PxMeshScale(address);
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxMeshScale createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxMeshScale(address);
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxMeshScale(long var0);

   public static PxMeshScale createAt(long address, float r) {
      __placement_new_PxMeshScale(address, r);
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxMeshScale createAt(T allocator, NativeObject.Allocator<T> allocate, float r) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxMeshScale(address, r);
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxMeshScale(long var0, float var2);

   public static PxMeshScale createAt(long address, PxVec3 s, PxQuat r) {
      __placement_new_PxMeshScale(address, s.getAddress(), r.getAddress());
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxMeshScale createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 s, PxQuat r) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxMeshScale(address, s.getAddress(), r.getAddress());
      PxMeshScale createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxMeshScale(long var0, long var2, long var4);

   public PxMeshScale() {
      this.address = _PxMeshScale();
   }

   private static native long _PxMeshScale();

   public PxMeshScale(float r) {
      this.address = _PxMeshScale(r);
   }

   private static native long _PxMeshScale(float var0);

   public PxMeshScale(PxVec3 s, PxQuat r) {
      this.address = _PxMeshScale(s.getAddress(), r.getAddress());
   }

   private static native long _PxMeshScale(long var0, long var2);

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
}
