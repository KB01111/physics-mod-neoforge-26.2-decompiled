package physx.extensions;

import physx.NativeObject;
import physx.common.PxBase;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxConstraint;
import physx.physics.PxConstraintFlagEnum;
import physx.physics.PxConstraintFlags;
import physx.physics.PxRigidActor;
import physx.physics.PxScene;

public class PxJoint extends PxBase {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxJoint() {
   }

   private static native int __sizeOf();

   public static PxJoint wrapPointer(long address) {
      return address != 0L ? new PxJoint(address) : null;
   }

   public static PxJoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxJoint(long address) {
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

   public void setActors(PxRigidActor actor0, PxRigidActor actor1) {
      this.checkNotNull();
      _setActors(this.address, actor0.getAddress(), actor1.getAddress());
   }

   private static native void _setActors(long var0, long var2, long var4);

   public void setLocalPose(PxJointActorIndexEnum actor, PxTransform localPose) {
      this.checkNotNull();
      _setLocalPose(this.address, actor.value, localPose.getAddress());
   }

   private static native void _setLocalPose(long var0, int var2, long var3);

   public PxTransform getLocalPose(PxJointActorIndexEnum actor) {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getLocalPose(this.address, actor.value));
   }

   private static native long _getLocalPose(long var0, int var2);

   public PxTransform getRelativeTransform() {
      this.checkNotNull();
      return PxTransform.wrapPointer(_getRelativeTransform(this.address));
   }

   private static native long _getRelativeTransform(long var0);

   public PxVec3 getRelativeLinearVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getRelativeLinearVelocity(this.address));
   }

   private static native long _getRelativeLinearVelocity(long var0);

   public PxVec3 getRelativeAngularVelocity() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getRelativeAngularVelocity(this.address));
   }

   private static native long _getRelativeAngularVelocity(long var0);

   public void setBreakForce(float force, float torque) {
      this.checkNotNull();
      _setBreakForce(this.address, force, torque);
   }

   private static native void _setBreakForce(long var0, float var2, float var3);

   public void setConstraintFlags(PxConstraintFlags flags) {
      this.checkNotNull();
      _setConstraintFlags(this.address, flags.getAddress());
   }

   private static native void _setConstraintFlags(long var0, long var2);

   public void setConstraintFlag(PxConstraintFlagEnum flag, boolean value) {
      this.checkNotNull();
      _setConstraintFlag(this.address, flag.value, value);
   }

   private static native void _setConstraintFlag(long var0, int var2, boolean var3);

   public PxConstraintFlags getConstraintFlags() {
      this.checkNotNull();
      return PxConstraintFlags.wrapPointer(_getConstraintFlags(this.address));
   }

   private static native long _getConstraintFlags(long var0);

   public void setInvMassScale0(float invMassScale) {
      this.checkNotNull();
      _setInvMassScale0(this.address, invMassScale);
   }

   private static native void _setInvMassScale0(long var0, float var2);

   public float getInvMassScale0() {
      this.checkNotNull();
      return _getInvMassScale0(this.address);
   }

   private static native float _getInvMassScale0(long var0);

   public void setInvMassScale1(float invMassScale) {
      this.checkNotNull();
      _setInvMassScale1(this.address, invMassScale);
   }

   private static native void _setInvMassScale1(long var0, float var2);

   public float getInvMassScale1() {
      this.checkNotNull();
      return _getInvMassScale1(this.address);
   }

   private static native float _getInvMassScale1(long var0);

   public PxConstraint getConstraint() {
      this.checkNotNull();
      return PxConstraint.wrapPointer(_getConstraint(this.address));
   }

   private static native long _getConstraint(long var0);

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

   public PxScene getScene() {
      this.checkNotNull();
      return PxScene.wrapPointer(_getScene(this.address));
   }

   private static native long _getScene(long var0);
}
