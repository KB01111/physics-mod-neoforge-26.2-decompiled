package physx.geometry;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxContactPoint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxContactPoint wrapPointer(long address) {
      return address != 0L ? new PxContactPoint(address) : null;
   }

   public static PxContactPoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxContactPoint(long address) {
      super(address);
   }

   public PxContactPoint() {
      this.address = _PxContactPoint();
   }

   private static native long _PxContactPoint();

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

   public PxVec3 getNormal() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getNormal(this.address));
   }

   private static native long _getNormal(long var0);

   public void setNormal(PxVec3 value) {
      this.checkNotNull();
      _setNormal(this.address, value.getAddress());
   }

   private static native void _setNormal(long var0, long var2);

   public PxVec3 getPoint() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPoint(this.address));
   }

   private static native long _getPoint(long var0);

   public void setPoint(PxVec3 value) {
      this.checkNotNull();
      _setPoint(this.address, value.getAddress());
   }

   private static native void _setPoint(long var0, long var2);

   public PxVec3 getTargetVel() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getTargetVel(this.address));
   }

   private static native long _getTargetVel(long var0);

   public void setTargetVel(PxVec3 value) {
      this.checkNotNull();
      _setTargetVel(this.address, value.getAddress());
   }

   private static native void _setTargetVel(long var0, long var2);

   public float getSeparation() {
      this.checkNotNull();
      return _getSeparation(this.address);
   }

   private static native float _getSeparation(long var0);

   public void setSeparation(float value) {
      this.checkNotNull();
      _setSeparation(this.address, value);
   }

   private static native void _setSeparation(long var0, float var2);

   public float getMaxImpulse() {
      this.checkNotNull();
      return _getMaxImpulse(this.address);
   }

   private static native float _getMaxImpulse(long var0);

   public void setMaxImpulse(float value) {
      this.checkNotNull();
      _setMaxImpulse(this.address, value);
   }

   private static native void _setMaxImpulse(long var0, float var2);

   public float getStaticFriction() {
      this.checkNotNull();
      return _getStaticFriction(this.address);
   }

   private static native float _getStaticFriction(long var0);

   public void setStaticFriction(float value) {
      this.checkNotNull();
      _setStaticFriction(this.address, value);
   }

   private static native void _setStaticFriction(long var0, float var2);

   public byte getMaterialFlags() {
      this.checkNotNull();
      return _getMaterialFlags(this.address);
   }

   private static native byte _getMaterialFlags(long var0);

   public void setMaterialFlags(byte value) {
      this.checkNotNull();
      _setMaterialFlags(this.address, value);
   }

   private static native void _setMaterialFlags(long var0, byte var2);

   public int getInternalFaceIndex1() {
      this.checkNotNull();
      return _getInternalFaceIndex1(this.address);
   }

   private static native int _getInternalFaceIndex1(long var0);

   public void setInternalFaceIndex1(int value) {
      this.checkNotNull();
      _setInternalFaceIndex1(this.address, value);
   }

   private static native void _setInternalFaceIndex1(long var0, int var2);

   public float getDynamicFriction() {
      this.checkNotNull();
      return _getDynamicFriction(this.address);
   }

   private static native float _getDynamicFriction(long var0);

   public void setDynamicFriction(float value) {
      this.checkNotNull();
      _setDynamicFriction(this.address, value);
   }

   private static native void _setDynamicFriction(long var0, float var2);

   public float getRestitution() {
      this.checkNotNull();
      return _getRestitution(this.address);
   }

   private static native float _getRestitution(long var0);

   public void setRestitution(float value) {
      this.checkNotNull();
      _setRestitution(this.address, value);
   }

   private static native void _setRestitution(long var0, float var2);

   public float getDamping() {
      this.checkNotNull();
      return _getDamping(this.address);
   }

   private static native float _getDamping(long var0);

   public void setDamping(float value) {
      this.checkNotNull();
      _setDamping(this.address, value);
   }

   private static native void _setDamping(long var0, float var2);
}
