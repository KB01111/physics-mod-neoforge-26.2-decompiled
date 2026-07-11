package physx.character;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.physics.PxScene;

public class PxControllerManager extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxControllerManager() {
   }

   private static native int __sizeOf();

   public static PxControllerManager wrapPointer(long address) {
      return address != 0L ? new PxControllerManager(address) : null;
   }

   public static PxControllerManager arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerManager(long address) {
      super(address);
   }

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public int getNbControllers() {
      this.checkNotNull();
      return _getNbControllers(this.address);
   }

   private static native int _getNbControllers(long var0);

   public PxController getController(int index) {
      this.checkNotNull();
      return PxController.wrapPointer(_getController(this.address, index));
   }

   private static native long _getController(long var0, int var2);

   public PxController createController(PxControllerDesc desc) {
      this.checkNotNull();
      return PxController.wrapPointer(_createController(this.address, desc.getAddress()));
   }

   private static native long _createController(long var0, long var2);

   public void purgeControllers() {
      this.checkNotNull();
      _purgeControllers(this.address);
   }

   private static native void _purgeControllers(long var0);

   public int getNbObstacleContexts() {
      this.checkNotNull();
      return _getNbObstacleContexts(this.address);
   }

   private static native int _getNbObstacleContexts(long var0);

   public PxObstacleContext getObstacleContext(int index) {
      this.checkNotNull();
      return PxObstacleContext.wrapPointer(_getObstacleContext(this.address, index));
   }

   private static native long _getObstacleContext(long var0, int var2);

   public PxObstacleContext createObstacleContext() {
      this.checkNotNull();
      return PxObstacleContext.wrapPointer(_createObstacleContext(this.address));
   }

   private static native long _createObstacleContext(long var0);

   public void computeInteractions(float elapsedTime) {
      this.checkNotNull();
      _computeInteractions(this.address, elapsedTime);
   }

   private static native void _computeInteractions(long var0, float var2);

   public void setTessellation(boolean flag, float maxEdgeLength) {
      this.checkNotNull();
      _setTessellation(this.address, flag, maxEdgeLength);
   }

   private static native void _setTessellation(long var0, boolean var2, float var3);

   public void setOverlapRecoveryModule(boolean flag) {
      this.checkNotNull();
      _setOverlapRecoveryModule(this.address, flag);
   }

   private static native void _setOverlapRecoveryModule(long var0, boolean var2);

   public void setPreciseSweeps(boolean flags) {
      this.checkNotNull();
      _setPreciseSweeps(this.address, flags);
   }

   private static native void _setPreciseSweeps(long var0, boolean var2);

   public void setPreventVerticalSlidingAgainstCeiling(boolean flag) {
      this.checkNotNull();
      _setPreventVerticalSlidingAgainstCeiling(this.address, flag);
   }

   private static native void _setPreventVerticalSlidingAgainstCeiling(long var0, boolean var2);

   public void shiftOrigin(PxVec3 shift) {
      this.checkNotNull();
      _shiftOrigin(this.address, shift.getAddress());
   }

   private static native void _shiftOrigin(long var0, long var2);
}
