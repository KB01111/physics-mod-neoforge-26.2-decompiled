package physx.physics;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxCudaContextManager;
import physx.common.PxFoundation;
import physx.common.PxInputData;
import physx.common.PxInsertionCallback;
import physx.common.PxTolerancesScale;
import physx.common.PxTransform;
import physx.geometry.PxConvexMesh;
import physx.geometry.PxGeometry;
import physx.geometry.PxTriangleMesh;
import physx.particles.PxPBDMaterial;
import physx.particles.PxPBDParticleSystem;
import physx.particles.PxParticleBuffer;
import physx.particles.PxParticleClothBuffer;

public class PxPhysics extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxPhysics() {
   }

   private static native int __sizeOf();

   public static PxPhysics wrapPointer(long address) {
      return address != 0L ? new PxPhysics(address) : null;
   }

   public static PxPhysics arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPhysics(long address) {
      super(address);
   }

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

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public PxFoundation getFoundation() {
      this.checkNotNull();
      return PxFoundation.wrapPointer(_getFoundation(this.address));
   }

   private static native long _getFoundation(long var0);

   public PxAggregate createAggregate(int maxActor, int maxShape, boolean enableSelfCollision) {
      this.checkNotNull();
      return PxAggregate.wrapPointer(_createAggregate(this.address, maxActor, maxShape, enableSelfCollision));
   }

   private static native long _createAggregate(long var0, int var2, int var3, boolean var4);

   public PxTolerancesScale getTolerancesScale() {
      this.checkNotNull();
      return PxTolerancesScale.wrapPointer(_getTolerancesScale(this.address));
   }

   private static native long _getTolerancesScale(long var0);

   public PxScene createScene(PxSceneDesc sceneDesc) {
      this.checkNotNull();
      return PxScene.wrapPointer(_createScene(this.address, sceneDesc.getAddress()));
   }

   private static native long _createScene(long var0, long var2);

   public PxRigidStatic createRigidStatic(PxTransform pose) {
      this.checkNotNull();
      return PxRigidStatic.wrapPointer(_createRigidStatic(this.address, pose.getAddress()));
   }

   private static native long _createRigidStatic(long var0, long var2);

   public PxRigidDynamic createRigidDynamic(PxTransform pose) {
      this.checkNotNull();
      return PxRigidDynamic.wrapPointer(_createRigidDynamic(this.address, pose.getAddress()));
   }

   private static native long _createRigidDynamic(long var0, long var2);

   public PxShape createShape(PxGeometry geometry, PxMaterial material) {
      this.checkNotNull();
      return PxShape.wrapPointer(_createShape(this.address, geometry.getAddress(), material.getAddress()));
   }

   private static native long _createShape(long var0, long var2, long var4);

   public PxShape createShape(PxGeometry geometry, PxMaterial material, boolean isExclusive) {
      this.checkNotNull();
      return PxShape.wrapPointer(_createShape(this.address, geometry.getAddress(), material.getAddress(), isExclusive));
   }

   private static native long _createShape(long var0, long var2, long var4, boolean var6);

   public PxShape createShape(PxGeometry geometry, PxMaterial material, boolean isExclusive, PxShapeFlags shapeFlags) {
      this.checkNotNull();
      return PxShape.wrapPointer(_createShape(this.address, geometry.getAddress(), material.getAddress(), isExclusive, shapeFlags.getAddress()));
   }

   private static native long _createShape(long var0, long var2, long var4, boolean var6, long var7);

   public PxTriangleMesh createTriangleMesh(PxInputData stream) {
      this.checkNotNull();
      return PxTriangleMesh.wrapPointer(_createTriangleMesh(this.address, stream.getAddress()));
   }

   private static native long _createTriangleMesh(long var0, long var2);

   public PxConvexMesh createConvexMesh(PxInputData stream) {
      this.checkNotNull();
      return PxConvexMesh.wrapPointer(_createConvexMesh(this.address, stream.getAddress()));
   }

   private static native long _createConvexMesh(long var0, long var2);

   public int getNbShapes() {
      this.checkNotNull();
      return _getNbShapes(this.address);
   }

   private static native int _getNbShapes(long var0);

   public PxArticulationReducedCoordinate createArticulationReducedCoordinate() {
      this.checkNotNull();
      return PxArticulationReducedCoordinate.wrapPointer(_createArticulationReducedCoordinate(this.address));
   }

   private static native long _createArticulationReducedCoordinate(long var0);

   public PxMaterial createMaterial(float staticFriction, float dynamicFriction, float restitution) {
      this.checkNotNull();
      return PxMaterial.wrapPointer(_createMaterial(this.address, staticFriction, dynamicFriction, restitution));
   }

   private static native long _createMaterial(long var0, float var2, float var3, float var4);

   public PxInsertionCallback getPhysicsInsertionCallback() {
      this.checkNotNull();
      return PxInsertionCallback.wrapPointer(_getPhysicsInsertionCallback(this.address));
   }

   private static native long _getPhysicsInsertionCallback(long var0);

   public PxPBDParticleSystem createPBDParticleSystem(PxCudaContextManager cudaContextManager) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxPBDParticleSystem.wrapPointer(_createPBDParticleSystem(this.address, cudaContextManager.getAddress()));
   }

   private static native long _createPBDParticleSystem(long var0, long var2);

   public PxPBDParticleSystem createPBDParticleSystem(PxCudaContextManager cudaContextManager, int maxNeighborhood) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxPBDParticleSystem.wrapPointer(_createPBDParticleSystem(this.address, cudaContextManager.getAddress(), maxNeighborhood));
   }

   private static native long _createPBDParticleSystem(long var0, long var2, int var4);

   public PxParticleBuffer createParticleBuffer(int maxParticles, int maxVolumes, PxCudaContextManager cudaContextManager) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxParticleBuffer.wrapPointer(_createParticleBuffer(this.address, maxParticles, maxVolumes, cudaContextManager.getAddress()));
   }

   private static native long _createParticleBuffer(long var0, int var2, int var3, long var4);

   public PxParticleClothBuffer createParticleClothBuffer(
      int maxParticles, int maxNumVolumes, int maxNumCloths, int maxNumTriangles, int maxNumSprings, PxCudaContextManager cudaContextManager
   ) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxParticleClothBuffer.wrapPointer(
         _createParticleClothBuffer(this.address, maxParticles, maxNumVolumes, maxNumCloths, maxNumTriangles, maxNumSprings, cudaContextManager.getAddress())
      );
   }

   private static native long _createParticleClothBuffer(long var0, int var2, int var3, int var4, int var5, int var6, long var7);

   public PxPBDMaterial createPBDMaterial(
      float friction, float damping, float adhesion, float viscosity, float vorticityConfinement, float surfaceTension, float cohesion, float lift, float drag
   ) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxPBDMaterial.wrapPointer(
         _createPBDMaterial(this.address, friction, damping, adhesion, viscosity, vorticityConfinement, surfaceTension, cohesion, lift, drag)
      );
   }

   private static native long _createPBDMaterial(
      long var0, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10
   );

   public PxPBDMaterial createPBDMaterial(
      float friction,
      float damping,
      float adhesion,
      float viscosity,
      float vorticityConfinement,
      float surfaceTension,
      float cohesion,
      float lift,
      float drag,
      float cflCoefficient
   ) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxPBDMaterial.wrapPointer(
         _createPBDMaterial(this.address, friction, damping, adhesion, viscosity, vorticityConfinement, surfaceTension, cohesion, lift, drag, cflCoefficient)
      );
   }

   private static native long _createPBDMaterial(
      long var0, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11
   );

   public PxPBDMaterial createPBDMaterial(
      float friction,
      float damping,
      float adhesion,
      float viscosity,
      float vorticityConfinement,
      float surfaceTension,
      float cohesion,
      float lift,
      float drag,
      float cflCoefficient,
      float gravityScale
   ) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxPhysics");
      return PxPBDMaterial.wrapPointer(
         _createPBDMaterial(
            this.address, friction, damping, adhesion, viscosity, vorticityConfinement, surfaceTension, cohesion, lift, drag, cflCoefficient, gravityScale
         )
      );
   }

   private static native long _createPBDMaterial(
      long var0, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12
   );
}
