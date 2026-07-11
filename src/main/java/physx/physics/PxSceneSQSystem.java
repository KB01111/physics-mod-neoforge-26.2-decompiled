package physx.physics;

import physx.common.PxBaseTask;

public class PxSceneSQSystem extends PxSceneQuerySystemBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxSceneSQSystem() {
   }

   private static native int __sizeOf();

   public static PxSceneSQSystem wrapPointer(long address) {
      return address != 0L ? new PxSceneSQSystem(address) : null;
   }

   public static PxSceneSQSystem arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxSceneSQSystem(long address) {
      super(address);
   }

   public void setSceneQueryUpdateMode(PxSceneQueryUpdateModeEnum updateMode) {
      this.checkNotNull();
      _setSceneQueryUpdateMode(this.address, updateMode.value);
   }

   private static native void _setSceneQueryUpdateMode(long var0, int var2);

   public PxSceneQueryUpdateModeEnum getSceneQueryUpdateMode() {
      this.checkNotNull();
      return PxSceneQueryUpdateModeEnum.forValue(_getSceneQueryUpdateMode(this.address));
   }

   private static native int _getSceneQueryUpdateMode(long var0);

   public int getSceneQueryStaticTimestamp() {
      this.checkNotNull();
      return _getSceneQueryStaticTimestamp(this.address);
   }

   private static native int _getSceneQueryStaticTimestamp(long var0);

   public void flushQueryUpdates() {
      this.checkNotNull();
      _flushQueryUpdates(this.address);
   }

   private static native void _flushQueryUpdates(long var0);

   public void forceDynamicTreeRebuild(boolean rebuildStaticStructure, boolean rebuildDynamicStructure) {
      this.checkNotNull();
      _forceDynamicTreeRebuild(this.address, rebuildStaticStructure, rebuildDynamicStructure);
   }

   private static native void _forceDynamicTreeRebuild(long var0, boolean var2, boolean var3);

   public PxPruningStructureTypeEnum getStaticStructure() {
      this.checkNotNull();
      return PxPruningStructureTypeEnum.forValue(_getStaticStructure(this.address));
   }

   private static native int _getStaticStructure(long var0);

   public PxPruningStructureTypeEnum getDynamicStructure() {
      this.checkNotNull();
      return PxPruningStructureTypeEnum.forValue(_getDynamicStructure(this.address));
   }

   private static native int _getDynamicStructure(long var0);

   public void sceneQueriesUpdate() {
      this.checkNotNull();
      _sceneQueriesUpdate(this.address);
   }

   private static native void _sceneQueriesUpdate(long var0);

   public void sceneQueriesUpdate(PxBaseTask completionTask) {
      this.checkNotNull();
      _sceneQueriesUpdate(this.address, completionTask.getAddress());
   }

   private static native void _sceneQueriesUpdate(long var0, long var2);

   public void sceneQueriesUpdate(PxBaseTask completionTask, boolean controlSimulation) {
      this.checkNotNull();
      _sceneQueriesUpdate(this.address, completionTask.getAddress(), controlSimulation);
   }

   private static native void _sceneQueriesUpdate(long var0, long var2, boolean var4);

   public boolean checkQueries() {
      this.checkNotNull();
      return _checkQueries(this.address);
   }

   private static native boolean _checkQueries(long var0);

   public boolean checkQueries(boolean block) {
      this.checkNotNull();
      return _checkQueries(this.address, block);
   }

   private static native boolean _checkQueries(long var0, boolean var2);

   public boolean fetchQueries() {
      this.checkNotNull();
      return _fetchQueries(this.address);
   }

   private static native boolean _fetchQueries(long var0);

   public boolean fetchQueries(boolean block) {
      this.checkNotNull();
      return _fetchQueries(this.address, block);
   }

   private static native boolean _fetchQueries(long var0, boolean var2);
}
