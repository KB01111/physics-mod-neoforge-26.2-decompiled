package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleClothConstraintEnum {
   eTYPE_INVALID_CONSTRAINT(geteTYPE_INVALID_CONSTRAINT()),
   eTYPE_HORIZONTAL_CONSTRAINT(geteTYPE_HORIZONTAL_CONSTRAINT()),
   eTYPE_VERTICAL_CONSTRAINT(geteTYPE_VERTICAL_CONSTRAINT()),
   eTYPE_DIAGONAL_CONSTRAINT(geteTYPE_DIAGONAL_CONSTRAINT()),
   eTYPE_BENDING_CONSTRAINT(geteTYPE_BENDING_CONSTRAINT()),
   eTYPE_DIAGONAL_BENDING_CONSTRAINT(geteTYPE_DIAGONAL_BENDING_CONSTRAINT()),
   eTYPE_ALL(geteTYPE_ALL());

   public final int value;

   private PxParticleClothConstraintEnum(int value) {
      this.value = value;
   }

   private static native int _geteTYPE_INVALID_CONSTRAINT();

   private static int geteTYPE_INVALID_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_INVALID_CONSTRAINT();
   }

   private static native int _geteTYPE_HORIZONTAL_CONSTRAINT();

   private static int geteTYPE_HORIZONTAL_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_HORIZONTAL_CONSTRAINT();
   }

   private static native int _geteTYPE_VERTICAL_CONSTRAINT();

   private static int geteTYPE_VERTICAL_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_VERTICAL_CONSTRAINT();
   }

   private static native int _geteTYPE_DIAGONAL_CONSTRAINT();

   private static int geteTYPE_DIAGONAL_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_DIAGONAL_CONSTRAINT();
   }

   private static native int _geteTYPE_BENDING_CONSTRAINT();

   private static int geteTYPE_BENDING_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_BENDING_CONSTRAINT();
   }

   private static native int _geteTYPE_DIAGONAL_BENDING_CONSTRAINT();

   private static int geteTYPE_DIAGONAL_BENDING_CONSTRAINT() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_DIAGONAL_BENDING_CONSTRAINT();
   }

   private static native int _geteTYPE_ALL();

   private static int geteTYPE_ALL() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleClothConstraintEnum");
      return _geteTYPE_ALL();
   }

   public static PxParticleClothConstraintEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxParticleClothConstraintEnum: " + value);
   }
}
