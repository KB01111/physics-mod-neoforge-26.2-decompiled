package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticleFlagEnum {
   eDISABLE_SELF_COLLISION(geteDISABLE_SELF_COLLISION()),
   eDISABLE_RIGID_COLLISION(geteDISABLE_RIGID_COLLISION()),
   eFULL_DIFFUSE_ADVECTION(geteFULL_DIFFUSE_ADVECTION());

   public final int value;

   private PxParticleFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteDISABLE_SELF_COLLISION();

   private static int geteDISABLE_SELF_COLLISION() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
      return _geteDISABLE_SELF_COLLISION();
   }

   private static native int _geteDISABLE_RIGID_COLLISION();

   private static int geteDISABLE_RIGID_COLLISION() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
      return _geteDISABLE_RIGID_COLLISION();
   }

   private static native int _geteFULL_DIFFUSE_ADVECTION();

   private static int geteFULL_DIFFUSE_ADVECTION() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticleFlagEnum");
      return _geteFULL_DIFFUSE_ADVECTION();
   }

   public static PxParticleFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxParticleFlagEnum: " + value);
   }
}
