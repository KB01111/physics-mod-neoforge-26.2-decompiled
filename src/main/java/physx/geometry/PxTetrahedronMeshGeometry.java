package physx.geometry;

public class PxTetrahedronMeshGeometry extends PxGeometry {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTetrahedronMeshGeometry() {
   }

   private static native int __sizeOf();

   public static PxTetrahedronMeshGeometry wrapPointer(long address) {
      return address != 0L ? new PxTetrahedronMeshGeometry(address) : null;
   }

   public static PxTetrahedronMeshGeometry arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetrahedronMeshGeometry(long address) {
      super(address);
   }

   public PxTetrahedronMeshGeometry(PxTetrahedronMesh mesh) {
      this.address = _PxTetrahedronMeshGeometry(mesh.getAddress());
   }

   private static native long _PxTetrahedronMeshGeometry(long var0);

   public PxTetrahedronMesh getTetrahedronMesh() {
      this.checkNotNull();
      return PxTetrahedronMesh.wrapPointer(_getTetrahedronMesh(this.address));
   }

   private static native long _getTetrahedronMesh(long var0);

   public void setTetrahedronMesh(PxTetrahedronMesh value) {
      this.checkNotNull();
      _setTetrahedronMesh(this.address, value.getAddress());
   }

   private static native void _setTetrahedronMesh(long var0, long var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);
}
