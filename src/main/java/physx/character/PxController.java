package physx.character;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.physics.PxRigidDynamic;
import physx.physics.PxScene;

public class PxController extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxController() {
   }

   private static native int __sizeOf();

   public static PxController wrapPointer(long address) {
      return address != 0L ? new PxController(address) : null;
   }

   public static PxController arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxController(long address) {
      super(address);
   }

   public PxControllerShapeTypeEnum getType() {
      this.checkNotNull();
      return PxControllerShapeTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public PxControllerCollisionFlags move(PxVec3 disp, float minDist, float elapsedTime, PxControllerFilters filters) {
      this.checkNotNull();
      return PxControllerCollisionFlags.wrapPointer(_move(this.address, disp.getAddress(), minDist, elapsedTime, filters.getAddress()));
   }

   private static native long _move(long var0, long var2, float var4, float var5, long var6);

   public PxControllerCollisionFlags move(PxVec3 disp, float minDist, float elapsedTime, PxControllerFilters filters, PxObstacleContext obstacles) {
      this.checkNotNull();
      return PxControllerCollisionFlags.wrapPointer(_move(this.address, disp.getAddress(), minDist, elapsedTime, filters.getAddress(), obstacles.getAddress()));
   }

   private static native long _move(long var0, long var2, float var4, float var5, long var6, long var8);

   public boolean setPosition(PxExtendedVec3 position) {
      this.checkNotNull();
      return _setPosition(this.address, position.getAddress());
   }

   private static native boolean _setPosition(long var0, long var2);

   public PxExtendedVec3 getPosition() {
      this.checkNotNull();
      return PxExtendedVec3.wrapPointer(_getPosition(this.address));
   }

   private static native long _getPosition(long var0);

   public boolean setFootPosition(PxExtendedVec3 position) {
      this.checkNotNull();
      return _setFootPosition(this.address, position.getAddress());
   }

   private static native boolean _setFootPosition(long var0, long var2);

   public PxExtendedVec3 getFootPosition() {
      this.checkNotNull();
      return PxExtendedVec3.wrapPointer(_getFootPosition(this.address));
   }

   private static native long _getFootPosition(long var0);

   public PxRigidDynamic getActor() {
      this.checkNotNull();
      return PxRigidDynamic.wrapPointer(_getActor(this.address));
   }

   private static native long _getActor(long var0);

   public void setStepOffset(float offset) {
      this.checkNotNull();
      _setStepOffset(this.address, offset);
   }

   private static native void _setStepOffset(long var0, float var2);

   public float getStepOffset() {
      this.checkNotNull();
      return _getStepOffset(this.address);
   }

   private static native float _getStepOffset(long var0);

   public void setNonWalkableMode(PxControllerNonWalkableModeEnum flag) {
      this.checkNotNull();
      _setNonWalkableMode(this.address, flag.value);
   }

   private static native void _setNonWalkableMode(long var0, int var2);

   public PxControllerNonWalkableModeEnum getNonWalkableMode() {
      this.checkNotNull();
      return PxControllerNonWalkableModeEnum.forValue(_getNonWalkableMode(this.address));
   }

   private static native int _getNonWalkableMode(long var0);

   public float getContactOffset() {
      this.checkNotNull();
      return _getContactOffset(this.address);
   }

   private static native float _getContactOffset(long var0);

   public void setContactOffset(float offset) {
      this.checkNotNull();
      _setContactOffset(this.address, offset);
   }

   private static native void _setContactOffset(long var0, float var2);

   public PxVec3 getUpDirection() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getUpDirection(this.address));
   }

   private static native long _getUpDirection(long var0);

   public void setUpDirection(PxVec3 up) {
      this.checkNotNull();
      _setUpDirection(this.address, up.getAddress());
   }

   private static native void _setUpDirection(long var0, long var2);

   public float getSlopeLimit() {
      this.checkNotNull();
      return _getSlopeLimit(this.address);
   }

   private static native float _getSlopeLimit(long var0);

   public void setSlopeLimit(float slopeLimit) {
      this.checkNotNull();
      _setSlopeLimit(this.address, slopeLimit);
   }

   private static native void _setSlopeLimit(long var0, float var2);

   public void invalidateCache() {
      this.checkNotNull();
      _invalidateCache(this.address);
   }

   private static native void _invalidateCache(long var0);

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);

   public NativeObject getUserData() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getUserData(this.address));
   }

   private static native long _getUserData(long var0);

   public void setUserData(NativeObject userData) {
      this.checkNotNull();
      _setUserData(this.address, userData.getAddress());
   }

   private static native void _setUserData(long var0, long var2);

   public void getState(PxControllerState state) {
      this.checkNotNull();
      _getState(this.address, state.getAddress());
   }

   private static native void _getState(long var0, long var2);

   public void getStats(PxControllerStats stats) {
      this.checkNotNull();
      _getStats(this.address, stats.getAddress());
   }

   private static native void _getStats(long var0, long var2);

   public void resize(float height) {
      this.checkNotNull();
      _resize(this.address, height);
   }

   private static native void _resize(long var0, float var2);
}
