package physx.geometry;

import physx.NativeObject;

public class PxTriangleMeshGeometry extends PxGeometry {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTriangleMeshGeometry() {
   }

   private static native int __sizeOf();

   public static PxTriangleMeshGeometry wrapPointer(long address) {
      return address != 0L ? new PxTriangleMeshGeometry(address) : null;
   }

   public static PxTriangleMeshGeometry arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTriangleMeshGeometry(long address) {
      super(address);
   }

   public static PxTriangleMeshGeometry createAt(long address, PxTriangleMesh mesh) {
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTriangleMeshGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, PxTriangleMesh mesh) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTriangleMeshGeometry(long var0, long var2);

   public static PxTriangleMeshGeometry createAt(long address, PxTriangleMesh mesh, PxMeshScale scaling) {
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress(), scaling.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTriangleMeshGeometry createAt(T allocator, NativeObject.Allocator<T> allocate, PxTriangleMesh mesh, PxMeshScale scaling) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress(), scaling.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTriangleMeshGeometry(long var0, long var2, long var4);

   public static PxTriangleMeshGeometry createAt(long address, PxTriangleMesh mesh, PxMeshScale scaling, PxMeshGeometryFlags flags) {
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress(), scaling.getAddress(), flags.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTriangleMeshGeometry createAt(
      T allocator, NativeObject.Allocator<T> allocate, PxTriangleMesh mesh, PxMeshScale scaling, PxMeshGeometryFlags flags
   ) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTriangleMeshGeometry(address, mesh.getAddress(), scaling.getAddress(), flags.getAddress());
      PxTriangleMeshGeometry createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTriangleMeshGeometry(long var0, long var2, long var4, long var6);

   public PxTriangleMeshGeometry(PxTriangleMesh mesh) {
      this.address = _PxTriangleMeshGeometry(mesh.getAddress());
   }

   private static native long _PxTriangleMeshGeometry(long var0);

   public PxTriangleMeshGeometry(PxTriangleMesh mesh, PxMeshScale scaling) {
      this.address = _PxTriangleMeshGeometry(mesh.getAddress(), scaling.getAddress());
   }

   private static native long _PxTriangleMeshGeometry(long var0, long var2);

   public PxTriangleMeshGeometry(PxTriangleMesh mesh, PxMeshScale scaling, PxMeshGeometryFlags flags) {
      this.address = _PxTriangleMeshGeometry(mesh.getAddress(), scaling.getAddress(), flags.getAddress());
   }

   private static native long _PxTriangleMeshGeometry(long var0, long var2, long var4);

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

   public PxMeshGeometryFlags getMeshFlags() {
      this.checkNotNull();
      return PxMeshGeometryFlags.wrapPointer(_getMeshFlags(this.address));
   }

   private static native long _getMeshFlags(long var0);

   public void setMeshFlags(PxMeshGeometryFlags value) {
      this.checkNotNull();
      _setMeshFlags(this.address, value.getAddress());
   }

   private static native void _setMeshFlags(long var0, long var2);

   public PxTriangleMesh getTriangleMesh() {
      this.checkNotNull();
      return PxTriangleMesh.wrapPointer(_getTriangleMesh(this.address));
   }

   private static native long _getTriangleMesh(long var0);

   public void setTriangleMesh(PxTriangleMesh value) {
      this.checkNotNull();
      _setTriangleMesh(this.address, value.getAddress());
   }

   private static native void _setTriangleMesh(long var0, long var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
