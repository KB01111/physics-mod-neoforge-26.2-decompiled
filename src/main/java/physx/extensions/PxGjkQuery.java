package physx.extensions;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;

public class PxGjkQuery extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxGjkQuery() {
   }

   private static native int __sizeOf();

   public static PxGjkQuery wrapPointer(long address) {
      return address != 0L ? new PxGjkQuery(address) : null;
   }

   public static PxGjkQuery arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGjkQuery(long address) {
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

   public static boolean proximityInfo(
      Support a, Support b, PxTransform poseA, PxTransform poseB, float contactDistance, float toleranceLength, PxGjkQueryProximityInfoResult result
   ) {
      return _proximityInfo(a.getAddress(), b.getAddress(), poseA.getAddress(), poseB.getAddress(), contactDistance, toleranceLength, result.getAddress());
   }

   private static native boolean _proximityInfo(long var0, long var2, long var4, long var6, float var8, float var9, long var10);

   public static boolean raycast(Support shape, PxTransform pose, PxVec3 rayStart, PxVec3 unitDir, float maxDist, PxGjkQueryRaycastResult result) {
      return _raycast(shape.getAddress(), pose.getAddress(), rayStart.getAddress(), unitDir.getAddress(), maxDist, result.getAddress());
   }

   private static native boolean _raycast(long var0, long var2, long var4, long var6, float var8, long var9);

   public static boolean overlap(Support a, Support b, PxTransform poseA, PxTransform poseB) {
      return _overlap(a.getAddress(), b.getAddress(), poseA.getAddress(), poseB.getAddress());
   }

   private static native boolean _overlap(long var0, long var2, long var4, long var6);

   public static boolean sweep(Support a, Support b, PxTransform poseA, PxTransform poseB, PxVec3 unitDir, float maxDist, PxGjkQuerySweepResult result) {
      return _sweep(a.getAddress(), b.getAddress(), poseA.getAddress(), poseB.getAddress(), unitDir.getAddress(), maxDist, result.getAddress());
   }

   private static native boolean _sweep(long var0, long var2, long var4, long var6, long var8, float var10, long var11);
}
