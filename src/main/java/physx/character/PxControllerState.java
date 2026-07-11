package physx.character;

import physx.NativeObject;
import physx.common.PxVec3;
import physx.physics.PxRigidActor;
import physx.physics.PxShape;

public class PxControllerState extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxControllerState wrapPointer(long address) {
      return address != 0L ? new PxControllerState(address) : null;
   }

   public static PxControllerState arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxControllerState(long address) {
      super(address);
   }

   public PxControllerState() {
      this.address = _PxControllerState();
   }

   private static native long _PxControllerState();

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

   public PxVec3 getDeltaXP() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getDeltaXP(this.address));
   }

   private static native long _getDeltaXP(long var0);

   public void setDeltaXP(PxVec3 value) {
      this.checkNotNull();
      _setDeltaXP(this.address, value.getAddress());
   }

   private static native void _setDeltaXP(long var0, long var2);

   public PxShape getTouchedShape() {
      this.checkNotNull();
      return PxShape.wrapPointer(_getTouchedShape(this.address));
   }

   private static native long _getTouchedShape(long var0);

   public void setTouchedShape(PxShape value) {
      this.checkNotNull();
      _setTouchedShape(this.address, value.getAddress());
   }

   private static native void _setTouchedShape(long var0, long var2);

   public PxRigidActor getTouchedActor() {
      this.checkNotNull();
      return PxRigidActor.wrapPointer(_getTouchedActor(this.address));
   }

   private static native long _getTouchedActor(long var0);

   public void setTouchedActor(PxRigidActor value) {
      this.checkNotNull();
      _setTouchedActor(this.address, value.getAddress());
   }

   private static native void _setTouchedActor(long var0, long var2);

   public int getTouchedObstacleHandle() {
      this.checkNotNull();
      return _getTouchedObstacleHandle(this.address);
   }

   private static native int _getTouchedObstacleHandle(long var0);

   public void setTouchedObstacleHandle(int value) {
      this.checkNotNull();
      _setTouchedObstacleHandle(this.address, value);
   }

   private static native void _setTouchedObstacleHandle(long var0, int var2);

   public int getCollisionFlags() {
      this.checkNotNull();
      return _getCollisionFlags(this.address);
   }

   private static native int _getCollisionFlags(long var0);

   public void setCollisionFlags(int value) {
      this.checkNotNull();
      _setCollisionFlags(this.address, value);
   }

   private static native void _setCollisionFlags(long var0, int var2);

   public boolean getStandOnAnotherCCT() {
      this.checkNotNull();
      return _getStandOnAnotherCCT(this.address);
   }

   private static native boolean _getStandOnAnotherCCT(long var0);

   public void setStandOnAnotherCCT(boolean value) {
      this.checkNotNull();
      _setStandOnAnotherCCT(this.address, value);
   }

   private static native void _setStandOnAnotherCCT(long var0, boolean var2);

   public boolean getStandOnObstacle() {
      this.checkNotNull();
      return _getStandOnObstacle(this.address);
   }

   private static native boolean _getStandOnObstacle(long var0);

   public void setStandOnObstacle(boolean value) {
      this.checkNotNull();
      _setStandOnObstacle(this.address, value);
   }

   private static native void _setStandOnObstacle(long var0, boolean var2);

   public boolean getIsMovingUp() {
      this.checkNotNull();
      return _getIsMovingUp(this.address);
   }

   private static native boolean _getIsMovingUp(long var0);

   public void setIsMovingUp(boolean value) {
      this.checkNotNull();
      _setIsMovingUp(this.address, value);
   }

   private static native void _setIsMovingUp(long var0, boolean var2);
}
