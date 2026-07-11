package physx.extensions;

import physx.NativeObject;
import physx.common.PxVec3;

public class PxGjkQueryRaycastResult extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxGjkQueryRaycastResult wrapPointer(long address) {
      return address != 0L ? new PxGjkQueryRaycastResult(address) : null;
   }

   public static PxGjkQueryRaycastResult arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGjkQueryRaycastResult(long address) {
      super(address);
   }

   public static PxGjkQueryRaycastResult createAt(long address) {
      __placement_new_PxGjkQueryRaycastResult(address);
      PxGjkQueryRaycastResult createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxGjkQueryRaycastResult createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxGjkQueryRaycastResult(address);
      PxGjkQueryRaycastResult createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxGjkQueryRaycastResult(long var0);

   public PxGjkQueryRaycastResult() {
      this.address = _PxGjkQueryRaycastResult();
   }

   private static native long _PxGjkQueryRaycastResult();

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

   public float getT() {
      this.checkNotNull();
      return _getT(this.address);
   }

   private static native float _getT(long var0);

   public void setT(float value) {
      this.checkNotNull();
      _setT(this.address, value);
   }

   private static native void _setT(long var0, float var2);

   public PxVec3 getN() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getN(this.address));
   }

   private static native long _getN(long var0);

   public void setN(PxVec3 value) {
      this.checkNotNull();
      _setN(this.address, value.getAddress());
   }

   private static native void _setN(long var0, long var2);

   public PxVec3 getP() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getP(this.address));
   }

   private static native long _getP(long var0);

   public void setP(PxVec3 value) {
      this.checkNotNull();
      _setP(this.address, value.getAddress());
   }

   private static native void _setP(long var0, long var2);
}
