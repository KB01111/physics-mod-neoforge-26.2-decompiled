package physx.common;

import physx.NativeObject;

public class PxVec3 extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxVec3 wrapPointer(long address) {
      return address != 0L ? new PxVec3(address) : null;
   }

   public static PxVec3 arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxVec3(long address) {
      super(address);
   }

   public static PxVec3 createAt(long address) {
      __placement_new_PxVec3(address);
      PxVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVec3 createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVec3(address);
      PxVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVec3(long var0);

   public static PxVec3 createAt(long address, float x, float y, float z) {
      __placement_new_PxVec3(address, x, y, z);
      PxVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxVec3 createAt(T allocator, NativeObject.Allocator<T> allocate, float x, float y, float z) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxVec3(address, x, y, z);
      PxVec3 createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxVec3(long var0, float var2, float var3, float var4);

   public PxVec3() {
      this.address = _PxVec3();
   }

   private static native long _PxVec3();

   public PxVec3(float x, float y, float z) {
      this.address = _PxVec3(x, y, z);
   }

   private static native long _PxVec3(float var0, float var1, float var2);

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

   public float getX() {
      this.checkNotNull();
      return _getX(this.address);
   }

   private static native float _getX(long var0);

   public void setX(float value) {
      this.checkNotNull();
      _setX(this.address, value);
   }

   private static native void _setX(long var0, float var2);

   public float getY() {
      this.checkNotNull();
      return _getY(this.address);
   }

   private static native float _getY(long var0);

   public void setY(float value) {
      this.checkNotNull();
      _setY(this.address, value);
   }

   private static native void _setY(long var0, float var2);

   public float getZ() {
      this.checkNotNull();
      return _getZ(this.address);
   }

   private static native float _getZ(long var0);

   public void setZ(float value) {
      this.checkNotNull();
      _setZ(this.address, value);
   }

   private static native void _setZ(long var0, float var2);

   public boolean isZero() {
      this.checkNotNull();
      return _isZero(this.address);
   }

   private static native boolean _isZero(long var0);

   public boolean isFinite() {
      this.checkNotNull();
      return _isFinite(this.address);
   }

   private static native boolean _isFinite(long var0);

   public boolean isNormalized() {
      this.checkNotNull();
      return _isNormalized(this.address);
   }

   private static native boolean _isNormalized(long var0);

   public float magnitudeSquared() {
      this.checkNotNull();
      return _magnitudeSquared(this.address);
   }

   private static native float _magnitudeSquared(long var0);

   public float magnitude() {
      this.checkNotNull();
      return _magnitude(this.address);
   }

   private static native float _magnitude(long var0);

   public float dot(PxVec3 v) {
      this.checkNotNull();
      return _dot(this.address, v.getAddress());
   }

   private static native float _dot(long var0, long var2);

   public PxVec3 cross(PxVec3 v) {
      this.checkNotNull();
      return wrapPointer(_cross(this.address, v.getAddress()));
   }

   private static native long _cross(long var0, long var2);

   public PxVec3 getNormalized() {
      this.checkNotNull();
      return wrapPointer(_getNormalized(this.address));
   }

   private static native long _getNormalized(long var0);

   public float normalize() {
      this.checkNotNull();
      return _normalize(this.address);
   }

   private static native float _normalize(long var0);

   public float normalizeSafe() {
      this.checkNotNull();
      return _normalizeSafe(this.address);
   }

   private static native float _normalizeSafe(long var0);

   public float normalizeFast() {
      this.checkNotNull();
      return _normalizeFast(this.address);
   }

   private static native float _normalizeFast(long var0);

   public PxVec3 multiply(PxVec3 a) {
      this.checkNotNull();
      return wrapPointer(_multiply(this.address, a.getAddress()));
   }

   private static native long _multiply(long var0, long var2);

   public PxVec3 minimum(PxVec3 v) {
      this.checkNotNull();
      return wrapPointer(_minimum(this.address, v.getAddress()));
   }

   private static native long _minimum(long var0, long var2);

   public float minElement() {
      this.checkNotNull();
      return _minElement(this.address);
   }

   private static native float _minElement(long var0);

   public PxVec3 maximum(PxVec3 v) {
      this.checkNotNull();
      return wrapPointer(_maximum(this.address, v.getAddress()));
   }

   private static native long _maximum(long var0, long var2);

   public float maxElement() {
      this.checkNotNull();
      return _maxElement(this.address);
   }

   private static native float _maxElement(long var0);

   public PxVec3 abs() {
      this.checkNotNull();
      return wrapPointer(_abs(this.address));
   }

   private static native long _abs(long var0);
}
