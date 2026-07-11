package physx.extensions;

import physx.NativeObject;
import physx.geometry.PxGeometry;

public class ConvexGeomSupport extends Support {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static ConvexGeomSupport wrapPointer(long address) {
      return address != 0L ? new ConvexGeomSupport(address) : null;
   }

   public static ConvexGeomSupport arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected ConvexGeomSupport(long address) {
      super(address);
   }

   public static ConvexGeomSupport createAt(long address) {
      __placement_new_ConvexGeomSupport(address);
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> ConvexGeomSupport createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_ConvexGeomSupport(address);
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_ConvexGeomSupport(long var0);

   public static ConvexGeomSupport createAt(long address, PxGeometry geom) {
      __placement_new_ConvexGeomSupport(address, geom.getAddress());
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> ConvexGeomSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxGeometry geom) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_ConvexGeomSupport(address, geom.getAddress());
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_ConvexGeomSupport(long var0, long var2);

   public static ConvexGeomSupport createAt(long address, PxGeometry geom, float margin) {
      __placement_new_ConvexGeomSupport(address, geom.getAddress(), margin);
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> ConvexGeomSupport createAt(T allocator, NativeObject.Allocator<T> allocate, PxGeometry geom, float margin) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_ConvexGeomSupport(address, geom.getAddress(), margin);
      ConvexGeomSupport createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_ConvexGeomSupport(long var0, long var2, float var4);

   public ConvexGeomSupport() {
      this.address = _ConvexGeomSupport();
   }

   private static native long _ConvexGeomSupport();

   public ConvexGeomSupport(PxGeometry geom) {
      this.address = _ConvexGeomSupport(geom.getAddress());
   }

   private static native long _ConvexGeomSupport(long var0);

   public ConvexGeomSupport(PxGeometry geom, float margin) {
      this.address = _ConvexGeomSupport(geom.getAddress(), margin);
   }

   private static native long _ConvexGeomSupport(long var0, float var2);

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
}
