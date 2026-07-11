package physx.physics;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxGeometry;

public class PxSceneQuerySystemBase extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSceneQuerySystemBase() {
   }

   private static native int __sizeOf();

   public static PxSceneQuerySystemBase wrapPointer(long address) {
      return address != 0L ? new PxSceneQuerySystemBase(address) : null;
   }

   public static PxSceneQuerySystemBase arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSceneQuerySystemBase(long address) {
      super(address);
   }

   public void setDynamicTreeRebuildRateHint(int dynamicTreeRebuildRateHint) {
      this.checkNotNull();
      _setDynamicTreeRebuildRateHint(this.address, dynamicTreeRebuildRateHint);
   }

   private static native void _setDynamicTreeRebuildRateHint(long var0, int var2);

   public int getDynamicTreeRebuildRateHint() {
      this.checkNotNull();
      return _getDynamicTreeRebuildRateHint(this.address);
   }

   private static native int _getDynamicTreeRebuildRateHint(long var0);

   public void forceRebuildDynamicTree(int prunerIndex) {
      this.checkNotNull();
      _forceRebuildDynamicTree(this.address, prunerIndex);
   }

   private static native void _forceRebuildDynamicTree(long var0, int var2);

   public void setUpdateMode(PxSceneQueryUpdateModeEnum updateMode) {
      this.checkNotNull();
      _setUpdateMode(this.address, updateMode.value);
   }

   private static native void _setUpdateMode(long var0, int var2);

   public PxSceneQueryUpdateModeEnum getUpdateMode() {
      this.checkNotNull();
      return PxSceneQueryUpdateModeEnum.forValue(_getUpdateMode(this.address));
   }

   private static native int _getUpdateMode(long var0);

   public int getStaticTimestamp() {
      this.checkNotNull();
      return _getStaticTimestamp(this.address);
   }

   private static native int _getStaticTimestamp(long var0);

   public void flushUpdates() {
      this.checkNotNull();
      _flushUpdates(this.address);
   }

   private static native void _flushUpdates(long var0);

   public boolean raycast(PxVec3 origin, PxVec3 unitDir, float distance, PxRaycastCallback hitCall) {
      this.checkNotNull();
      return _raycast(this.address, origin.getAddress(), unitDir.getAddress(), distance, hitCall.getAddress());
   }

   private static native boolean _raycast(long var0, long var2, long var4, float var6, long var7);

   public boolean raycast(PxVec3 origin, PxVec3 unitDir, float distance, PxRaycastCallback hitCall, PxHitFlags hitFlags) {
      this.checkNotNull();
      return _raycast(this.address, origin.getAddress(), unitDir.getAddress(), distance, hitCall.getAddress(), hitFlags.getAddress());
   }

   private static native boolean _raycast(long var0, long var2, long var4, float var6, long var7, long var9);

   public boolean raycast(PxVec3 origin, PxVec3 unitDir, float distance, PxRaycastCallback hitCall, PxHitFlags hitFlags, PxQueryFilterData filterData) {
      this.checkNotNull();
      return _raycast(this.address, origin.getAddress(), unitDir.getAddress(), distance, hitCall.getAddress(), hitFlags.getAddress(), filterData.getAddress());
   }

   private static native boolean _raycast(long var0, long var2, long var4, float var6, long var7, long var9, long var11);

   public boolean sweep(PxGeometry geometry, PxTransform pose, PxVec3 unitDir, float distance, PxSweepCallback hitCall) {
      this.checkNotNull();
      return _sweep(this.address, geometry.getAddress(), pose.getAddress(), unitDir.getAddress(), distance, hitCall.getAddress());
   }

   private static native boolean _sweep(long var0, long var2, long var4, long var6, float var8, long var9);

   public boolean sweep(PxGeometry geometry, PxTransform pose, PxVec3 unitDir, float distance, PxSweepCallback hitCall, PxHitFlags hitFlags) {
      this.checkNotNull();
      return _sweep(this.address, geometry.getAddress(), pose.getAddress(), unitDir.getAddress(), distance, hitCall.getAddress(), hitFlags.getAddress());
   }

   private static native boolean _sweep(long var0, long var2, long var4, long var6, float var8, long var9, long var11);

   public boolean sweep(
      PxGeometry geometry, PxTransform pose, PxVec3 unitDir, float distance, PxSweepCallback hitCall, PxHitFlags hitFlags, PxQueryFilterData filterData
   ) {
      this.checkNotNull();
      return _sweep(
         this.address,
         geometry.getAddress(),
         pose.getAddress(),
         unitDir.getAddress(),
         distance,
         hitCall.getAddress(),
         hitFlags.getAddress(),
         filterData.getAddress()
      );
   }

   private static native boolean _sweep(long var0, long var2, long var4, long var6, float var8, long var9, long var11, long var13);

   public boolean overlap(PxGeometry geometry, PxTransform pose, PxOverlapCallback hitCall) {
      this.checkNotNull();
      return _overlap(this.address, geometry.getAddress(), pose.getAddress(), hitCall.getAddress());
   }

   private static native boolean _overlap(long var0, long var2, long var4, long var6);

   public boolean overlap(PxGeometry geometry, PxTransform pose, PxOverlapCallback hitCall, PxQueryFilterData filterData) {
      this.checkNotNull();
      return _overlap(this.address, geometry.getAddress(), pose.getAddress(), hitCall.getAddress(), filterData.getAddress());
   }

   private static native boolean _overlap(long var0, long var2, long var4, long var6, long var8);
}
