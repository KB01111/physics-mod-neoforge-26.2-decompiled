package physx.physics;

import physx.common.PxTransform;
import physx.common.PxVec3;

public class PxRigidDynamic extends PxRigidBody {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRigidDynamic() {
   }

   private static native int __sizeOf();

   public static PxRigidDynamic wrapPointer(long address) {
      return address != 0L ? new PxRigidDynamic(address) : null;
   }

   public static PxRigidDynamic arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRigidDynamic(long address) {
      super(address);
   }

   public void setKinematicTarget(PxTransform destination) {
      this.checkNotNull();
      _setKinematicTarget(this.address, destination.getAddress());
   }

   private static native void _setKinematicTarget(long var0, long var2);

   public boolean getKinematicTarget(PxTransform target) {
      this.checkNotNull();
      return _getKinematicTarget(this.address, target.getAddress());
   }

   private static native boolean _getKinematicTarget(long var0, long var2);

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

   public PxRigidDynamicLockFlags getRigidDynamicLockFlags() {
      this.checkNotNull();
      return PxRigidDynamicLockFlags.wrapPointer(_getRigidDynamicLockFlags(this.address));
   }

   private static native long _getRigidDynamicLockFlags(long var0);

   public void setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setRigidDynamicLockFlag(this.address, flag.value, value);
   }

   private static native void _setRigidDynamicLockFlag(long var0, int var2, boolean var3);

   public void setRigidDynamicLockFlags(PxRigidDynamicLockFlags flags) {
      this.checkNotNull();
      _setRigidDynamicLockFlags(this.address, flags.getAddress());
   }

   private static native void _setRigidDynamicLockFlags(long var0, long var2);

   public void setLinearVelocity(PxVec3 linVel) {
      this.checkNotNull();
      _setLinearVelocity(this.address, linVel.getAddress());
   }

   private static native void _setLinearVelocity(long var0, long var2);

   public void setLinearVelocity(PxVec3 linVel, boolean autowake) {
      this.checkNotNull();
      _setLinearVelocity(this.address, linVel.getAddress(), autowake);
   }

   private static native void _setLinearVelocity(long var0, long var2, boolean var4);

   public void setAngularVelocity(PxVec3 angVel) {
      this.checkNotNull();
      _setAngularVelocity(this.address, angVel.getAddress());
   }

   private static native void _setAngularVelocity(long var0, long var2);

   public void setAngularVelocity(PxVec3 angVel, boolean autowake) {
      this.checkNotNull();
      _setAngularVelocity(this.address, angVel.getAddress(), autowake);
   }

   private static native void _setAngularVelocity(long var0, long var2, boolean var4);

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

   public float getContactReportThreshold() {
      this.checkNotNull();
      return _getContactReportThreshold(this.address);
   }

   private static native float _getContactReportThreshold(long var0);

   public void setContactReportThreshold(float threshold) {
      this.checkNotNull();
      _setContactReportThreshold(this.address, threshold);
   }

   private static native void _setContactReportThreshold(long var0, float var2);
}
