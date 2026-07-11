package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxMat33 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxMat33 wrapPointer(long address) {
      return address != 0L ? new PxMat33(address) : null;
   }

   public static PxMat33 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxMat33(long address) {
      super(address);
   }

   public PxMat33() {
      this.address = _PxMat33();
   }

   private static native long _PxMat33();

   public PxMat33(PxIDENTITYEnum r) {
      this.address = _PxMat33(r.value);
   }

   private static native long _PxMat33(int var0);

   public PxMat33(PxVec3 col0, PxVec3 col1, PxVec3 col2) {
      this.address = _PxMat33(col0.getAddress(), col1.getAddress(), col2.getAddress());
   }

   private static native long _PxMat33(long var0, long var2, long var4);

   public PxMat33(float r) {
      PlatformChecks.requirePlatform(15, "physx.common.PxMat33");
      this.address = _PxMat33(r);
   }

   private static native long _PxMat33(float var0);

   public PxMat33(PxQuat q) {
      PlatformChecks.requirePlatform(15, "physx.common.PxMat33");
      this.address = _PxMat33(q.getAddress());
   }

   private static native long _PxMat33(long var0);

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

   public PxVec3 getColumn0() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getColumn0(this.address));
   }

   private static native long _getColumn0(long var0);

   public void setColumn0(PxVec3 value) {
      this.checkNotNull();
      _setColumn0(this.address, value.getAddress());
   }

   private static native void _setColumn0(long var0, long var2);

   public PxVec3 getColumn1() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getColumn1(this.address));
   }

   private static native long _getColumn1(long var0);

   public void setColumn1(PxVec3 value) {
      this.checkNotNull();
      _setColumn1(this.address, value.getAddress());
   }

   private static native void _setColumn1(long var0, long var2);

   public PxVec3 getColumn2() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getColumn2(this.address));
   }

   private static native long _getColumn2(long var0);

   public void setColumn2(PxVec3 value) {
      this.checkNotNull();
      _setColumn2(this.address, value.getAddress());
   }

   private static native void _setColumn2(long var0, long var2);

   public PxMat33 getTranspose() {
      this.checkNotNull();
      return wrapPointer(_getTranspose(this.address));
   }

   private static native long _getTranspose(long var0);

   public PxMat33 getInverse() {
      this.checkNotNull();
      return wrapPointer(_getInverse(this.address));
   }

   private static native long _getInverse(long var0);

   public float getDeterminant() {
      this.checkNotNull();
      return _getDeterminant(this.address);
   }

   private static native float _getDeterminant(long var0);

   public PxVec3 transform(PxVec3 other) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_transform(this.address, other.getAddress()));
   }

   private static native long _transform(long var0, long var2);

   public PxVec3 transformTranspose(PxVec3 other) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_transformTranspose(this.address, other.getAddress()));
   }

   private static native long _transformTranspose(long var0, long var2);
}
