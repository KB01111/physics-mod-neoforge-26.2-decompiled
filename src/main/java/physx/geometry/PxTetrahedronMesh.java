package physx.geometry;

import physx.NativeObject;
import physx.common.PxBounds3;
import physx.common.PxRefCounted;
import physx.common.PxVec3;
import physx.support.PxU32ConstPtr;

public class PxTetrahedronMesh extends PxRefCounted {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxTetrahedronMesh() {
   }

   private static native int __sizeOf();

   public static PxTetrahedronMesh wrapPointer(long address) {
      return address != 0L ? new PxTetrahedronMesh(address) : null;
   }

   public static PxTetrahedronMesh arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxTetrahedronMesh(long address) {
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

   public int getNbTetrahedrons() {
      this.checkNotNull();
      return _getNbTetrahedrons(this.address);
   }

   private static native int _getNbTetrahedrons(long var0);

   public NativeObject getTetrahedrons() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getTetrahedrons(this.address));
   }

   private static native long _getTetrahedrons(long var0);

   public PxTetrahedronMeshFlags getTetrahedronMeshFlags() {
      this.checkNotNull();
      return PxTetrahedronMeshFlags.wrapPointer(_getTetrahedronMeshFlags(this.address));
   }

   private static native long _getTetrahedronMeshFlags(long var0);

   public PxU32ConstPtr getTetrahedraRemap() {
      this.checkNotNull();
      return PxU32ConstPtr.wrapPointer(_getTetrahedraRemap(this.address));
   }

   private static native long _getTetrahedraRemap(long var0);

   public PxBounds3 getLocalBounds() {
      this.checkNotNull();
      return PxBounds3.wrapPointer(_getLocalBounds(this.address));
   }

   private static native long _getLocalBounds(long var0);
}
