package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleSolverTypeEnum {
   ePBD(getePBD());

   public final int value;

   private PxParticleSolverTypeEnum(int value) {
      this.value = value;
   }

   private static native int _getePBD();

   private static int getePBD() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleSolverTypeEnum");
      return _getePBD();
   }

   public static PxParticleSolverTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxParticleSolverTypeEnum: " + value);
   }
}
