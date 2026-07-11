package physx.extensions;

import physx.NativeObject;
import physx.common.PxMat33;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.PxGeometry;

public class PxMassProperties extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxMassProperties wrapPointer(long address) {
      return address != 0L ? new PxMassProperties(address) : null;
   }

   public static PxMassProperties arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMassProperties(long address) {
      super(address);
   }

   public PxMassProperties() {
      this.address = _PxMassProperties();
   }

   private static native long _PxMassProperties();

   public PxMassProperties(float m, PxMat33 inertiaT, PxVec3 com) {
      this.address = _PxMassProperties(m, inertiaT.getAddress(), com.getAddress());
   }

   private static native long _PxMassProperties(float var0, long var1, long var3);

   public PxMassProperties(PxGeometry geometry) {
      this.address = _PxMassProperties(geometry.getAddress());
   }

   private static native long _PxMassProperties(long var0);

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

   public PxMat33 getInertiaTensor() {
      this.checkNotNull();
      return PxMat33.wrapPointer(_getInertiaTensor(this.address));
   }

   private static native long _getInertiaTensor(long var0);

   public void setInertiaTensor(PxMat33 value) {
      this.checkNotNull();
      _setInertiaTensor(this.address, value.getAddress());
   }

   private static native void _setInertiaTensor(long var0, long var2);

   public PxVec3 getCenterOfMass() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getCenterOfMass(this.address));
   }

   private static native long _getCenterOfMass(long var0);

   public void setCenterOfMass(PxVec3 value) {
      this.checkNotNull();
      _setCenterOfMass(this.address, value.getAddress());
   }

   private static native void _setCenterOfMass(long var0, long var2);

   public float getMass() {
      this.checkNotNull();
      return _getMass(this.address);
   }

   private static native float _getMass(long var0);

   public void setMass(float value) {
      this.checkNotNull();
      _setMass(this.address, value);
   }

   private static native void _setMass(long var0, float var2);

   public void translate(PxVec3 t) {
      this.checkNotNull();
      _translate(this.address, t.getAddress());
   }

   private static native void _translate(long var0, long var2);

   public static PxVec3 getMassSpaceInertia(PxMat33 inertia, PxQuat massFrame) {
      return PxVec3.wrapPointer(_getMassSpaceInertia(inertia.getAddress(), massFrame.getAddress()));
   }

   private static native long _getMassSpaceInertia(long var0, long var2);

   public static PxMat33 translateInertia(PxMat33 inertia, float mass, PxVec3 t) {
      return PxMat33.wrapPointer(_translateInertia(inertia.getAddress(), mass, t.getAddress()));
   }

   private static native long _translateInertia(long var0, float var2, long var3);

   public static PxMat33 rotateInertia(PxMat33 inertia, PxQuat q) {
      return PxMat33.wrapPointer(_rotateInertia(inertia.getAddress(), q.getAddress()));
   }

   private static native long _rotateInertia(long var0, long var2);

   public static PxMat33 scaleInertia(PxMat33 inertia, PxQuat scaleRotation, PxVec3 scale) {
      return PxMat33.wrapPointer(_scaleInertia(inertia.getAddress(), scaleRotation.getAddress(), scale.getAddress()));
   }

   private static native long _scaleInertia(long var0, long var2, long var4);

   public static PxMassProperties sum(PxMassProperties props, PxTransform transforms, int count) {
      return wrapPointer(_sum(props.getAddress(), transforms.getAddress(), count));
   }

   private static native long _sum(long var0, long var2, int var4);
}
