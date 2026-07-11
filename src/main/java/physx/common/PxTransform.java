package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxTransform extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxTransform wrapPointer(long address) {
      return address != 0L ? new PxTransform(address) : null;
   }

   public static PxTransform arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTransform(long address) {
      super(address);
   }

   public static PxTransform createAt(long address) {
      __placement_new_PxTransform(address);
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTransform(address);
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTransform(long var0);

   public static PxTransform createAt(long address, PxIDENTITYEnum r) {
      __placement_new_PxTransform(address, r.value);
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate, PxIDENTITYEnum r) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTransform(address, r.value);
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTransform(long var0, int var2);

   public static PxTransform createAt(long address, PxVec3 p0, PxQuat q0) {
      __placement_new_PxTransform(address, p0.getAddress(), q0.getAddress());
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 p0, PxQuat q0) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTransform(address, p0.getAddress(), q0.getAddress());
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTransform(long var0, long var2, long var4);

   public static PxTransform createAt(long address, PxVec3 position) {
      PlatformChecks.requirePlatform(15, "physx.common.PxTransform");
      __placement_new_PxTransform(address, position.getAddress());
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxTransform createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 position) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxTransform(address, position.getAddress());
      PxTransform createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxTransform(long var0, long var2);

   public PxTransform() {
      this.address = _PxTransform();
   }

   private static native long _PxTransform();

   public PxTransform(PxIDENTITYEnum r) {
      this.address = _PxTransform(r.value);
   }

   private static native long _PxTransform(int var0);

   public PxTransform(PxVec3 p0, PxQuat q0) {
      this.address = _PxTransform(p0.getAddress(), q0.getAddress());
   }

   private static native long _PxTransform(long var0, long var2);

   public PxTransform(PxVec3 position) {
      PlatformChecks.requirePlatform(15, "physx.common.PxTransform");
      this.address = _PxTransform(position.getAddress());
   }

   private static native long _PxTransform(long var0);

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

   public PxQuat getQ() {
      this.checkNotNull();
      return PxQuat.wrapPointer(_getQ(this.address));
   }

   private static native long _getQ(long var0);

   public void setQ(PxQuat value) {
      this.checkNotNull();
      _setQ(this.address, value.getAddress());
   }

   private static native void _setQ(long var0, long var2);

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

   public PxTransform getInverse() {
      this.checkNotNull();
      return wrapPointer(_getInverse(this.address));
   }

   private static native long _getInverse(long var0);

   public PxVec3 transform(PxVec3 input) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_transform(this.address, input.getAddress()));
   }

   private static native long _transform(long var0, long var2);

   public PxVec3 transformInv(PxVec3 input) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_transformInv(this.address, input.getAddress()));
   }

   private static native long _transformInv(long var0, long var2);

   public boolean isValid() {
      this.checkNotNull();
      return _isValid(this.address);
   }

   private static native boolean _isValid(long var0);

   public boolean isSane() {
      this.checkNotNull();
      return _isSane(this.address);
   }

   private static native boolean _isSane(long var0);

   public boolean isFinite() {
      this.checkNotNull();
      return _isFinite(this.address);
   }

   private static native boolean _isFinite(long var0);

   public PxTransform getNormalized() {
      this.checkNotNull();
      return wrapPointer(_getNormalized(this.address));
   }

   private static native long _getNormalized(long var0);
}
