package physx.extensions;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxGjkQueryProximityInfoResult extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxGjkQueryProximityInfoResult wrapPointer(long address) {
      return address != 0L ? new PxGjkQueryProximityInfoResult(address) : null;
   }

   public static PxGjkQueryProximityInfoResult arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGjkQueryProximityInfoResult(long address) {
      super(address);
   }

   public static PxGjkQueryProximityInfoResult createAt(long address) {
      __placement_new_PxGjkQueryProximityInfoResult(address);
      PxGjkQueryProximityInfoResult createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxGjkQueryProximityInfoResult createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxGjkQueryProximityInfoResult(address);
      PxGjkQueryProximityInfoResult createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxGjkQueryProximityInfoResult(long var0);

   public PxGjkQueryProximityInfoResult() {
      this.address = _PxGjkQueryProximityInfoResult();
   }

   private static native long _PxGjkQueryProximityInfoResult();

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

   public boolean getSuccess() {
      this.checkNotNull();
      return _getSuccess(this.address);
   }

   private static native boolean _getSuccess(long var0);

   public void setSuccess(boolean value) {
      this.checkNotNull();
      _setSuccess(this.address, value);
   }

   private static native void _setSuccess(long var0, boolean var2);

   public PxVec3 getPointA() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPointA(this.address));
   }

   private static native long _getPointA(long var0);

   public void setPointA(PxVec3 value) {
      this.checkNotNull();
      _setPointA(this.address, value.getAddress());
   }

   private static native void _setPointA(long var0, long var2);

   public PxVec3 getPointB() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getPointB(this.address));
   }

   private static native long _getPointB(long var0);

   public void setPointB(PxVec3 value) {
      this.checkNotNull();
      _setPointB(this.address, value.getAddress());
   }

   private static native void _setPointB(long var0, long var2);

   public PxVec3 getSeparatingAxis() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getSeparatingAxis(this.address));
   }

   private static native long _getSeparatingAxis(long var0);

   public void setSeparatingAxis(PxVec3 value) {
      this.checkNotNull();
      _setSeparatingAxis(this.address, value.getAddress());
   }

   private static native void _setSeparatingAxis(long var0, long var2);

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
}
