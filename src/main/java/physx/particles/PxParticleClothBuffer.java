package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;
import physx.common.PxVec4;

public class PxParticleClothBuffer extends PxParticleBuffer {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleClothBuffer() {
   }

   private static native int __sizeOf();

   public static PxParticleClothBuffer wrapPointer(long address) {
      return address != 0L ? new PxParticleClothBuffer(address) : null;
   }

   public static PxParticleClothBuffer arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleClothBuffer(long address) {
      super(address);
   }

   public PxVec4 getRestPositions() {
      this.checkNotNull();
      return PxVec4.wrapPointer(_getRestPositions(this.address));
   }

   private static native long _getRestPositions(long var0);

   public NativeObject getTriangles() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getTriangles(this.address));
   }

   private static native long _getTriangles(long var0);

   public void setNbTriangles(int nbTriangles) {
      this.checkNotNull();
      _setNbTriangles(this.address, nbTriangles);
   }

   private static native void _setNbTriangles(long var0, int var2);

   public int getNbTriangles() {
      this.checkNotNull();
      return _getNbTriangles(this.address);
   }

   private static native int _getNbTriangles(long var0);

   public int getNbSprings() {
      this.checkNotNull();
      return _getNbSprings(this.address);
   }

   private static native int _getNbSprings(long var0);

   public PxParticleSpring getSprings() {
      this.checkNotNull();
      return PxParticleSpring.wrapPointer(_getSprings(this.address));
   }

   private static native long _getSprings(long var0);

   public void setCloths(PxPartitionedParticleCloth cloths) {
      this.checkNotNull();
      _setCloths(this.address, cloths.getAddress());
   }

   private static native void _setCloths(long var0, long var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothBuffer");
   }
}
