package physx.geometry;

import physx.NativeObject;
import physx.common.PxBounds3;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxHitFlags;
import physx.physics.PxRaycastHit;
import physx.physics.PxSweepHit;

public class PxGeometryQuery extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGeometryQuery() {
   }

   private static native int __sizeOf();

   public static PxGeometryQuery wrapPointer(long address) {
      return address != 0L ? new PxGeometryQuery(address) : null;
   }

   public static PxGeometryQuery arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGeometryQuery(long address) {
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

   public static boolean sweep(PxVec3 unitDir, float maxDist, PxGeometry geom0, PxTransform pose0, PxGeometry geom1, PxTransform pose1, PxSweepHit sweepHit) {
      return _sweep(unitDir.getAddress(), maxDist, geom0.getAddress(), pose0.getAddress(), geom1.getAddress(), pose1.getAddress(), sweepHit.getAddress());
   }

   private static native boolean _sweep(long var0, float var2, long var3, long var5, long var7, long var9, long var11);

   public static boolean sweep(
      PxVec3 unitDir, float maxDist, PxGeometry geom0, PxTransform pose0, PxGeometry geom1, PxTransform pose1, PxSweepHit sweepHit, PxHitFlags hitFlags
   ) {
      return _sweep(
         unitDir.getAddress(),
         maxDist,
         geom0.getAddress(),
         pose0.getAddress(),
         geom1.getAddress(),
         pose1.getAddress(),
         sweepHit.getAddress(),
         hitFlags.getAddress()
      );
   }

   private static native boolean _sweep(long var0, float var2, long var3, long var5, long var7, long var9, long var11, long var13);

   public static boolean sweep(
      PxVec3 unitDir,
      float maxDist,
      PxGeometry geom0,
      PxTransform pose0,
      PxGeometry geom1,
      PxTransform pose1,
      PxSweepHit sweepHit,
      PxHitFlags hitFlags,
      float inflation
   ) {
      return _sweep(
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

   private static native boolean _sweep(long var0, float var2, long var3, long var5, long var7, long var9, long var11, long var13, float var15);

   public static boolean overlap(PxGeometry geom0, PxTransform pose0, PxGeometry geom1, PxTransform pose1) {
      return _overlap(geom0.getAddress(), pose0.getAddress(), geom1.getAddress(), pose1.getAddress());
   }

   private static native boolean _overlap(long var0, long var2, long var4, long var6);

   public static int raycast(
      PxVec3 origin, PxVec3 unitDir, PxGeometry geom, PxTransform pose, float maxDist, PxHitFlags hitFlags, int maxHits, PxRaycastHit rayHits
   ) {
      return _raycast(
         origin.getAddress(), unitDir.getAddress(), geom.getAddress(), pose.getAddress(), maxDist, hitFlags.getAddress(), maxHits, rayHits.getAddress()
      );
   }

   private static native int _raycast(long var0, long var2, long var4, long var6, float var8, long var9, int var11, long var12);

   public static float pointDistance(PxVec3 point, PxGeometry geom, PxTransform pose) {
      return _pointDistance(point.getAddress(), geom.getAddress(), pose.getAddress());
   }

   private static native float _pointDistance(long var0, long var2, long var4);

   public static float pointDistance(PxVec3 point, PxGeometry geom, PxTransform pose, PxVec3 closestPoint) {
      return _pointDistance(point.getAddress(), geom.getAddress(), pose.getAddress(), closestPoint.getAddress());
   }

   private static native float _pointDistance(long var0, long var2, long var4, long var6);

   public static void computeGeomBounds(PxBounds3 bounds, PxGeometry geom, PxTransform pose) {
      _computeGeomBounds(bounds.getAddress(), geom.getAddress(), pose.getAddress());
   }

   private static native void _computeGeomBounds(long var0, long var2, long var4);

   public static void computeGeomBounds(PxBounds3 bounds, PxGeometry geom, PxTransform pose, float inflation) {
      _computeGeomBounds(bounds.getAddress(), geom.getAddress(), pose.getAddress(), inflation);
   }

   private static native void _computeGeomBounds(long var0, long var2, long var4, float var6);

   public static boolean isValid(PxGeometry geom) {
      return _isValid(geom.getAddress());
   }

   private static native boolean _isValid(long var0);
}
