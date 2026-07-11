package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxSceneFlagEnum {
   eENABLE_ACTIVE_ACTORS(geteENABLE_ACTIVE_ACTORS()),
   eENABLE_CCD(geteENABLE_CCD()),
   eDISABLE_CCD_RESWEEP(geteDISABLE_CCD_RESWEEP()),
   eENABLE_PCM(geteENABLE_PCM()),
   eDISABLE_CONTACT_REPORT_BUFFER_RESIZE(geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE()),
   eDISABLE_CONTACT_CACHE(geteDISABLE_CONTACT_CACHE()),
   eREQUIRE_RW_LOCK(geteREQUIRE_RW_LOCK()),
   eENABLE_STABILIZATION(geteENABLE_STABILIZATION()),
   eENABLE_AVERAGE_POINT(geteENABLE_AVERAGE_POINT()),
   eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS(geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()),
   eENABLE_GPU_DYNAMICS(geteENABLE_GPU_DYNAMICS()),
   eENABLE_ENHANCED_DETERMINISM(geteENABLE_ENHANCED_DETERMINISM()),
   eENABLE_FRICTION_EVERY_ITERATION(geteENABLE_FRICTION_EVERY_ITERATION()),
   eENABLE_DIRECT_GPU_API(geteENABLE_DIRECT_GPU_API()),
   eMUTABLE_FLAGS(geteMUTABLE_FLAGS());

   public final int value;

   private PxSceneFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteENABLE_ACTIVE_ACTORS();

   private static int geteENABLE_ACTIVE_ACTORS() {
      Loader.load();
      return _geteENABLE_ACTIVE_ACTORS();
   }

   private static native int _geteENABLE_CCD();

   private static int geteENABLE_CCD() {
      Loader.load();
      return _geteENABLE_CCD();
   }

   private static native int _geteDISABLE_CCD_RESWEEP();

   private static int geteDISABLE_CCD_RESWEEP() {
      Loader.load();
      return _geteDISABLE_CCD_RESWEEP();
   }

   private static native int _geteENABLE_PCM();

   private static int geteENABLE_PCM() {
      Loader.load();
      return _geteENABLE_PCM();
   }

   private static native int _geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE();

   private static int geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE() {
      Loader.load();
      return _geteDISABLE_CONTACT_REPORT_BUFFER_RESIZE();
   }

   private static native int _geteDISABLE_CONTACT_CACHE();

   private static int geteDISABLE_CONTACT_CACHE() {
      Loader.load();
      return _geteDISABLE_CONTACT_CACHE();
   }

   private static native int _geteREQUIRE_RW_LOCK();

   private static int geteREQUIRE_RW_LOCK() {
      Loader.load();
      return _geteREQUIRE_RW_LOCK();
   }

   private static native int _geteENABLE_STABILIZATION();

   private static int geteENABLE_STABILIZATION() {
      Loader.load();
      return _geteENABLE_STABILIZATION();
   }

   private static native int _geteENABLE_AVERAGE_POINT();

   private static int geteENABLE_AVERAGE_POINT() {
      Loader.load();
      return _geteENABLE_AVERAGE_POINT();
   }

   private static native int _geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS();

   private static int geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS() {
      Loader.load();
      return _geteEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS();
   }

   private static native int _geteENABLE_GPU_DYNAMICS();

   private static int geteENABLE_GPU_DYNAMICS() {
      Loader.load();
      return _geteENABLE_GPU_DYNAMICS();
   }

   private static native int _geteENABLE_ENHANCED_DETERMINISM();

   private static int geteENABLE_ENHANCED_DETERMINISM() {
      Loader.load();
      return _geteENABLE_ENHANCED_DETERMINISM();
   }

   private static native int _geteENABLE_FRICTION_EVERY_ITERATION();

   private static int geteENABLE_FRICTION_EVERY_ITERATION() {
      Loader.load();
      return _geteENABLE_FRICTION_EVERY_ITERATION();
   }

   private static native int _geteENABLE_DIRECT_GPU_API();

   private static int geteENABLE_DIRECT_GPU_API() {
      Loader.load();
      return _geteENABLE_DIRECT_GPU_API();
   }

   private static native int _geteMUTABLE_FLAGS();

   private static int geteMUTABLE_FLAGS() {
      Loader.load();
      return _geteMUTABLE_FLAGS();
   }

   public static PxSceneFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxSceneFlagEnum: " + value);
   }
}
