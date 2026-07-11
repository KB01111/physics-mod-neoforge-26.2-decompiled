package physx.particles;

import physx.NativeObject;
import physx.PlatformChecks;

public class PxParticleClothConstraint extends NativeObject {
   public static final int SIZEOF = __sizeOf();
   public static final int ALIGNOF = 8;

   private static native int __sizeOf();

   public static PxParticleClothConstraint wrapPointer(long address) {
      return address != 0L ? new PxParticleClothConstraint(address) : null;
   }

   public static PxParticleClothConstraint arrayGet(long baseAddress, int index) {
      if (baseAddress == 0L) {
         throw new NullPointerException("baseAddress is 0");
      } else {
         return wrapPointer(baseAddress + (long)SIZEOF * (long)index);
      }
   }

   protected PxParticleClothConstraint(long address) {
      super(address);
   }

   public PxParticleClothConstraint() {
      this.address = _PxParticleClothConstraint();
   }

   private static native long _PxParticleClothConstraint();

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

   public int getParticleIndexA() {
      this.checkNotNull();
      return _getParticleIndexA(this.address);
   }

   private static native int _getParticleIndexA(long var0);

   public void setParticleIndexA(int value) {
      this.checkNotNull();
      _setParticleIndexA(this.address, value);
   }

   private static native void _setParticleIndexA(long var0, int var2);

   public int getParticleIndexB() {
      this.checkNotNull();
      return _getParticleIndexB(this.address);
   }

   private static native int _getParticleIndexB(long var0);

   public void setParticleIndexB(int value) {
      this.checkNotNull();
      _setParticleIndexB(this.address, value);
   }

   private static native void _setParticleIndexB(long var0, int var2);

   public float getLength() {
      this.checkNotNull();
      return _getLength(this.address);
   }

   private static native float _getLength(long var0);

   public void setLength(float value) {
      this.checkNotNull();
      _setLength(this.address, value);
   }

   private static native void _setLength(long var0, float var2);

   public int getConstraintType() {
      this.checkNotNull();
      return _getConstraintType(this.address);
   }

   private static native int _getConstraintType(long var0);

   public void setConstraintType(int value) {
      this.checkNotNull();
      _setConstraintType(this.address, value);
   }

   private static native void _setConstraintType(long var0, int var2);

   static {
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraint");
   }
}
