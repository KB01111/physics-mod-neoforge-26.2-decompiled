package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.particles.PxParticleAndDiffuseBuffer;
import physx.particles.PxParticleAndDiffuseBufferDesc;
import physx.particles.PxParticleBuffer;
import physx.particles.PxParticleBufferDesc;
import physx.particles.PxParticleClothBuffer;
import physx.particles.PxParticleClothBufferHelper;
import physx.particles.PxParticleClothCooker;
import physx.particles.PxParticleClothDesc;
import physx.particles.PxParticleClothPreProcessor;
import physx.particles.PxPartitionedParticleCloth;
import physx.support.PxU32Ptr;

public class PxCudaTopLevelFunctions extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxCudaTopLevelFunctions() {
   }

   private static native int __sizeOf();

   public static PxCudaTopLevelFunctions wrapPointer(long address) {
      return address != 0L ? new PxCudaTopLevelFunctions(address) : null;
   }

   public static PxCudaTopLevelFunctions arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxCudaTopLevelFunctions(long address) {
      super(address);
   }

   public static int GetSuggestedCudaDeviceOrdinal(PxFoundation foundation) {
      return _GetSuggestedCudaDeviceOrdinal(foundation.getAddress());
   }

   private static native int _GetSuggestedCudaDeviceOrdinal(long var0);

   public static PxCudaContextManager CreateCudaContextManager(PxFoundation foundation, PxCudaContextManagerDesc desc) {
      return PxCudaContextManager.wrapPointer(_CreateCudaContextManager(foundation.getAddress(), desc.getAddress()));
   }

   private static native long _CreateCudaContextManager(long var0, long var2);

   public static PxParticleClothPreProcessor CreateParticleClothPreProcessor(PxCudaContextManager cudaContextManager) {
      return PxParticleClothPreProcessor.wrapPointer(_CreateParticleClothPreProcessor(cudaContextManager.getAddress()));
   }

   private static native long _CreateParticleClothPreProcessor(long var0);

   public static PxParticleClothBufferHelper CreateParticleClothBufferHelper(
      int maxCloths, int maxTriangles, int maxSprings, int maxParticles, PxCudaContextManager cudaContextManager
   ) {
      return PxParticleClothBufferHelper.wrapPointer(
         _CreateParticleClothBufferHelper(maxCloths, maxTriangles, maxSprings, maxParticles, cudaContextManager.getAddress())
      );
   }

   private static native long _CreateParticleClothBufferHelper(int var0, int var1, int var2, int var3, long var4);

   public static PxParticleClothCooker CreateParticleClothCooker(int vertexCount, PxVec4 inVertices, int triangleIndexCount, PxU32Ptr inTriangleIndices) {
      return PxParticleClothCooker.wrapPointer(
         _CreateParticleClothCooker(vertexCount, inVertices.getAddress(), triangleIndexCount, inTriangleIndices.getAddress())
      );
   }

   private static native long _CreateParticleClothCooker(int var0, long var1, int var3, long var4);

   public static PxParticleClothCooker CreateParticleClothCooker(
      int vertexCount, PxVec4 inVertices, int triangleIndexCount, PxU32Ptr inTriangleIndices, int constraintTypeFlags
   ) {
      return PxParticleClothCooker.wrapPointer(
         _CreateParticleClothCooker(vertexCount, inVertices.getAddress(), triangleIndexCount, inTriangleIndices.getAddress(), constraintTypeFlags)
      );
   }

   private static native long _CreateParticleClothCooker(int var0, long var1, int var3, long var4, int var6);

   public static PxParticleClothCooker CreateParticleClothCooker(
      int vertexCount, PxVec4 inVertices, int triangleIndexCount, PxU32Ptr inTriangleIndices, int constraintTypeFlags, PxVec3 verticalDirection
   ) {
      return PxParticleClothCooker.wrapPointer(
         _CreateParticleClothCooker(
            vertexCount, inVertices.getAddress(), triangleIndexCount, inTriangleIndices.getAddress(), constraintTypeFlags, verticalDirection.getAddress()
         )
      );
   }

   private static native long _CreateParticleClothCooker(int var0, long var1, int var3, long var4, int var6, long var7);

   public static PxParticleClothCooker CreateParticleClothCooker(
      int vertexCount,
      PxVec4 inVertices,
      int triangleIndexCount,
      PxU32Ptr inTriangleIndices,
      int constraintTypeFlags,
      PxVec3 verticalDirection,
      float bendingConstraintMaxAngle
   ) {
      return PxParticleClothCooker.wrapPointer(
         _CreateParticleClothCooker(
            vertexCount,
            inVertices.getAddress(),
            triangleIndexCount,
            inTriangleIndices.getAddress(),
            constraintTypeFlags,
            verticalDirection.getAddress(),
            bendingConstraintMaxAngle
         )
      );
   }

   private static native long _CreateParticleClothCooker(int var0, long var1, int var3, long var4, int var6, long var7, float var9);

   public static PxParticleClothBuffer CreateAndPopulateParticleClothBuffer(
      PxParticleBufferDesc desc, PxParticleClothDesc clothDesc, PxPartitionedParticleCloth output, PxCudaContextManager cudaContextManager
   ) {
      return PxParticleClothBuffer.wrapPointer(
         _CreateAndPopulateParticleClothBuffer(desc.getAddress(), clothDesc.getAddress(), output.getAddress(), cudaContextManager.getAddress())
      );
   }

   private static native long _CreateAndPopulateParticleClothBuffer(long var0, long var2, long var4, long var6);

   public static PxParticleBuffer CreateAndPopulateParticleBuffer(PxParticleBufferDesc desc, PxCudaContextManager cudaContextManager) {
      return PxParticleBuffer.wrapPointer(_CreateAndPopulateParticleBuffer(desc.getAddress(), cudaContextManager.getAddress()));
   }

   private static native long _CreateAndPopulateParticleBuffer(long var0, long var2);

   public static PxParticleAndDiffuseBuffer CreateAndPopulateParticleAndDiffuseBuffer(
      PxParticleAndDiffuseBufferDesc desc, PxCudaContextManager cudaContextManager
   ) {
      return PxParticleAndDiffuseBuffer.wrapPointer(_CreateAndPopulateParticleAndDiffuseBuffer(desc.getAddress(), cudaContextManager.getAddress()));
   }

   private static native long _CreateAndPopulateParticleAndDiffuseBuffer(long var0, long var2);

   public static NativeObject allocPinnedHostBufferPxU32(PxCudaContextManager cudaContextManager, int numElements) {
      return NativeObject.wrapPointer(_allocPinnedHostBufferPxU32(cudaContextManager.getAddress(), numElements));
   }

   private static native long _allocPinnedHostBufferPxU32(long var0, int var2);

   public static PxVec4 allocPinnedHostBufferPxVec4(PxCudaContextManager cudaContextManager, int numElements) {
      return PxVec4.wrapPointer(_allocPinnedHostBufferPxVec4(cudaContextManager.getAddress(), numElements));
   }

   private static native long _allocPinnedHostBufferPxVec4(long var0, int var2);

   public static void freePinnedHostBufferPxU32(PxCudaContextManager cudaContextManager, PxU32Ptr buffer) {
      _freePinnedHostBufferPxU32(cudaContextManager.getAddress(), buffer.getAddress());
   }

   private static native void _freePinnedHostBufferPxU32(long var0, long var2);

   public static void freePinnedHostBufferPxVec4(PxCudaContextManager cudaContextManager, PxVec4 buffer) {
      _freePinnedHostBufferPxVec4(cudaContextManager.getAddress(), buffer.getAddress());
   }

   private static native void _freePinnedHostBufferPxVec4(long var0, long var2);

   public static long pxU32deviceptr(NativeObject pxU32data) {
      return _pxU32deviceptr(pxU32data.getAddress());
   }

   private static native long _pxU32deviceptr(long var0);

   public static long pxVec4deviceptr(PxVec4 pxVec4data) {
      return _pxVec4deviceptr(pxVec4data.getAddress());
   }

   private static native long _pxVec4deviceptr(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.common.PxCudaTopLevelFunctions");
   }
}
