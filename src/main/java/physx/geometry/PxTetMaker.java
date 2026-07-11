package physx.geometry;

import physx.NativeObject;
import physx.common.PxBoundedData;
import physx.support.PxArray_PxU32;
import physx.support.PxArray_PxVec3;
import physx.support.PxI32ConstPtr;
import physx.support.PxU32ConstPtr;

public class PxTetMaker extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTetMaker() {
   }

   private static native int __sizeOf();

   public static PxTetMaker wrapPointer(long address) {
      return address != 0L ? new PxTetMaker(address) : null;
   }

   public static PxTetMaker arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetMaker(long address) {
      super(address);
   }

   public static boolean createConformingTetrahedronMesh(
      PxSimpleTriangleMesh triangleMesh, PxArray_PxVec3 outVertices, PxArray_PxU32 outTetIndices, boolean validate, float volumeThreshold
   ) {
      return _createConformingTetrahedronMesh(triangleMesh.getAddress(), outVertices.getAddress(), outTetIndices.getAddress(), validate, volumeThreshold);
   }

   private static native boolean _createConformingTetrahedronMesh(long var0, long var2, long var4, boolean var6, float var7);

   public static boolean createVoxelTetrahedronMesh(
      PxTetrahedronMeshDesc tetMesh, int numVoxelsAlongLongestBoundingBoxAxis, PxArray_PxVec3 outVertices, PxArray_PxU32 outTetIndices
   ) {
      return _createVoxelTetrahedronMesh(tetMesh.getAddress(), numVoxelsAlongLongestBoundingBoxAxis, outVertices.getAddress(), outTetIndices.getAddress());
   }

   private static native boolean _createVoxelTetrahedronMesh(long var0, int var2, long var3, long var5);

   public static boolean createVoxelTetrahedronMeshFromEdgeLength(
      PxTetrahedronMeshDesc tetMesh, float voxelEdgeLength, PxArray_PxVec3 outVertices, PxArray_PxU32 outTetIndices
   ) {
      return _createVoxelTetrahedronMeshFromEdgeLength(tetMesh.getAddress(), voxelEdgeLength, outVertices.getAddress(), outTetIndices.getAddress());
   }

   private static native boolean _createVoxelTetrahedronMeshFromEdgeLength(long var0, float var2, long var3, long var5);

   public static PxTriangleMeshAnalysisResults validateTriangleMesh(PxSimpleTriangleMesh triangleMesh, float minVolumeThreshold, float minTriangleAngleRadians) {
      return PxTriangleMeshAnalysisResults.wrapPointer(_validateTriangleMesh(triangleMesh.getAddress(), minVolumeThreshold, minTriangleAngleRadians));
   }

   private static native long _validateTriangleMesh(long var0, float var2, float var3);

   public static PxTetrahedronMeshAnalysisResults validateTetrahedronMesh(PxBoundedData points, PxBoundedData tetrahedra, float minTetVolumeThreshold) {
      return PxTetrahedronMeshAnalysisResults.wrapPointer(_validateTetrahedronMesh(points.getAddress(), tetrahedra.getAddress(), minTetVolumeThreshold));
   }

   private static native long _validateTetrahedronMesh(long var0, long var2, float var4);

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(), inputIndices.getAddress(), targetTriangleCount, maximalEdgeLength, outputVertices.getAddress(), outputIndices.getAddress()
      );
   }

   private static native void _simplifyTriangleMesh(long var0, long var2, int var4, float var5, long var6, long var8);

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress()
      );
   }

   private static native void _simplifyTriangleMesh(long var0, long var2, int var4, float var5, long var6, long var8, long var10);

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap,
      float edgeLengthCostWeight
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress(),
         edgeLengthCostWeight
      );
   }

   private static native void _simplifyTriangleMesh(long var0, long var2, int var4, float var5, long var6, long var8, long var10, float var12);

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap,
      float edgeLengthCostWeight,
      float flatnessDetectionThreshold
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress(),
         edgeLengthCostWeight,
         flatnessDetectionThreshold
      );
   }

   private static native void _simplifyTriangleMesh(long var0, long var2, int var4, float var5, long var6, long var8, long var10, float var12, float var13);

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap,
      float edgeLengthCostWeight,
      float flatnessDetectionThreshold,
      boolean projectSimplifiedPointsOnInputMeshSurface
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress(),
         edgeLengthCostWeight,
         flatnessDetectionThreshold,
         projectSimplifiedPointsOnInputMeshSurface
      );
   }

   private static native void _simplifyTriangleMesh(
      long var0, long var2, int var4, float var5, long var6, long var8, long var10, float var12, float var13, boolean var14
   );

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap,
      float edgeLengthCostWeight,
      float flatnessDetectionThreshold,
      boolean projectSimplifiedPointsOnInputMeshSurface,
      PxArray_PxU32 outputVertexToInputTriangle
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress(),
         edgeLengthCostWeight,
         flatnessDetectionThreshold,
         projectSimplifiedPointsOnInputMeshSurface,
         outputVertexToInputTriangle.getAddress()
      );
   }

   private static native void _simplifyTriangleMesh(
      long var0, long var2, int var4, float var5, long var6, long var8, long var10, float var12, float var13, boolean var14, long var15
   );

   public static void simplifyTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int targetTriangleCount,
      float maximalEdgeLength,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap,
      float edgeLengthCostWeight,
      float flatnessDetectionThreshold,
      boolean projectSimplifiedPointsOnInputMeshSurface,
      PxArray_PxU32 outputVertexToInputTriangle,
      boolean removeDisconnectedPatches
   ) {
      _simplifyTriangleMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         targetTriangleCount,
         maximalEdgeLength,
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         vertexMap.getAddress(),
         edgeLengthCostWeight,
         flatnessDetectionThreshold,
         projectSimplifiedPointsOnInputMeshSurface,
         outputVertexToInputTriangle.getAddress(),
         removeDisconnectedPatches
      );
   }

   private static native void _simplifyTriangleMesh(
      long var0, long var2, int var4, float var5, long var6, long var8, long var10, float var12, float var13, boolean var14, long var15, boolean var17
   );

   public static void remeshTriangleMesh(
      PxArray_PxVec3 inputVertices, PxArray_PxU32 inputIndices, int gridResolution, PxArray_PxVec3 outputVertices, PxArray_PxU32 outputIndices
   ) {
      _remeshTriangleMesh(inputVertices.getAddress(), inputIndices.getAddress(), gridResolution, outputVertices.getAddress(), outputIndices.getAddress());
   }

   private static native void _remeshTriangleMesh(long var0, long var2, int var4, long var5, long var7);

   public static void remeshTriangleMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      int gridResolution,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      PxArray_PxU32 vertexMap
   ) {
      _remeshTriangleMesh(
         inputVertices.getAddress(), inputIndices.getAddress(), gridResolution, outputVertices.getAddress(), outputIndices.getAddress(), vertexMap.getAddress()
      );
   }

   private static native void _remeshTriangleMesh(long var0, long var2, int var4, long var5, long var7, long var9);

   public static void createTreeBasedTetrahedralMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      boolean useTreeNodes,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      float volumeThreshold
   ) {
      _createTreeBasedTetrahedralMesh(
         inputVertices.getAddress(), inputIndices.getAddress(), useTreeNodes, outputVertices.getAddress(), outputIndices.getAddress(), volumeThreshold
      );
   }

   private static native void _createTreeBasedTetrahedralMesh(long var0, long var2, boolean var4, long var5, long var7, float var9);

   public static void createRelaxedVoxelTetrahedralMesh(
      PxArray_PxVec3 inputVertices, PxArray_PxU32 inputIndices, PxArray_PxVec3 outputVertices, PxArray_PxU32 outputIndices, int resolution
   ) {
      _createRelaxedVoxelTetrahedralMesh(
         inputVertices.getAddress(), inputIndices.getAddress(), outputVertices.getAddress(), outputIndices.getAddress(), resolution
      );
   }

   private static native void _createRelaxedVoxelTetrahedralMesh(long var0, long var2, long var4, long var6, int var8);

   public static void createRelaxedVoxelTetrahedralMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      int resolution,
      int numRelaxationIterations
   ) {
      _createRelaxedVoxelTetrahedralMesh(
         inputVertices.getAddress(), inputIndices.getAddress(), outputVertices.getAddress(), outputIndices.getAddress(), resolution, numRelaxationIterations
      );
   }

   private static native void _createRelaxedVoxelTetrahedralMesh(long var0, long var2, long var4, long var6, int var8, int var9);

   public static void createRelaxedVoxelTetrahedralMesh(
      PxArray_PxVec3 inputVertices,
      PxArray_PxU32 inputIndices,
      PxArray_PxVec3 outputVertices,
      PxArray_PxU32 outputIndices,
      int resolution,
      int numRelaxationIterations,
      float relMinTetVolume
   ) {
      _createRelaxedVoxelTetrahedralMesh(
         inputVertices.getAddress(),
         inputIndices.getAddress(),
         outputVertices.getAddress(),
         outputIndices.getAddress(),
         resolution,
         numRelaxationIterations,
         relMinTetVolume
      );
   }

   private static native void _createRelaxedVoxelTetrahedralMesh(long var0, long var2, long var4, long var6, int var8, int var9, float var10);

   public static void detectTriangleIslands(PxI32ConstPtr triangles, int numTriangles, PxArray_PxU32 islandIndexPerTriangle) {
      _detectTriangleIslands(triangles.getAddress(), numTriangles, islandIndexPerTriangle.getAddress());
   }

   private static native void _detectTriangleIslands(long var0, int var2, long var3);

   public static int findLargestIslandId(PxU32ConstPtr islandIndexPerTriangle, int numTriangles) {
      return _findLargestIslandId(islandIndexPerTriangle.getAddress(), numTriangles);
   }

   private static native int _findLargestIslandId(long var0, int var2);
}
