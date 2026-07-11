package physx.particles;

import de.fabmax.physxjni.Loader;
import physx.PlatformChecks;

public enum PxParticlePhaseFlagEnum {
   eParticlePhaseGroupMask(geteParticlePhaseGroupMask()),
   eParticlePhaseFlagsMask(geteParticlePhaseFlagsMask()),
   eParticlePhaseSelfCollide(geteParticlePhaseSelfCollide()),
   eParticlePhaseSelfCollideFilter(geteParticlePhaseSelfCollideFilter()),
   eParticlePhaseFluid(geteParticlePhaseFluid());

   public final int value;

   private PxParticlePhaseFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteParticlePhaseGroupMask();

   private static int geteParticlePhaseGroupMask() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
      return _geteParticlePhaseGroupMask();
   }

   private static native int _geteParticlePhaseFlagsMask();

   private static int geteParticlePhaseFlagsMask() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
      return _geteParticlePhaseFlagsMask();
   }

   private static native int _geteParticlePhaseSelfCollide();

   private static int geteParticlePhaseSelfCollide() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
      return _geteParticlePhaseSelfCollide();
   }

   private static native int _geteParticlePhaseSelfCollideFilter();

   private static int geteParticlePhaseSelfCollideFilter() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
      return _geteParticlePhaseSelfCollideFilter();
   }

   private static native int _geteParticlePhaseFluid();

   private static int geteParticlePhaseFluid() {
      Loader.load();
      PlatformChecks.requirePlatform(3, "physx.particles.PxParticlePhaseFlagEnum");
      return _geteParticlePhaseFluid();
   }

   public static PxParticlePhaseFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxParticlePhaseFlagEnum: " + value);
   }
}
