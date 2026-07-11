package physx.geometry;

import physx.PlatformChecks;
import physx.common.PxBounds3;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.extensions.PxMassProperties;
import physx.physics.PxGeomRaycastHit;
import physx.physics.PxGeomSweepHit;
import physx.physics.PxHitFlags;

public class SimpleCustomGeometryCallbacksImpl extends SimpleCustomGeometryCallbacks {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static SimpleCustomGeometryCallbacksImpl wrapPointer(long address) {
      return address != 0L ? new SimpleCustomGeometryCallbacksImpl(address) : null;
   }

   public static SimpleCustomGeometryCallbacksImpl arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected SimpleCustomGeometryCallbacksImpl(long address) {
      super(address);
   }

   protected SimpleCustomGeometryCallbacksImpl() {
      this.address = this._SimpleCustomGeometryCallbacksImpl();
   }

   private native long _SimpleCustomGeometryCallbacksImpl();

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

   private long _getLocalBoundsImpl(long geometry) {
      return this.getLocalBoundsImpl(PxGeometry.wrapPointer(geometry)).getAddress();
   }

   @Override
   public PxBounds3 getLocalBoundsImpl(PxGeometry geometry) {
      return null;
   }

   private boolean _generateContactsImpl(
      long geom0, long geom1, long pose0, long pose1, float contactDistance, float meshContactMargin, float toleranceLength, long contactBuffer
   ) {
      return this.generateContactsImpl(
         PxGeometry.wrapPointer(geom0),
         PxGeometry.wrapPointer(geom1),
         PxTransform.wrapPointer(pose0),
         PxTransform.wrapPointer(pose1),
         contactDistance,
         meshContactMargin,
         toleranceLength,
         PxContactBuffer.wrapPointer(contactBuffer)
      );
   }

   @Override
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
      return false;
   }

   private int _raycastImpl(long origin, long unitDir, long geom, long pose, float maxDist, long hitFlags, int maxHits, long rayHits, int stride) {
      return this.raycastImpl(
         PxVec3.wrapPointer(origin),
         PxVec3.wrapPointer(unitDir),
         PxGeometry.wrapPointer(geom),
         PxTransform.wrapPointer(pose),
         maxDist,
         PxHitFlags.wrapPointer(hitFlags),
         maxHits,
         PxGeomRaycastHit.wrapPointer(rayHits),
         stride
      );
   }

   @Override
   public int raycastImpl(
      PxVec3 origin, PxVec3 unitDir, PxGeometry geom, PxTransform pose, float maxDist, PxHitFlags hitFlags, int maxHits, PxGeomRaycastHit rayHits, int stride
   ) {
      return 0;
   }

   private boolean _overlapImpl(long geom0, long pose0, long geom1, long pose1) {
      return this.overlapImpl(PxGeometry.wrapPointer(geom0), PxTransform.wrapPointer(pose0), PxGeometry.wrapPointer(geom1), PxTransform.wrapPointer(pose1));
   }

   @Override
   public boolean overlapImpl(PxGeometry geom0, PxTransform pose0, PxGeometry geom1, PxTransform pose1) {
      return false;
   }

   private boolean _sweepImpl(long unitDir, float maxDist, long geom0, long pose0, long geom1, long pose1, long sweepHit, long hitFlags, float inflation) {
      return this.sweepImpl(
         PxVec3.wrapPointer(unitDir),
         maxDist,
         PxGeometry.wrapPointer(geom0),
         PxTransform.wrapPointer(pose0),
         PxGeometry.wrapPointer(geom1),
         PxTransform.wrapPointer(pose1),
         PxGeomSweepHit.wrapPointer(sweepHit),
         PxHitFlags.wrapPointer(hitFlags),
         inflation
      );
   }

   @Override
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
      return false;
   }

   private void _computeMassPropertiesImpl(long geometry, long massProperties) {
      this.computeMassPropertiesImpl(PxGeometry.wrapPointer(geometry), PxMassProperties.wrapPointer(massProperties));
   }

   @Override
   public void computeMassPropertiesImpl(PxGeometry geometry, PxMassProperties massProperties) {
   }

   private boolean _usePersistentContactManifoldImpl(long geometry) {
      return this.usePersistentContactManifoldImpl(PxGeometry.wrapPointer(geometry));
   }

   @Override
   public boolean usePersistentContactManifoldImpl(PxGeometry geometry) {
      return false;
   }

   static {
      PlatformChecks.requirePlatform(15, "physx.geometry.SimpleCustomGeometryCallbacksImpl");
   }
}
