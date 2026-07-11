package physx.common;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxPlane extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxPlane wrapPointer(long address) {
      return address != 0L ? new PxPlane(address) : null;
   }

   public static PxPlane arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxPlane(long address) {
      super(address);
   }

   public static PxPlane createAt(long address) {
      __placement_new_PxPlane(address);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPlane createAt(T allocator, NativeObject.Allocator<T> allocate) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPlane(address);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPlane(long var0);

   public static PxPlane createAt(long address, float nx, float ny, float nz, float distance) {
      __placement_new_PxPlane(address, nx, ny, nz, distance);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPlane createAt(T allocator, NativeObject.Allocator<T> allocate, float nx, float ny, float nz, float distance) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPlane(address, nx, ny, nz, distance);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPlane(long var0, float var2, float var3, float var4, float var5);

   public static PxPlane createAt(long address, PxVec3 normal, float distance) {
      __placement_new_PxPlane(address, normal.getAddress(), distance);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPlane createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 normal, float distance) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPlane(address, normal.getAddress(), distance);
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPlane(long var0, long var2, float var4);

   public static PxPlane createAt(long address, PxVec3 p0, PxVec3 p1, PxVec3 p2) {
      __placement_new_PxPlane(address, p0.getAddress(), p1.getAddress(), p2.getAddress());
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPlane createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 p0, PxVec3 p1, PxVec3 p2) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPlane(address, p0.getAddress(), p1.getAddress(), p2.getAddress());
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPlane(long var0, long var2, long var4, long var6);

   public static PxPlane createAt(long address, PxVec3 point, PxVec3 normal) {
      PlatformChecks.requirePlatform(15, "physx.common.PxPlane");
      __placement_new_PxPlane(address, point.getAddress(), normal.getAddress());
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   public static <T> PxPlane createAt(T allocator, NativeObject.Allocator<T> allocate, PxVec3 point, PxVec3 normal) {
      long address = allocate.on(allocator, 8, SIZEOF);
      __placement_new_PxPlane(address, point.getAddress(), normal.getAddress());
      PxPlane createdObj = wrapPointer(address);
      createdObj.isExternallyAllocated = true;
      return createdObj;
   }

   private static native void __placement_new_PxPlane(long var0, long var2, long var4);

   public PxPlane() {
      this.address = _PxPlane();
   }

   private static native long _PxPlane();

   public PxPlane(float nx, float ny, float nz, float distance) {
      this.address = _PxPlane(nx, ny, nz, distance);
   }

   private static native long _PxPlane(float var0, float var1, float var2, float var3);

   public PxPlane(PxVec3 normal, float distance) {
      this.address = _PxPlane(normal.getAddress(), distance);
   }

   private static native long _PxPlane(long var0, float var2);

   public PxPlane(PxVec3 p0, PxVec3 p1, PxVec3 p2) {
      this.address = _PxPlane(p0.getAddress(), p1.getAddress(), p2.getAddress());
   }

   private static native long _PxPlane(long var0, long var2, long var4);

   public PxPlane(PxVec3 point, PxVec3 normal) {
      PlatformChecks.requirePlatform(15, "physx.common.PxPlane");
      this.address = _PxPlane(point.getAddress(), normal.getAddress());
   }

   private static native long _PxPlane(long var0, long var2);

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

   public float getD() {
      this.checkNotNull();
      return _getD(this.address);
   }

   private static native float _getD(long var0);

   public void setD(float value) {
      this.checkNotNull();
      _setD(this.address, value);
   }

   private static native void _setD(long var0, float var2);

   public float distance(PxVec3 p) {
      this.checkNotNull();
      return _distance(this.address, p.getAddress());
   }

   private static native float _distance(long var0, long var2);

   public boolean contains(PxVec3 p) {
      this.checkNotNull();
      return _contains(this.address, p.getAddress());
   }

   private static native boolean _contains(long var0, long var2);

   public PxVec3 project(PxVec3 p) {
      this.checkNotNull();
      return PxVec3.wrapPointer(_project(this.address, p.getAddress()));
   }

   private static native long _project(long var0, long var2);

   public PxVec3 pointInPlane() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_pointInPlane(this.address));
   }

   private static native long _pointInPlane(long var0);

   public void normalize() {
      this.checkNotNull();
      _normalize(this.address);
   }

   private static native void _normalize(long var0);

   public PxPlane transform(PxTransform pose) {
      this.checkNotNull();
      return wrapPointer(_transform(this.address, pose.getAddress()));
   }

   private static native long _transform(long var0, long var2);

   public PxPlane inverseTransform(PxTransform pose) {
      this.checkNotNull();
      return wrapPointer(_inverseTransform(this.address, pose.getAddress()));
   }

   private static native long _inverseTransform(long var0, long var2);
}
