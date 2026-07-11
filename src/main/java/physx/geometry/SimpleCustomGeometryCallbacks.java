package physx.geometry;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBounds3;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxMassProperties;
import physx.physics.PxGeomRaycastHit;
import physx.physics.PxGeomSweepHit;
import physx.physics.PxHitFlags;

public class SimpleCustomGeometryCallbacks extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected SimpleCustomGeometryCallbacks() {
   }

   private static native int __sizeOf();

   public static SimpleCustomGeometryCallbacks wrapPointer(long address) {
      return address != 0L ? new SimpleCustomGeometryCallbacks(address) : null;
   }

   public static SimpleCustomGeometryCallbacks arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimpleCustomGeometryCallbacks(long address) {
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

   public float getPersistentContactManifold_outBreakingThreshold() {
      this.checkNotNull();
      return _getPersistentContactManifold_outBreakingThreshold(this.address);
   }

   private static native float _getPersistentContactManifold_outBreakingThreshold(long var0);

   public void setPersistentContactManifold_outBreakingThreshold(float value) {
      this.checkNotNull();
      _setPersistentContactManifold_outBreakingThreshold(this.address, value);
   }

   private static native void _setPersistentContactManifold_outBreakingThreshold(long var0, float var2);

   public PxBounds3 getLocalBoundsImpl(PxGeometry geometry) {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getLocalBoundsImpl(this.address, geometry.getAddress()));
   }

   private static native long _getLocalBoundsImpl(long var0, long var2);

   public boolean generateContactsImpl(
      PxGeometry geom0,
      PxGeometry geom1,
      PxTransform pose0,
      PxTransform pose1,
      float contactDistance,
      float meshContactMargin,
      float toleranceLength,
      PxContactBuffer contactBuffer
   ) {
      this.checkNotNull();
      return _generateContactsImpl(
         this.address,
         geom0.getAddress(),
         geom1.getAddress(),
         pose0.getAddress(),
         pose1.getAddress(),
         contactDistance,
         meshContactMargin,
         toleranceLength,
         contactBuffer.getAddress()
      );
   }

   private static native boolean _generateContactsImpl(long var0, long var2, long var4, long var6, long var8, float var10, float var11, float var12, long var13);

   public int raycastImpl(
      PxVec3 origin, PxVec3 unitDir, PxGeometry geom, PxTransform pose, float maxDist, PxHitFlags hitFlags, int maxHits, PxGeomRaycastHit rayHits, int stride
   ) {
      this.checkNotNull();
      return _raycastImpl(
         this.address,
         origin.getAddress(),
         unitDir.getAddress(),
         geom.getAddress(),
         pose.getAddress(),
         maxDist,
         hitFlags.getAddress(),
         maxHits,
         rayHits.getAddress(),
         stride
      );
   }

   private static native int _raycastImpl(long var0, long var2, long var4, long var6, long var8, float var10, long var11, int var13, long var14, int var16);

   public boolean overlapImpl(PxGeometry geom0, PxTransform pose0, PxGeometry geom1, PxTransform pose1) {
      this.checkNotNull();
      return _overlapImpl(this.address, geom0.getAddress(), pose0.getAddress(), geom1.getAddress(), pose1.getAddress());
   }

   private static native boolean _overlapImpl(long var0, long var2, long var4, long var6, long var8);

   public boolean sweepImpl(
      PxVec3 unitDir,
      float maxDist,
      PxGeometry geom0,
      PxTransform pose0,
      PxGeometry geom1,
      PxTransform pose1,
      PxGeomSweepHit sweepHit,
      PxHitFlags hitFlags,
      float inflation
   ) {
      this.checkNotNull();
      return _sweepImpl(
         this.address,
         unitDir.getAddress(),
         maxDist,
         geom0.getAddress(),
         pose0.getAddress(),
         geom1.getAddress(),
         pose1.getAddress(),
         sweepHit.getAddress(),
         hitFlags.getAddress(),
         inflation
      );
   }

   private static native boolean _sweepImpl(long var0, long var2, float var4, long var5, long var7, long var9, long var11, long var13, long var15, float var17);

   public void computeMassPropertiesImpl(PxGeometry geometry, PxMassProperties massProperties) {
      this.checkNotNull();
      _computeMassPropertiesImpl(this.address, geometry.getAddress(), massProperties.getAddress());
   }

   private static native void _computeMassPropertiesImpl(long var0, long var2, long var4);

   public boolean usePersistentContactManifoldImpl(PxGeometry geometry) {
      this.checkNotNull();
      return _usePersistentContactManifoldImpl(this.address, geometry.getAddress());
   }

   private static native boolean _usePersistentContactManifoldImpl(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(15, "physx.geometry.SimpleCustomGeometryCallbacks");
   }
}
