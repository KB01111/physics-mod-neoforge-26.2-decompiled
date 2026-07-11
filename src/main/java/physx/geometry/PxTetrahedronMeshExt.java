package physx.geometry;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.common.PxVec4;
import physx.support.PxArray_PxU32;
import physx.support.PxArray_PxVec3;
import physx.support.PxArray_PxVec4;

public class PxTetrahedronMeshExt extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTetrahedronMeshExt() {
   }

   private static native int __sizeOf();

   public static PxTetrahedronMeshExt wrapPointer(long address) {
      return address != 0L ? new PxTetrahedronMeshExt(address) : null;
   }

   public static PxTetrahedronMeshExt arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetrahedronMeshExt(long address) {
      super(address);
   }

   public static int findTetrahedronContainingPoint(PxTetrahedronMesh mesh, PxVec3 point, PxVec4 bary, float tolerance) {
      return _findTetrahedronContainingPoint(mesh.getAddress(), point.getAddress(), bary.getAddress(), tolerance);
   }

   private static native int _findTetrahedronContainingPoint(long var0, long var2, long var4, float var6);

   public static int findTetrahedronClosestToPoint(PxTetrahedronMesh mesh, PxVec3 point, PxVec4 bary) {
      return _findTetrahedronClosestToPoint(mesh.getAddress(), point.getAddress(), bary.getAddress());
   }

   private static native int _findTetrahedronClosestToPoint(long var0, long var2, long var4);

   public static void createPointsToTetrahedronMap(
      PxArray_PxVec3 tetMeshVertices, PxArray_PxU32 tetMeshIndices, PxArray_PxVec3 pointsToEmbed, PxArray_PxVec4 barycentricCoordinates, PxArray_PxU32 tetLinks
   ) {
      _createPointsToTetrahedronMap(
         tetMeshVertices.getAddress(), tetMeshIndices.getAddress(), pointsToEmbed.getAddress(), barycentricCoordinates.getAddress(), tetLinks.getAddress()
      );
   }

   private static native void _createPointsToTetrahedronMap(long var0, long var2, long var4, long var6, long var8);

   public static void extractTetMeshSurface(PxTetrahedronMesh mesh, PxArray_PxU32 surfaceTriangles) {
      _extractTetMeshSurface(mesh.getAddress(), surfaceTriangles.getAddress());
   }

   private static native void _extractTetMeshSurface(long var0, long var2);

   public static void extractTetMeshSurface(PxTetrahedronMesh mesh, PxArray_PxU32 surfaceTriangles, PxArray_PxU32 surfaceTriangleToTet) {
      _extractTetMeshSurface(mesh.getAddress(), surfaceTriangles.getAddress(), surfaceTriangleToTet.getAddress());
   }

   private static native void _extractTetMeshSurface(long var0, long var2, long var4);

   public static void extractTetMeshSurface(
      PxTetrahedronMesh mesh, PxArray_PxU32 surfaceTriangles, PxArray_PxU32 surfaceTriangleToTet, boolean flipTriangleOrientation
   ) {
      _extractTetMeshSurface(mesh.getAddress(), surfaceTriangles.getAddress(), surfaceTriangleToTet.getAddress(), flipTriangleOrientation);
   }

   private static native void _extractTetMeshSurface(long var0, long var2, long var4, boolean var6);
}
