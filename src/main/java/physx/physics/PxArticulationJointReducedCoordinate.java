package physx.physics;

import physx.common.PxBase;
import physx.common.PxTransform;

public class PxArticulationJointReducedCoordinate extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxArticulationJointReducedCoordinate() {
   }

   private static native int __sizeOf();

   public static PxArticulationJointReducedCoordinate wrapPointer(long address) {
      return address != 0L ? new PxArticulationJointReducedCoordinate(address) : null;
   }

   public static PxArticulationJointReducedCoordinate arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxArticulationJointReducedCoordinate(long address) {
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

   public PxArticulationLink getParentArticulationLink() {
      this.checkNotNull();
      return PxArticulationLink.wrapPointer(_getParentArticulationLink(this.address));
   }

   private static native long _getParentArticulationLink(long var0);

   public void setParentPose(PxTransform pose) {
      this.checkNotNull();
      _setParentPose(this.address, pose.getAddress());
   }

   private static native void _setParentPose(long var0, long var2);

   public PxTransform getParentPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getParentPose(this.address));
   }

   private static native long _getParentPose(long var0);

   public PxArticulationLink getChildArticulationLink() {
      this.checkNotNull();
      return PxArticulationLink.wrapPointer(_getChildArticulationLink(this.address));
   }

   private static native long _getChildArticulationLink(long var0);

   public void setChildPose(PxTransform pose) {
      this.checkNotNull();
      _setChildPose(this.address, pose.getAddress());
   }

   private static native void _setChildPose(long var0, long var2);

   public PxTransform getChildPose() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getChildPose(this.address));
   }

   private static native long _getChildPose(long var0);

   public void setJointType(PxArticulationJointTypeEnum jointType) {
      this.checkNotNull();
      _setJointType(this.address, jointType.value);
   }

   private static native void _setJointType(long var0, int var2);

   public PxArticulationJointTypeEnum getJointType() {
      this.checkNotNull();
      return PxArticulationJointTypeEnum.forValue(_getJointType(this.address));
   }

   private static native int _getJointType(long var0);

   public void setMotion(PxArticulationAxisEnum axis, PxArticulationMotionEnum motion) {
      this.checkNotNull();
      _setMotion(this.address, axis.value, motion.value);
   }

   private static native void _setMotion(long var0, int var2, int var3);

   public PxArticulationMotionEnum getMotion(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return PxArticulationMotionEnum.forValue(_getMotion(this.address, axis.value));
   }

   private static native int _getMotion(long var0, int var2);

   public void setLimitParams(PxArticulationAxisEnum axis, PxArticulationLimit limit) {
      this.checkNotNull();
      _setLimitParams(this.address, axis.value, limit.getAddress());
   }

   private static native void _setLimitParams(long var0, int var2, long var3);

   public PxArticulationLimit getLimitParams(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return PxArticulationLimit.wrapPointer(_getLimitParams(this.address, axis.value));
   }

   private static native long _getLimitParams(long var0, int var2);

   public void setDriveParams(PxArticulationAxisEnum axis, PxArticulationDrive drive) {
      this.checkNotNull();
      _setDriveParams(this.address, axis.value, drive.getAddress());
   }

   private static native void _setDriveParams(long var0, int var2, long var3);

   public void setDriveTarget(PxArticulationAxisEnum axis, float target) {
      this.checkNotNull();
      _setDriveTarget(this.address, axis.value, target);
   }

   private static native void _setDriveTarget(long var0, int var2, float var3);

   public void setDriveTarget(PxArticulationAxisEnum axis, float target, boolean autowake) {
      this.checkNotNull();
      _setDriveTarget(this.address, axis.value, target, autowake);
   }

   private static native void _setDriveTarget(long var0, int var2, float var3, boolean var4);

   public float getDriveTarget(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return _getDriveTarget(this.address, axis.value);
   }

   private static native float _getDriveTarget(long var0, int var2);

   public void setDriveVelocity(PxArticulationAxisEnum axis, float targetVel) {
      this.checkNotNull();
      _setDriveVelocity(this.address, axis.value, targetVel);
   }

   private static native void _setDriveVelocity(long var0, int var2, float var3);

   public void setDriveVelocity(PxArticulationAxisEnum axis, float targetVel, boolean autowake) {
      this.checkNotNull();
      _setDriveVelocity(this.address, axis.value, targetVel, autowake);
   }

   private static native void _setDriveVelocity(long var0, int var2, float var3, boolean var4);

   public float getDriveVelocity(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return _getDriveVelocity(this.address, axis.value);
   }

   private static native float _getDriveVelocity(long var0, int var2);

   public void setArmature(PxArticulationAxisEnum axis, float armature) {
      this.checkNotNull();
      _setArmature(this.address, axis.value, armature);
   }

   private static native void _setArmature(long var0, int var2, float var3);

   public float getArmature(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return _getArmature(this.address, axis.value);
   }

   private static native float _getArmature(long var0, int var2);

   public void setFrictionCoefficient(float coefficient) {
      this.checkNotNull();
      _setFrictionCoefficient(this.address, coefficient);
   }

   private static native void _setFrictionCoefficient(long var0, float var2);

   public float getFrictionCoefficient() {
      this.checkNotNull();
      return _getFrictionCoefficient(this.address);
   }

   private static native float _getFrictionCoefficient(long var0);

   public void setMaxJointVelocity(float maxJointV) {
      this.checkNotNull();
      _setMaxJointVelocity(this.address, maxJointV);
   }

   private static native void _setMaxJointVelocity(long var0, float var2);

   public float getMaxJointVelocity() {
      this.checkNotNull();
      return _getMaxJointVelocity(this.address);
   }

   private static native float _getMaxJointVelocity(long var0);

   public void setJointPosition(PxArticulationAxisEnum axis, float jointPos) {
      this.checkNotNull();
      _setJointPosition(this.address, axis.value, jointPos);
   }

   private static native void _setJointPosition(long var0, int var2, float var3);

   public float getJointPosition(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return _getJointPosition(this.address, axis.value);
   }

   private static native float _getJointPosition(long var0, int var2);

   public void setJointVelocity(PxArticulationAxisEnum axis, float jointVel) {
      this.checkNotNull();
      _setJointVelocity(this.address, axis.value, jointVel);
   }

   private static native void _setJointVelocity(long var0, int var2, float var3);

   public float getJointVelocity(PxArticulationAxisEnum axis) {
      this.checkNotNull();
      return _getJointVelocity(this.address, axis.value);
   }

   private static native float _getJointVelocity(long var0, int var2);
}
