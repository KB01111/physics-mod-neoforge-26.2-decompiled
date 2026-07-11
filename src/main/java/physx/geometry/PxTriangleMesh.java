package physx.geometry;

import physx.NativeObject;
import physx.common.PxBounds3;
import physx.common.PxRefCounted;
import physx.common.PxVec3;
import physx.support.PxU32ConstPtr;

public class PxTriangleMesh extends PxRefCounted {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTriangleMesh() {
   }

   private static native int __sizeOf();

   public static PxTriangleMesh wrapPointer(long address) {
      return address != 0L ? new PxTriangleMesh(address) : null;
   }

   public static PxTriangleMesh arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTriangleMesh(long address) {
      super(address);
   }

   public int getNbVertices() {
      this.checkNotNull();
      return _getNbVertices(this.address);
   }

   private static native int _getNbVertices(long var0);

   public PxVec3 getVertices() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getVertices(this.address));
   }

   private static native long _getVertices(long var0);

   public PxVec3 getVerticesForModification() {
      this.checkNotNull();
      return PxVec3.wrapPointer(_getVerticesForModification(this.address));
   }

   private static native long _getVerticesForModification(long var0);

   public PxBounds3 refitBVH() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_refitBVH(this.address));
   }

   private static native long _refitBVH(long var0);

   public int getNbTriangles() {
      this.checkNotNull();
      return _getNbTriangles(this.address);
   }

   private static native int _getNbTriangles(long var0);

   public NativeObject getTriangles() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getTriangles(this.address));
   }

   private static native long _getTriangles(long var0);

   public PxTriangleMeshFlags getTriangleMeshFlags() {
      this.checkNotNull();
      return PxTriangleMeshFlags.wrapPointer(_getTriangleMeshFlags(this.address));
   }

   private static native long _getTriangleMeshFlags(long var0);

   public PxU32ConstPtr getTrianglesRemap() {
      this.checkNotNull();
      return PxU32ConstPtr.wrapPointer(_getTrianglesRemap(this.address));
   }

   private static native long _getTrianglesRemap(long var0);

   public short getTriangleMaterialIndex(int triangleIndex) {
      this.checkNotNull();
      return _getTriangleMaterialIndex(this.address, triangleIndex);
   }

   private static native short _getTriangleMaterialIndex(long var0, int var2);

   public PxBounds3 getLocalBounds() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getLocalBounds(this.address));
   }

   private static native long _getLocalBounds(long var0);
}
