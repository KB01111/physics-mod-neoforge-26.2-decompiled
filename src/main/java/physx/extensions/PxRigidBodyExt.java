package physx.extensions;

import physx.NativeObject;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidBody;

public class PxRigidBodyExt extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxRigidBodyExt() {
   }

   private static native int __sizeOf();

   public static PxRigidBodyExt wrapPointer(long address) {
      return address != 0L ? new PxRigidBodyExt(address) : null;
   }

   public static PxRigidBodyExt arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxRigidBodyExt(long address) {
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

   public static boolean updateMassAndInertia(PxRigidBody body, float density) {
      return _updateMassAndInertia(body.getAddress(), density);
   }

   private static native boolean _updateMassAndInertia(long var0, float var2);

   public static boolean updateMassAndInertia(PxRigidBody body, float density, PxVec3 massLocalPose) {
      return _updateMassAndInertia(body.getAddress(), density, massLocalPose.getAddress());
   }

   private static native boolean _updateMassAndInertia(long var0, float var2, long var3);

   public static boolean updateMassAndInertia(PxRigidBody body, float density, PxVec3 massLocalPose, boolean includeNonSimShapes) {
      return _updateMassAndInertia(body.getAddress(), density, massLocalPose.getAddress(), includeNonSimShapes);
   }

   private static native boolean _updateMassAndInertia(long var0, float var2, long var3, boolean var5);

   public static boolean setMassAndUpdateInertia(PxRigidBody body, float mass) {
      return _setMassAndUpdateInertia(body.getAddress(), mass);
   }

   private static native boolean _setMassAndUpdateInertia(long var0, float var2);

   public static boolean setMassAndUpdateInertia(PxRigidBody body, float mass, PxVec3 massLocalPose) {
      return _setMassAndUpdateInertia(body.getAddress(), mass, massLocalPose.getAddress());
   }

   private static native boolean _setMassAndUpdateInertia(long var0, float var2, long var3);

   public static boolean setMassAndUpdateInertia(PxRigidBody body, float mass, PxVec3 massLocalPose, boolean includeNonSimShapes) {
      return _setMassAndUpdateInertia(body.getAddress(), mass, massLocalPose.getAddress(), includeNonSimShapes);
   }

   private static native boolean _setMassAndUpdateInertia(long var0, float var2, long var3, boolean var5);

   public static void addForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos) {
      _addForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress());
   }

   private static native void _addForceAtPos(long var0, long var2, long var4);

   public static void addForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode) {
      _addForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value);
   }

   private static native void _addForceAtPos(long var0, long var2, long var4, int var6);

   public static void addForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode, boolean wakeup) {
      _addForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value, wakeup);
   }

   private static native void _addForceAtPos(long var0, long var2, long var4, int var6, boolean var7);

   public static void addForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos) {
      _addForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress());
   }

   private static native void _addForceAtLocalPos(long var0, long var2, long var4);

   public static void addForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode) {
      _addForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value);
   }

   private static native void _addForceAtLocalPos(long var0, long var2, long var4, int var6);

   public static void addForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode, boolean wakeup) {
      _addForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value, wakeup);
   }

   private static native void _addForceAtLocalPos(long var0, long var2, long var4, int var6, boolean var7);

   public static void addLocalForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos) {
      _addLocalForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress());
   }

   private static native void _addLocalForceAtPos(long var0, long var2, long var4);

   public static void addLocalForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode) {
      _addLocalForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value);
   }

   private static native void _addLocalForceAtPos(long var0, long var2, long var4, int var6);

   public static void addLocalForceAtPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode, boolean wakeup) {
      _addLocalForceAtPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value, wakeup);
   }

   private static native void _addLocalForceAtPos(long var0, long var2, long var4, int var6, boolean var7);

   public static void addLocalForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos) {
      _addLocalForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress());
   }

   private static native void _addLocalForceAtLocalPos(long var0, long var2, long var4);

   public static void addLocalForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode) {
      _addLocalForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value);
   }

   private static native void _addLocalForceAtLocalPos(long var0, long var2, long var4, int var6);

   public static void addLocalForceAtLocalPos(PxRigidBody body, PxVec3 force, PxVec3 pos, PxForceModeEnum mode, boolean wakeup) {
      _addLocalForceAtLocalPos(body.getAddress(), force.getAddress(), pos.getAddress(), mode.value, wakeup);
   }

   private static native void _addLocalForceAtLocalPos(long var0, long var2, long var4, int var6, boolean var7);

   public static PxVec3 getVelocityAtPos(PxRigidBody body, PxVec3 pos) {
      return PxVec3.wrapPointer(_getVelocityAtPos(body.getAddress(), pos.getAddress()));
   }

   private static native long _getVelocityAtPos(long var0, long var2);

   public static PxVec3 getLocalVelocityAtLocalPos(PxRigidBody body, PxVec3 pos) {
      return PxVec3.wrapPointer(_getLocalVelocityAtLocalPos(body.getAddress(), pos.getAddress()));
   }

   private static native long _getLocalVelocityAtLocalPos(long var0, long var2);

   public static PxVec3 getVelocityAtOffset(PxRigidBody body, PxVec3 pos) {
      return PxVec3.wrapPointer(_getVelocityAtOffset(body.getAddress(), pos.getAddress()));
   }

   private static native long _getVelocityAtOffset(long var0, long var2);

   public static void computeVelocityDeltaFromImpulse(
      PxRigidBody body, PxVec3 impulsiveForce, PxVec3 impulsiveTorque, PxVec3 deltaLinearVelocity, PxVec3 deltaAngularVelocity
   ) {
      _computeVelocityDeltaFromImpulse(
         body.getAddress(), impulsiveForce.getAddress(), impulsiveTorque.getAddress(), deltaLinearVelocity.getAddress(), deltaAngularVelocity.getAddress()
      );
   }

   private static native void _computeVelocityDeltaFromImpulse(long var0, long var2, long var4, long var6, long var8);

   public static void computeVelocityDeltaFromImpulse(
      PxRigidBody body,
      PxTransform globalPose,
      PxVec3 point,
      PxVec3 impulse,
      float invMassScale,
      float invInertiaScale,
      PxVec3 deltaLinearVelocity,
      PxVec3 deltaAngularVelocity
   ) {
      _computeVelocityDeltaFromImpulse(
         body.getAddress(),
         globalPose.getAddress(),
         point.getAddress(),
         impulse.getAddress(),
         invMassScale,
         invInertiaScale,
         deltaLinearVelocity.getAddress(),
         deltaAngularVelocity.getAddress()
      );
   }

   private static native void _computeVelocityDeltaFromImpulse(long var0, long var2, long var4, long var6, float var8, float var9, long var10, long var12);

   public static void computeLinearAngularImpulse(
      PxRigidBody body,
      PxTransform globalPose,
      PxVec3 point,
      PxVec3 impulse,
      float invMassScale,
      float invInertiaScale,
      PxVec3 linearImpulse,
      PxVec3 angularImpulse
   ) {
      _computeLinearAngularImpulse(
         body.getAddress(),
         globalPose.getAddress(),
         point.getAddress(),
         impulse.getAddress(),
         invMassScale,
         invInertiaScale,
         linearImpulse.getAddress(),
         angularImpulse.getAddress()
      );
   }

   private static native void _computeLinearAngularImpulse(long var0, long var2, long var4, long var6, float var8, float var9, long var10, long var12);
}
