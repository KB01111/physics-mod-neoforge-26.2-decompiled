package physx.physics;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxBaseTask;
import physx.common.PxBounds3;
import physx.common.PxCollection;
import physx.common.PxCpuDispatcher;
import physx.common.PxCudaContextManager;
import physx.common.PxRenderBuffer;
import physx.common.PxVec3;
import physx.geometry.PxBVH;
import physx.particles.PxParticleSolverTypeEnum;
import physx.support.PxActorPtr;
import physx.support.PxConstraintPtr;
import physx.support.PxVisualizationParameterEnum;

public class PxScene extends PxSceneSQSystem {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxScene() {
   }

   private static native int __sizeOf();

   public static PxScene wrapPointer(long address) {
      return address != 0L ? new PxScene(address) : null;
   }

   public static PxScene arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxScene(long address) {
      super(address);
   }

   public NativeObject getUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getUserData(this.address));
   }

   private static native long _getUserData(long var0);

   public void setUserData(NativeObject value) {
      this.checkNotNull();
      _setUserData(this.address, value.getAddress());
   }

   private static native void _setUserData(long var0, long var2);

   public boolean addActor(PxActor actor) {
      this.checkNotNull();
      return _addActor(this.address, actor.getAddress());
   }

   private static native boolean _addActor(long var0, long var2);

   public boolean addActor(PxActor actor, PxBVH bvh) {
      this.checkNotNull();
      return _addActor(this.address, actor.getAddress(), bvh.getAddress());
   }

   private static native boolean _addActor(long var0, long var2, long var4);

   public void removeActor(PxActor actor) {
      this.checkNotNull();
      _removeActor(this.address, actor.getAddress());
   }

   private static native void _removeActor(long var0, long var2);

   public void removeActor(PxActor actor, boolean wakeOnLostTouch) {
      this.checkNotNull();
      _removeActor(this.address, actor.getAddress(), wakeOnLostTouch);
   }

   private static native void _removeActor(long var0, long var2, boolean var4);

   public boolean addAggregate(PxAggregate aggregate) {
      this.checkNotNull();
      return _addAggregate(this.address, aggregate.getAddress());
   }

   private static native boolean _addAggregate(long var0, long var2);

   public void removeAggregate(PxAggregate aggregate) {
      this.checkNotNull();
      _removeAggregate(this.address, aggregate.getAddress());
   }

   private static native void _removeAggregate(long var0, long var2);

   public void removeAggregate(PxAggregate aggregate, boolean wakeOnLostTouch) {
      this.checkNotNull();
      _removeAggregate(this.address, aggregate.getAddress(), wakeOnLostTouch);
   }

   private static native void _removeAggregate(long var0, long var2, boolean var4);

   public boolean addCollection(PxCollection collection) {
      this.checkNotNull();
      return _addCollection(this.address, collection.getAddress());
   }

   private static native boolean _addCollection(long var0, long var2);

   public float getWakeCounterResetValue() {
      this.checkNotNull();
      return _getWakeCounterResetValue(this.address);
   }

   private static native float _getWakeCounterResetValue(long var0);

   public void shiftOrigin(PxVec3 shift) {
      this.checkNotNull();
      _shiftOrigin(this.address, shift.getAddress());
   }

   private static native void _shiftOrigin(long var0, long var2);

   public boolean addArticulation(PxArticulationReducedCoordinate articulation) {
      this.checkNotNull();
      return _addArticulation(this.address, articulation.getAddress());
   }

   private static native boolean _addArticulation(long var0, long var2);

   public void removeArticulation(PxArticulationReducedCoordinate articulation) {
      this.checkNotNull();
      _removeArticulation(this.address, articulation.getAddress());
   }

   private static native void _removeArticulation(long var0, long var2);

   public void removeArticulation(PxArticulationReducedCoordinate articulation, boolean wakeOnLostTouch) {
      this.checkNotNull();
      _removeArticulation(this.address, articulation.getAddress(), wakeOnLostTouch);
   }

   private static native void _removeArticulation(long var0, long var2, boolean var4);

   public int getNbActors(PxActorTypeFlags types) {
      this.checkNotNull();
      return _getNbActors(this.address, types.getAddress());
   }

   private static native int _getNbActors(long var0, long var2);

   public int getActors(PxActorTypeFlags types, PxActorPtr userBuffer, int bufferSize, int startIndex) {
      this.checkNotNull();
      return _getActors(this.address, types.getAddress(), userBuffer.getAddress(), bufferSize, startIndex);
   }

   private static native int _getActors(long var0, long var2, long var4, int var6, int var7);

   public int getConstraints(PxConstraintPtr userBuffer, int bufferSize, int startIndex) {
      this.checkNotNull();
      return _getConstraints(this.address, userBuffer.getAddress(), bufferSize, startIndex);
   }

   private static native int _getConstraints(long var0, long var2, int var4, int var5);

   public int getNbSoftBodies() {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxScene");
      return _getNbSoftBodies(this.address);
   }

   private static native int _getNbSoftBodies(long var0);

   @Deprecated
   public int getNbParticleSystems(PxParticleSolverTypeEnum type) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxScene");
      return _getNbParticleSystems(this.address, type.value);
   }

   private static native int _getNbParticleSystems(long var0, int var2);

   public int getNbArticulations() {
      this.checkNotNull();
      return _getNbArticulations(this.address);
   }

   private static native int _getNbArticulations(long var0);

   public int getNbConstraints() {
      this.checkNotNull();
      return _getNbConstraints(this.address);
   }

   private static native int _getNbConstraints(long var0);

   public int getNbAggregates() {
      this.checkNotNull();
      return _getNbAggregates(this.address);
   }

   private static native int _getNbAggregates(long var0);

   public void setDominanceGroupPair(byte group1, byte group2, PxDominanceGroupPair dominance) {
      this.checkNotNull();
      _setDominanceGroupPair(this.address, group1, group2, dominance.getAddress());
   }

   private static native void _setDominanceGroupPair(long var0, byte var2, byte var3, long var4);

   public PxDominanceGroupPair getDominanceGroupPair(byte group1, byte group2) {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxScene");
      return PxDominanceGroupPair.wrapPointer(_getDominanceGroupPair(this.address, group1, group2));
   }

   private static native long _getDominanceGroupPair(long var0, byte var2, byte var3);

   public PxCpuDispatcher getCpuDispatcher() {
      this.checkNotNull();
      return PxCpuDispatcher.wrapPointer(_getCpuDispatcher(this.address));
   }

   private static native long _getCpuDispatcher(long var0);

   public PxCudaContextManager getCudaContextManager() {
      this.checkNotNull();
      PlatformChecks.requirePlatform(3, "physx.physics.PxScene");
      return PxCudaContextManager.wrapPointer(_getCudaContextManager(this.address));
   }

   private static native long _getCudaContextManager(long var0);

   public byte createClient() {
      this.checkNotNull();
      return _createClient(this.address);
   }

   private static native byte _createClient(long var0);

   public void setSimulationEventCallback(PxSimulationEventCallback callback) {
      this.checkNotNull();
      _setSimulationEventCallback(this.address, callback.getAddress());
   }

   private static native void _setSimulationEventCallback(long var0, long var2);

   public PxSimulationEventCallback getSimulationEventCallback() {
      this.checkNotNull();
      return PxSimulationEventCallback.wrapPointer(_getSimulationEventCallback(this.address));
   }

   private static native long _getSimulationEventCallback(long var0);

   public void setFilterShaderData(NativeObject data, int dataSize) {
      this.checkNotNull();
      _setFilterShaderData(this.address, data.getAddress(), dataSize);
   }

   private static native void _setFilterShaderData(long var0, long var2, int var4);

   public NativeObject getFilterShaderData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getFilterShaderData(this.address));
   }

   private static native long _getFilterShaderData(long var0);

   public int getFilterShaderDataSize() {
      this.checkNotNull();
      return _getFilterShaderDataSize(this.address);
   }

   private static native int _getFilterShaderDataSize(long var0);

   public PxSimulationFilterShader getFilterShader() {
      this.checkNotNull();
      return PxSimulationFilterShader.wrapPointer(_getFilterShader(this.address));
   }

   private static native long _getFilterShader(long var0);

   public boolean resetFiltering(PxActor actor) {
      this.checkNotNull();
      return _resetFiltering(this.address, actor.getAddress());
   }

   private static native boolean _resetFiltering(long var0, long var2);

   public PxPairFilteringModeEnum getKinematicKinematicFilteringMode() {
      this.checkNotNull();
      return PxPairFilteringModeEnum.forValue(_getKinematicKinematicFilteringMode(this.address));
   }

   private static native int _getKinematicKinematicFilteringMode(long var0);

   public PxPairFilteringModeEnum getStaticKinematicFilteringMode() {
      this.checkNotNull();
      return PxPairFilteringModeEnum.forValue(_getStaticKinematicFilteringMode(this.address));
   }

   private static native int _getStaticKinematicFilteringMode(long var0);

   public boolean simulate(float elapsedTime) {
      this.checkNotNull();
      return _simulate(this.address, elapsedTime);
   }

   private static native boolean _simulate(long var0, float var2);

   public boolean simulate(float elapsedTime, PxBaseTask completionTask) {
      this.checkNotNull();
      return _simulate(this.address, elapsedTime, completionTask.getAddress());
   }

   private static native boolean _simulate(long var0, float var2, long var3);

   public boolean simulate(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock) {
      this.checkNotNull();
      return _simulate(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress());
   }

   private static native boolean _simulate(long var0, float var2, long var3, long var5);

   public boolean simulate(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock, int scratchMemBlockSize) {
      this.checkNotNull();
      return _simulate(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress(), scratchMemBlockSize);
   }

   private static native boolean _simulate(long var0, float var2, long var3, long var5, int var7);

   public boolean simulate(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock, int scratchMemBlockSize, boolean controlSimulation) {
      this.checkNotNull();
      return _simulate(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress(), scratchMemBlockSize, controlSimulation);
   }

   private static native boolean _simulate(long var0, float var2, long var3, long var5, int var7, boolean var8);

   public boolean advance() {
      this.checkNotNull();
      return _advance(this.address);
   }

   private static native boolean _advance(long var0);

   public boolean advance(PxBaseTask completionTask) {
      this.checkNotNull();
      return _advance(this.address, completionTask.getAddress());
   }

   private static native boolean _advance(long var0, long var2);

   public boolean collide(float elapsedTime) {
      this.checkNotNull();
      return _collide(this.address, elapsedTime);
   }

   private static native boolean _collide(long var0, float var2);

   public boolean collide(float elapsedTime, PxBaseTask completionTask) {
      this.checkNotNull();
      return _collide(this.address, elapsedTime, completionTask.getAddress());
   }

   private static native boolean _collide(long var0, float var2, long var3);

   public boolean collide(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock) {
      this.checkNotNull();
      return _collide(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress());
   }

   private static native boolean _collide(long var0, float var2, long var3, long var5);

   public boolean collide(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock, int scratchMemBlockSize) {
      this.checkNotNull();
      return _collide(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress(), scratchMemBlockSize);
   }

   private static native boolean _collide(long var0, float var2, long var3, long var5, int var7);

   public boolean collide(float elapsedTime, PxBaseTask completionTask, NativeObject scratchMemBlock, int scratchMemBlockSize, boolean controlSimulation) {
      this.checkNotNull();
      return _collide(this.address, elapsedTime, completionTask.getAddress(), scratchMemBlock.getAddress(), scratchMemBlockSize, controlSimulation);
   }

   private static native boolean _collide(long var0, float var2, long var3, long var5, int var7, boolean var8);

   public boolean checkResults() {
      this.checkNotNull();
      return _checkResults(this.address);
   }

   private static native boolean _checkResults(long var0);

   public boolean checkResults(boolean block) {
      this.checkNotNull();
      return _checkResults(this.address, block);
   }

   private static native boolean _checkResults(long var0, boolean var2);

   public boolean fetchCollision() {
      this.checkNotNull();
      return _fetchCollision(this.address);
   }

   private static native boolean _fetchCollision(long var0);

   public boolean fetchCollision(boolean block) {
      this.checkNotNull();
      return _fetchCollision(this.address, block);
   }

   private static native boolean _fetchCollision(long var0, boolean var2);

   public boolean fetchResults() {
      this.checkNotNull();
      return _fetchResults(this.address);
   }

   private static native boolean _fetchResults(long var0);

   public boolean fetchResults(boolean block) {
      this.checkNotNull();
      return _fetchResults(this.address, block);
   }

   private static native boolean _fetchResults(long var0, boolean var2);

   public void processCallbacks(PxBaseTask continuation) {
      this.checkNotNull();
      _processCallbacks(this.address, continuation.getAddress());
   }

   private static native void _processCallbacks(long var0, long var2);

   public void fetchResultsParticleSystem() {
      this.checkNotNull();
      _fetchResultsParticleSystem(this.address);
   }

   private static native void _fetchResultsParticleSystem(long var0);

   public void flushSimulation() {
      this.checkNotNull();
      _flushSimulation(this.address);
   }

   private static native void _flushSimulation(long var0);

   public void flushSimulation(boolean sendPendingReports) {
      this.checkNotNull();
      _flushSimulation(this.address, sendPendingReports);
   }

   private static native void _flushSimulation(long var0, boolean var2);

   public void setGravity(PxVec3 vec) {
      this.checkNotNull();
      _setGravity(this.address, vec.getAddress());
   }

   private static native void _setGravity(long var0, long var2);

   public PxVec3 getGravity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getGravity(this.address));
   }

   private static native long _getGravity(long var0);

   public void setBounceThresholdVelocity(float t) {
      this.checkNotNull();
      _setBounceThresholdVelocity(this.address, t);
   }

   private static native void _setBounceThresholdVelocity(long var0, float var2);

   public float getBounceThresholdVelocity() {
      this.checkNotNull();
      return _getBounceThresholdVelocity(this.address);
   }

   private static native float _getBounceThresholdVelocity(long var0);

   public void setCCDMaxPasses(int ccdMaxPasses) {
      this.checkNotNull();
      _setCCDMaxPasses(this.address, ccdMaxPasses);
   }

   private static native void _setCCDMaxPasses(long var0, int var2);

   public int getCCDMaxPasses() {
      this.checkNotNull();
      return _getCCDMaxPasses(this.address);
   }

   private static native int _getCCDMaxPasses(long var0);

   public void setCCDMaxSeparation(float t) {
      this.checkNotNull();
      _setCCDMaxSeparation(this.address, t);
   }

   private static native void _setCCDMaxSeparation(long var0, float var2);

   public float getCCDMaxSeparation() {
      this.checkNotNull();
      return _getCCDMaxSeparation(this.address);
   }

   private static native float _getCCDMaxSeparation(long var0);

   public void setCCDThreshold(float t) {
      this.checkNotNull();
      _setCCDThreshold(this.address, t);
   }

   private static native void _setCCDThreshold(long var0, float var2);

   public float getCCDThreshold() {
      this.checkNotNull();
      return _getCCDThreshold(this.address);
   }

   private static native float _getCCDThreshold(long var0);

   public void setMaxBiasCoefficient(float t) {
      this.checkNotNull();
      _setMaxBiasCoefficient(this.address, t);
   }

   private static native void _setMaxBiasCoefficient(long var0, float var2);

   public float getMaxBiasCoefficient() {
      this.checkNotNull();
      return _getMaxBiasCoefficient(this.address);
   }

   private static native float _getMaxBiasCoefficient(long var0);

   public void setFrictionOffsetThreshold(float t) {
      this.checkNotNull();
      _setFrictionOffsetThreshold(this.address, t);
   }

   private static native void _setFrictionOffsetThreshold(long var0, float var2);

   public float getFrictionOffsetThreshold() {
      this.checkNotNull();
      return _getFrictionOffsetThreshold(this.address);
   }

   private static native float _getFrictionOffsetThreshold(long var0);

   public void setFrictionCorrelationDistance(float t) {
      this.checkNotNull();
      _setFrictionCorrelationDistance(this.address, t);
   }

   private static native void _setFrictionCorrelationDistance(long var0, float var2);

   public float getFrictionCorrelationDistance() {
      this.checkNotNull();
      return _getFrictionCorrelationDistance(this.address);
   }

   private static native float _getFrictionCorrelationDistance(long var0);

   public PxFrictionTypeEnum getFrictionType() {
      this.checkNotNull();
      return PxFrictionTypeEnum.forValue(_getFrictionType(this.address));
   }

   private static native int _getFrictionType(long var0);

   public PxSolverTypeEnum getSolverType() {
      this.checkNotNull();
      return PxSolverTypeEnum.forValue(_getSolverType(this.address));
   }

   private static native int _getSolverType(long var0);

   public PxRenderBuffer getRenderBuffer() {
      this.checkNotNull();
      return PxRenderBuffer.wrapPointer(_getRenderBuffer(this.address));
   }

   private static native long _getRenderBuffer(long var0);

   public boolean setVisualizationParameter(PxVisualizationParameterEnum param, float value) {
      this.checkNotNull();
      return _setVisualizationParameter(this.address, param.value, value);
   }

   private static native boolean _setVisualizationParameter(long var0, int var2, float var3);

   public float getVisualizationParameter(PxVisualizationParameterEnum paramEnum) {
      this.checkNotNull();
      return _getVisualizationParameter(this.address, paramEnum.value);
   }

   private static native float _getVisualizationParameter(long var0, int var2);

   public void setVisualizationCullingBox(PxBounds3 box) {
      this.checkNotNull();
      _setVisualizationCullingBox(this.address, box.getAddress());
   }

   private static native void _setVisualizationCullingBox(long var0, long var2);

   public PxBounds3 getVisualizationCullingBox() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getVisualizationCullingBox(this.address));
   }

   private static native long _getVisualizationCullingBox(long var0);

   public void getSimulationStatistics(PxSimulationStatistics stats) {
      this.checkNotNull();
      _getSimulationStatistics(this.address, stats.getAddress());
   }

   private static native void _getSimulationStatistics(long var0, long var2);

   public PxBroadPhaseTypeEnum getBroadPhaseType() {
      this.checkNotNull();
      return PxBroadPhaseTypeEnum.forValue(_getBroadPhaseType(this.address));
   }

   private static native int _getBroadPhaseType(long var0);

   public boolean getBroadPhaseCaps(PxBroadPhaseCaps caps) {
      this.checkNotNull();
      return _getBroadPhaseCaps(this.address, caps.getAddress());
   }

   private static native boolean _getBroadPhaseCaps(long var0, long var2);

   public int getNbBroadPhaseRegions() {
      this.checkNotNull();
      return _getNbBroadPhaseRegions(this.address);
   }

   private static native int _getNbBroadPhaseRegions(long var0);

   public int getBroadPhaseRegions(PxBroadPhaseRegionInfo userBuffer, int bufferSize) {
      this.checkNotNull();
      return _getBroadPhaseRegions(this.address, userBuffer.getAddress(), bufferSize);
   }

   private static native int _getBroadPhaseRegions(long var0, long var2, int var4);

   public int getBroadPhaseRegions(PxBroadPhaseRegionInfo userBuffer, int bufferSize, int startIndex) {
      this.checkNotNull();
      return _getBroadPhaseRegions(this.address, userBuffer.getAddress(), bufferSize, startIndex);
   }

   private static native int _getBroadPhaseRegions(long var0, long var2, int var4, int var5);

   public int addBroadPhaseRegion(PxBroadPhaseRegion region) {
      this.checkNotNull();
      return _addBroadPhaseRegion(this.address, region.getAddress());
   }

   private static native int _addBroadPhaseRegion(long var0, long var2);

   public int addBroadPhaseRegion(PxBroadPhaseRegion region, boolean populateRegion) {
      this.checkNotNull();
      return _addBroadPhaseRegion(this.address, region.getAddress(), populateRegion);
   }

   private static native int _addBroadPhaseRegion(long var0, long var2, boolean var4);

   public boolean removeBroadPhaseRegion(int handle) {
      this.checkNotNull();
      return _removeBroadPhaseRegion(this.address, handle);
   }

   private static native boolean _removeBroadPhaseRegion(long var0, int var2);

   public void lockRead() {
      this.checkNotNull();
      _lockRead(this.address);
   }

   private static native void _lockRead(long var0);

   public void lockRead(String file) {
      this.checkNotNull();
      _lockRead(this.address, file);
   }

   private static native void _lockRead(long var0, String var2);

   public void lockRead(String file, int line) {
      this.checkNotNull();
      _lockRead(this.address, file, line);
   }

   private static native void _lockRead(long var0, String var2, int var3);

   public void unlockRead() {
      this.checkNotNull();
      _unlockRead(this.address);
   }

   private static native void _unlockRead(long var0);

   public void lockWrite() {
      this.checkNotNull();
      _lockWrite(this.address);
   }

   private static native void _lockWrite(long var0);

   public void lockWrite(String file) {
      this.checkNotNull();
      _lockWrite(this.address, file);
   }

   private static native void _lockWrite(long var0, String var2);

   public void lockWrite(String file, int line) {
      this.checkNotNull();
      _lockWrite(this.address, file, line);
   }

   private static native void _lockWrite(long var0, String var2, int var3);

   public void unlockWrite() {
      this.checkNotNull();
      _unlockWrite(this.address);
   }

   private static native void _unlockWrite(long var0);

   public void setNbContactDataBlocks(int numBlocks) {
      this.checkNotNull();
      _setNbContactDataBlocks(this.address, numBlocks);
   }

   private static native void _setNbContactDataBlocks(long var0, int var2);

   public int getNbContactDataBlocksUsed() {
      this.checkNotNull();
      return _getNbContactDataBlocksUsed(this.address);
   }

   private static native int _getNbContactDataBlocksUsed(long var0);

   public int getMaxNbContactDataBlocksUsed() {
      this.checkNotNull();
      return _getMaxNbContactDataBlocksUsed(this.address);
   }

   private static native int _getMaxNbContactDataBlocksUsed(long var0);

   public int getContactReportStreamBufferSize() {
      this.checkNotNull();
      return _getContactReportStreamBufferSize(this.address);
   }

   private static native int _getContactReportStreamBufferSize(long var0);

   public void setSolverBatchSize(int solverBatchSize) {
      this.checkNotNull();
      _setSolverBatchSize(this.address, solverBatchSize);
   }

   private static native void _setSolverBatchSize(long var0, int var2);

   public int getSolverBatchSize() {
      this.checkNotNull();
      return _getSolverBatchSize(this.address);
   }

   private static native int _getSolverBatchSize(long var0);

   public void setSolverArticulationBatchSize(int solverBatchSize) {
      this.checkNotNull();
      _setSolverArticulationBatchSize(this.address, solverBatchSize);
   }

   private static native void _setSolverArticulationBatchSize(long var0, int var2);

   public int getSolverArticulationBatchSize() {
      this.checkNotNull();
      return _getSolverArticulationBatchSize(this.address);
   }

   private static native int _getSolverArticulationBatchSize(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public void setFlag(PxSceneFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setFlag(this.address, flag.value, value);
   }

   private static native void _setFlag(long var0, int var2, boolean var3);

   public PxSceneFlags getFlags() {
      this.checkNotNull();
      return PxSceneFlags.wrapPointer(_getFlags(this.address));
   }

   private static native long _getFlags(long var0);

   public void setLimits(PxSceneLimits limits) {
      this.checkNotNull();
      _setLimits(this.address, limits.getAddress());
   }

   private static native void _setLimits(long var0, long var2);

   public PxSceneLimits getLimits() {
      this.checkNotNull();
      return PxSceneLimits.wrapPointer(_getLimits(this.address));
   }

   private static native long _getLimits(long var0);

   public PxPhysics getPhysics() {
      this.checkNotNull();
      return PxPhysics.wrapPointer(_getPhysics(this.address));
   }

   private static native long _getPhysics(long var0);

   public int getTimestamp() {
      this.checkNotNull();
      return _getTimestamp(this.address);
   }

   private static native int _getTimestamp(long var0);
}
