package physx.physics;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxContactPairPoint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxContactPairPoint() {
   }

   private static native int __sizeOf();

   public static PxContactPairPoint wrapPointer(long address) {
      return address != 0L ? new PxContactPairPoint(address) : null;
   }

   public static PxContactPairPoint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxContactPairPoint(long address) {
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

   public PxVec3 getPosition() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPosition(this.address));
   }

   private static native long _getPosition(long var0);

   public void setPosition(PxVec3 value) {
      this.checkNotNull();
      _setPosition(this.address, value.getAddress());
   }

   private static native void _setPosition(long var0, long var2);

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

   public int getInternalFaceIndex0() {
      this.checkNotNull();
      return _getInternalFaceIndex0(this.address);
   }

   private static native int _getInternalFaceIndex0(long var0);

   public void setInternalFaceIndex0(int value) {
      this.checkNotNull();
      _setInternalFaceIndex0(this.address, value);
   }

   private static native void _setInternalFaceIndex0(long var0, int var2);

   public PxVec3 getImpulse() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getImpulse(this.address));
   }

   private static native long _getImpulse(long var0);

   public void setImpulse(PxVec3 value) {
      this.checkNotNull();
      _setImpulse(this.address, value.getAddress());
   }

   private static native void _setImpulse(long var0, long var2);

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
}
