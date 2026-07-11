package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleClothCooker extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   protected PxParticleClothCooker() {
   }

   private static native int __sizeOf();

   public static PxParticleClothCooker wrapPointer(long address) {
      return address != 0L ? new PxParticleClothCooker(address) : null;
   }

   public static PxParticleClothCooker arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleClothCooker(long address) {
      super(address);
   }

   public void release() {
      this.checkNotNull();
      _release(this.address);
   }

   private static native void _release(long var0);

   public void cookConstraints() {
      this.checkNotNull();
      _cookConstraints(this.address);
   }

   private static native void _cookConstraints(long var0);

   public void cookConstraints(PxParticleClothConstraint constraints) {
      this.checkNotNull();
      _cookConstraints(this.address, constraints.getAddress());
   }

   private static native void _cookConstraints(long var0, long var2);

   public void cookConstraints(PxParticleClothConstraint constraints, int numConstraints) {
      this.checkNotNull();
      _cookConstraints(this.address, constraints.getAddress(), numConstraints);
   }

   private static native void _cookConstraints(long var0, long var2, int var4);

   public NativeObject getTriangleIndices() {
      this.checkNotNull();
      return NativeObject.wrapPointer(_getTriangleIndices(this.address));
   }

   private static native long _getTriangleIndices(long var0);

   public int getTriangleIndicesCount() {
      this.checkNotNull();
      return _getTriangleIndicesCount(this.address);
   }

   private static native int _getTriangleIndicesCount(long var0);

   public PxParticleClothConstraint getConstraints() {
      this.checkNotNull();
      return PxParticleClothConstraint.wrapPointer(_getConstraints(this.address));
   }

   private static native long _getConstraints(long var0);

   public int getConstraintCount() {
      this.checkNotNull();
      return _getConstraintCount(this.address);
   }

   private static native int _getConstraintCount(long var0);

   public void calculateMeshVolume() {
      this.checkNotNull();
      _calculateMeshVolume(this.address);
   }

   private static native void _calculateMeshVolume(long var0);

   public float getMeshVolume() {
      this.checkNotNull();
      return _getMeshVolume(this.address);
   }

   private static native float _getMeshVolume(long var0);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothCooker");
   }
}
