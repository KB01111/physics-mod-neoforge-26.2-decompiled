package physx.geometry;

import physx.NativeObject;

public class PxConvexMeshGeometry extends PxGeometry {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxConvexMeshGeometry() {
   }

   private static native int __sizeOf();

   public static PxConvexMeshGeometry wrapPointer(long address) {
      return address != 0L ? new PxConvexMeshGeometry(address) : null;
   }

   public static PxConvexMeshGeometry arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxConvexMeshGeometry(long address) {
      super(address);
   }

   public static PxConvexMeshGeometry createAt(long address, PxConvexMesh mesh) {
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxConvexMeshGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh mesh) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxConvexMeshGeometry(long var0, long var2);

   public static PxConvexMeshGeometry createAt(long address, PxConvexMesh mesh, PxMeshScale scaling) {
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress(), scaling.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxConvexMeshGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh mesh, PxMeshScale scaling) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress(), scaling.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxConvexMeshGeometry(long var0, long var2, long var4);

   public static PxConvexMeshGeometry createAt(long address, PxConvexMesh mesh, PxMeshScale scaling, PxConvexMeshGeometryFlags flags) {
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress(), scaling.getAddress(), flags.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxConvexMeshGeometry createAt(
      T allocator, NativeObject.Allocator<T> allocate, PxConvexMesh mesh, PxMeshScale scaling, PxConvexMeshGeometryFlags flags
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxConvexMeshGeometry(address, mesh.getAddress(), scaling.getAddress(), flags.getAddress());
      PxConvexMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxConvexMeshGeometry(long var0, long var2, long var4, long var6);

   public PxConvexMeshGeometry(PxConvexMesh mesh) {
      this.address = _PxConvexMeshGeometry(mesh.getAddress());
   }

   private static native long _PxConvexMeshGeometry(long var0);

   public PxConvexMeshGeometry(PxConvexMesh mesh, PxMeshScale scaling) {
      this.address = _PxConvexMeshGeometry(mesh.getAddress(), scaling.getAddress());
   }

   private static native long _PxConvexMeshGeometry(long var0, long var2);

   public PxConvexMeshGeometry(PxConvexMesh mesh, PxMeshScale scaling, PxConvexMeshGeometryFlags flags) {
      this.address = _PxConvexMeshGeometry(mesh.getAddress(), scaling.getAddress(), flags.getAddress());
   }

   private static native long _PxConvexMeshGeometry(long var0, long var2, long var4);

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

   public PxMeshScale getScale() {
      this.checkNotNull();
      return PxMeshScale.wrapPointer(_getScale(this.address));
   }

   private static native long _getScale(long var0);

   public void setScale(PxMeshScale value) {
      this.checkNotNull();
      _setScale(this.address, value.getAddress());
   }

   private static native void _setScale(long var0, long var2);

   public PxConvexMesh getConvexMesh() {
      this.checkNotNull();
      return PxConvexMesh.wrapPointer(_getConvexMesh(this.address));
   }

   private static native long _getConvexMesh(long var0);

   public void setConvexMesh(PxConvexMesh value) {
      this.checkNotNull();
      _setConvexMesh(this.address, value.getAddress());
   }

   private static native void _setConvexMesh(long var0, long var2);

   public PxConvexMeshGeometryFlags getMeshFlags() {
      this.checkNotNull();
      return PxConvexMeshGeometryFlags.wrapPointer(_getMeshFlags(this.address));
   }

   private static native long _getMeshFlags(long var0);

   public void setMeshFlags(PxConvexMeshGeometryFlags value) {
      this.checkNotNull();
      _setMeshFlags(this.address, value.getAddress());
   }

   private static native void _setMeshFlags(long var0, long var2);
}
