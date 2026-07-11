package physx.geometry;

import physx.NativeObject;

public class PxGeometryHolder extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxGeometryHolder wrapPointer(long address) {
      return address != 0L ? new PxGeometryHolder(address) : null;
   }

   public static PxGeometryHolder arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxGeometryHolder(long address) {
      super(address);
   }

   public PxGeometryHolder() {
      this.address = _PxGeometryHolder();
   }

   private static native long _PxGeometryHolder();

   public PxGeometryHolder(PxGeometry geometry) {
      this.address = _PxGeometryHolder(geometry.getAddress());
   }

   private static native long _PxGeometryHolder(long var0);

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

   public PxGeometryTypeEnum getType() {
      this.checkNotNull();
      return PxGeometryTypeEnum.forValue(_getType(this.address));
   }

   private static native int _getType(long var0);

   public PxSphereGeometry sphere() {
      this.checkNotNull();
      return PxSphereGeometry.wrapPointer(_sphere(this.address));
   }

   private static native long _sphere(long var0);

   public PxPlaneGeometry plane() {
      this.checkNotNull();
      return PxPlaneGeometry.wrapPointer(_plane(this.address));
   }

   private static native long _plane(long var0);

   public PxCapsuleGeometry capsule() {
      this.checkNotNull();
      return PxCapsuleGeometry.wrapPointer(_capsule(this.address));
   }

   private static native long _capsule(long var0);

   public PxBoxGeometry box() {
      this.checkNotNull();
      return PxBoxGeometry.wrapPointer(_box(this.address));
   }

   private static native long _box(long var0);

   public PxConvexMeshGeometry convexMesh() {
      this.checkNotNull();
      return PxConvexMeshGeometry.wrapPointer(_convexMesh(this.address));
   }

   private static native long _convexMesh(long var0);

   public PxTriangleMeshGeometry triangleMesh() {
      this.checkNotNull();
      return PxTriangleMeshGeometry.wrapPointer(_triangleMesh(this.address));
   }

   private static native long _triangleMesh(long var0);

   public PxHeightFieldGeometry heightField() {
      this.checkNotNull();
      return PxHeightFieldGeometry.wrapPointer(_heightField(this.address));
   }

   private static native long _heightField(long var0);

   public void storeAny(PxGeometry geometry) {
      this.checkNotNull();
      _storeAny(this.address, geometry.getAddress());
   }

   private static native void _storeAny(long var0, long var2);
}
