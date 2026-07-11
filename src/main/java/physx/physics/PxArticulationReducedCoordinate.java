package physx.physics;

import physx.common.PxBase;
import physx.common.PxBounds3;
import physx.common.PxTransform;
import physx.common.PxVec3;

public class PxArticulationReducedCoordinate extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationReducedCoordinate() {
   }

   private static native int __sizeOf();

   public static PxArticulationReducedCoordinate wrapPointer(long address) {
      return address != 0L ? new PxArticulationReducedCoordinate(address) : null;
   }

   public static PxArticulationReducedCoordinate arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationReducedCoordinate(long address) {
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

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public void setSolverIterationCounts(int minPositionIters) {
      this.checkNotNull();
      _setSolverIterationCounts(this.address, minPositionIters);
   }

   private static native void _setSolverIterationCounts(long var0, int var2);

   public void setSolverIterationCounts(int minPositionIters, int minVelocityIters) {
      this.checkNotNull();
      _setSolverIterationCounts(this.address, minPositionIters, minVelocityIters);
   }

   private static native void _setSolverIterationCounts(long var0, int var2, int var3);

   public boolean isSleeping() {
      this.checkNotNull();
      return _isSleeping(this.address);
   }

   private static native boolean _isSleeping(long var0);

   public void setSleepThreshold(float threshold) {
      this.checkNotNull();
      _setSleepThreshold(this.address, threshold);
   }

   private static native void _setSleepThreshold(long var0, float var2);

   public float getSleepThreshold() {
      this.checkNotNull();
      return _getSleepThreshold(this.address);
   }

   private static native float _getSleepThreshold(long var0);

   public void setStabilizationThreshold(float threshold) {
      this.checkNotNull();
      _setStabilizationThreshold(this.address, threshold);
   }

   private static native void _setStabilizationThreshold(long var0, float var2);

   public float getStabilizationThreshold() {
      this.checkNotNull();
      return _getStabilizationThreshold(this.address);
   }

   private static native float _getStabilizationThreshold(long var0);

   public void setWakeCounter(float wakeCounterValue) {
      this.checkNotNull();
      _setWakeCounter(this.address, wakeCounterValue);
   }

   private static native void _setWakeCounter(long var0, float var2);

   public float getWakeCounter() {
      this.checkNotNull();
      return _getWakeCounter(this.address);
   }

   private static native float _getWakeCounter(long var0);

   public void wakeUp() {
      this.checkNotNull();
      _wakeUp(this.address);
   }

   private static native void _wakeUp(long var0);

   public void putToSleep() {
      this.checkNotNull();
      _putToSleep(this.address);
   }

   private static native void _putToSleep(long var0);

   @Deprecated
   public void setMaxCOMLinearVelocity(float maxLinerVelocity) {
      this.checkNotNull();
      _setMaxCOMLinearVelocity(this.address, maxLinerVelocity);
   }

   private static native void _setMaxCOMLinearVelocity(long var0, float var2);

   @Deprecated
   public float getMaxCOMLinearVelocity() {
      this.checkNotNull();
      return _getMaxCOMLinearVelocity(this.address);
   }

   private static native float _getMaxCOMLinearVelocity(long var0);

   @Deprecated
   public void setMaxCOMAngularVelocity(float maxAngularVelocity) {
      this.checkNotNull();
      _setMaxCOMAngularVelocity(this.address, maxAngularVelocity);
   }

   private static native void _setMaxCOMAngularVelocity(long var0, float var2);

   @Deprecated
   public float getMaxCOMAngularVelocity() {
      this.checkNotNull();
      return _getMaxCOMAngularVelocity(this.address);
   }

   private static native float _getMaxCOMAngularVelocity(long var0);

   public PxArticulationLink createLink(PxArticulationLink parent, PxTransform pose) {
      this.checkNotNull();
      return PxArticulationLink.wrapPointer(_createLink(this.address, parent != null ? parent.getAddress() : 0L, pose.getAddress()));
   }

   private static native long _createLink(long var0, long var2, long var4);

   public int getNbLinks() {
      this.checkNotNull();
      return _getNbLinks(this.address);
   }

   private static native int _getNbLinks(long var0);

   public int getNbShapes() {
      this.checkNotNull();
      return _getNbShapes(this.address);
   }

   private static native int _getNbShapes(long var0);

   public void setName(String name) {
      this.checkNotNull();
      _setName(this.address, name);
   }

   private static native void _setName(long var0, String var2);

   public String getName() {
      this.checkNotNull();
      return _getName(this.address);
   }

   private static native String _getName(long var0);

   public PxBounds3 getWorldBounds() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getWorldBounds(this.address));
   }

   private static native long _getWorldBounds(long var0);

   public PxBounds3 getWorldBounds(float inflation) {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getWorldBounds(this.address, inflation));
   }

   private static native long _getWorldBounds(long var0, float var2);

   public PxAggregate getAggregate() {
      this.checkNotNull();
      return PxAggregate.wrapPointer(_getAggregate(this.address));
   }

   private static native long _getAggregate(long var0);

   public void setArticulationFlags(PxArticulationFlags flags) {
      this.checkNotNull();
      _setArticulationFlags(this.address, flags.getAddress());
   }

   private static native void _setArticulationFlags(long var0, long var2);

   public void setArticulationFlag(PxArticulationFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setArticulationFlag(this.address, flag.value, value);
   }

   private static native void _setArticulationFlag(long var0, int var2, boolean var3);

   public PxArticulationFlags getArticulationFlags() {
      this.checkNotNull();
      return PxArticulationFlags.wrapPointer(_getArticulationFlags(this.address));
   }

   private static native long _getArticulationFlags(long var0);

   public int getDofs() {
      this.checkNotNull();
      return _getDofs(this.address);
   }

   private static native int _getDofs(long var0);

   public PxArticulationCache createCache() {
      this.checkNotNull();
      return PxArticulationCache.wrapPointer(_createCache(this.address));
   }

   private static native long _createCache(long var0);

   public int getCacheDataSize() {
      this.checkNotNull();
      return _getCacheDataSize(this.address);
   }

   private static native int _getCacheDataSize(long var0);

   public void zeroCache(PxArticulationCache cache) {
      this.checkNotNull();
      _zeroCache(this.address, cache.getAddress());
   }

   private static native void _zeroCache(long var0, long var2);

   public void applyCache(PxArticulationCache cache, PxArticulationCacheFlags flags) {
      this.checkNotNull();
      _applyCache(this.address, cache.getAddress(), flags.getAddress());
   }

   private static native void _applyCache(long var0, long var2, long var4);

   public void applyCache(PxArticulationCache cache, PxArticulationCacheFlags flags, boolean autowake) {
      this.checkNotNull();
      _applyCache(this.address, cache.getAddress(), flags.getAddress(), autowake);
   }

   private static native void _applyCache(long var0, long var2, long var4, boolean var6);

   public void copyInternalStateToCache(PxArticulationCache cache, PxArticulationCacheFlags flags) {
      this.checkNotNull();
      _copyInternalStateToCache(this.address, cache.getAddress(), flags.getAddress());
   }

   private static native void _copyInternalStateToCache(long var0, long var2, long var4);

   public void commonInit() {
      this.checkNotNull();
      _commonInit(this.address);
   }

   private static native void _commonInit(long var0);

   @Deprecated
   public void computeGeneralizedGravityForce(PxArticulationCache cache) {
      this.checkNotNull();
      _computeGeneralizedGravityForce(this.address, cache.getAddress());
   }

   private static native void _computeGeneralizedGravityForce(long var0, long var2);

   @Deprecated
   public void computeCoriolisAndCentrifugalForce(PxArticulationCache cache) {
      this.checkNotNull();
      _computeCoriolisAndCentrifugalForce(this.address, cache.getAddress());
   }

   private static native void _computeCoriolisAndCentrifugalForce(long var0, long var2);

   public void computeGeneralizedExternalForce(PxArticulationCache cache) {
      this.checkNotNull();
      _computeGeneralizedExternalForce(this.address, cache.getAddress());
   }

   private static native void _computeGeneralizedExternalForce(long var0, long var2);

   public void computeJointAcceleration(PxArticulationCache cache) {
      this.checkNotNull();
      _computeJointAcceleration(this.address, cache.getAddress());
   }

   private static native void _computeJointAcceleration(long var0, long var2);

   public void computeJointForce(PxArticulationCache cache) {
      this.checkNotNull();
      _computeJointForce(this.address, cache.getAddress());
   }

   private static native void _computeJointForce(long var0, long var2);

   @Deprecated
   public void computeCoefficientMatrix(PxArticulationCache cache) {
      this.checkNotNull();
      _computeCoefficientMatrix(this.address, cache.getAddress());
   }

   private static native void _computeCoefficientMatrix(long var0, long var2);

   @Deprecated
   public void computeGeneralizedMassMatrix(PxArticulationCache cache) {
      this.checkNotNull();
      _computeGeneralizedMassMatrix(this.address, cache.getAddress());
   }

   private static native void _computeGeneralizedMassMatrix(long var0, long var2);

   @Deprecated
   public void addLoopJoint(PxConstraint joint) {
      this.checkNotNull();
      _addLoopJoint(this.address, joint.getAddress());
   }

   private static native void _addLoopJoint(long var0, long var2);

   @Deprecated
   public void removeLoopJoint(PxConstraint joint) {
      this.checkNotNull();
      _removeLoopJoint(this.address, joint.getAddress());
   }

   private static native void _removeLoopJoint(long var0, long var2);

   @Deprecated
   public int getNbLoopJoints() {
      this.checkNotNull();
      return _getNbLoopJoints(this.address);
   }

   private static native int _getNbLoopJoints(long var0);

   @Deprecated
   public int getCoefficientMatrixSize() {
      this.checkNotNull();
      return _getCoefficientMatrixSize(this.address);
   }

   private static native int _getCoefficientMatrixSize(long var0);

   public void setRootGlobalPose(PxTransform pose) {
      this.checkNotNull();
      _setRootGlobalPose(this.address, pose.getAddress());
   }

   private static native void _setRootGlobalPose(long var0, long var2);

   public void setRootGlobalPose(PxTransform pose, boolean autowake) {
      this.checkNotNull();
      _setRootGlobalPose(this.address, pose.getAddress(), autowake);
   }

   private static native void _setRootGlobalPose(long var0, long var2, boolean var4);

   public PxTransform getRootGlobalPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getRootGlobalPose(this.address));
   }

   private static native long _getRootGlobalPose(long var0);

   public void setRootLinearVelocity(PxVec3 linearVelocity) {
      this.checkNotNull();
      _setRootLinearVelocity(this.address, linearVelocity.getAddress());
   }

   private static native void _setRootLinearVelocity(long var0, long var2);

   public void setRootLinearVelocity(PxVec3 linearVelocity, boolean autowake) {
      this.checkNotNull();
      _setRootLinearVelocity(this.address, linearVelocity.getAddress(), autowake);
   }

   private static native void _setRootLinearVelocity(long var0, long var2, boolean var4);

   public PxVec3 getRootLinearVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getRootLinearVelocity(this.address));
   }

   private static native long _getRootLinearVelocity(long var0);

   public void setRootAngularVelocity(PxVec3 angularVelocity) {
      this.checkNotNull();
      _setRootAngularVelocity(this.address, angularVelocity.getAddress());
   }

   private static native void _setRootAngularVelocity(long var0, long var2);

   public void setRootAngularVelocity(PxVec3 angularVelocity, boolean autowake) {
      this.checkNotNull();
      _setRootAngularVelocity(this.address, angularVelocity.getAddress(), autowake);
   }

   private static native void _setRootAngularVelocity(long var0, long var2, boolean var4);

   public PxVec3 getRootAngularVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getRootAngularVelocity(this.address));
   }

   private static native long _getRootAngularVelocity(long var0);

   public PxSpatialVelocity getLinkAcceleration(int linkId) {
      this.checkNotNull();
      return PxSpatialVelocity.wrapPointer(_getLinkAcceleration(this.address, linkId));
   }

   private static native long _getLinkAcceleration(long var0, int var2);

   @Deprecated
   public int getGpuArticulationIndex() {
      this.checkNotNull();
      return _getGpuArticulationIndex(this.address);
   }

   private static native int _getGpuArticulationIndex(long var0);

   public PxArticulationSpatialTendon createSpatialTendon() {
      this.checkNotNull();
      return PxArticulationSpatialTendon.wrapPointer(_createSpatialTendon(this.address));
   }

   private static native long _createSpatialTendon(long var0);

   public PxArticulationFixedTendon createFixedTendon() {
      this.checkNotNull();
      return PxArticulationFixedTendon.wrapPointer(_createFixedTendon(this.address));
   }

   private static native long _createFixedTendon(long var0);

   public int getNbSpatialTendons() {
      this.checkNotNull();
      return _getNbSpatialTendons(this.address);
   }

   private static native int _getNbSpatialTendons(long var0);

   public int getNbFixedTendons() {
      this.checkNotNull();
      return _getNbFixedTendons(this.address);
   }

   private static native int _getNbFixedTendons(long var0);

   public void updateKinematic(PxArticulationKinematicFlags flags) {
      this.checkNotNull();
      _updateKinematic(this.address, flags.getAddress());
   }

   private static native void _updateKinematic(long var0, long var2);
}
