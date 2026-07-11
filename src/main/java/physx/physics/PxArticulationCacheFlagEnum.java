package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationCacheFlagEnum {
   eVELOCITY(geteVELOCITY()),
   eACCELERATION(geteACCELERATION()),
   ePOSITION(getePOSITION()),
   eFORCE(geteFORCE()),
   eLINK_VELOCITY(geteLINK_VELOCITY()),
   eLINK_ACCELERATION(geteLINK_ACCELERATION()),
   eROOT_TRANSFORM(geteROOT_TRANSFORM()),
   eROOT_VELOCITIES(geteROOT_VELOCITIES()),
   eLINK_INCOMING_JOINT_FORCE(geteLINK_INCOMING_JOINT_FORCE()),
   eJOINT_TARGET_POSITIONS(geteJOINT_TARGET_POSITIONS()),
   eJOINT_TARGET_VELOCITIES(geteJOINT_TARGET_VELOCITIES()),
   eALL(geteALL());

   public final int value;

   private PxArticulationCacheFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteVELOCITY();

   private static int geteVELOCITY() {
      Loader.load();
      return _geteVELOCITY();
   }

   private static native int _geteACCELERATION();

   private static int geteACCELERATION() {
      Loader.load();
      return _geteACCELERATION();
   }

   private static native int _getePOSITION();

   private static int getePOSITION() {
      Loader.load();
      return _getePOSITION();
   }

   private static native int _geteFORCE();

   private static int geteFORCE() {
      Loader.load();
      return _geteFORCE();
   }

   private static native int _geteLINK_VELOCITY();

   private static int geteLINK_VELOCITY() {
      Loader.load();
      return _geteLINK_VELOCITY();
   }

   private static native int _geteLINK_ACCELERATION();

   private static int geteLINK_ACCELERATION() {
      Loader.load();
      return _geteLINK_ACCELERATION();
   }

   private static native int _geteROOT_TRANSFORM();

   private static int geteROOT_TRANSFORM() {
      Loader.load();
      return _geteROOT_TRANSFORM();
   }

   private static native int _geteROOT_VELOCITIES();

   private static int geteROOT_VELOCITIES() {
      Loader.load();
      return _geteROOT_VELOCITIES();
   }

   private static native int _geteLINK_INCOMING_JOINT_FORCE();

   private static int geteLINK_INCOMING_JOINT_FORCE() {
      Loader.load();
      return _geteLINK_INCOMING_JOINT_FORCE();
   }

   private static native int _geteJOINT_TARGET_POSITIONS();

   private static int geteJOINT_TARGET_POSITIONS() {
      Loader.load();
      return _geteJOINT_TARGET_POSITIONS();
   }

   private static native int _geteJOINT_TARGET_VELOCITIES();

   private static int geteJOINT_TARGET_VELOCITIES() {
      Loader.load();
      return _geteJOINT_TARGET_VELOCITIES();
   }

   private static native int _geteALL();

   private static int geteALL() {
      Loader.load();
      return _geteALL();
   }

   public static PxArticulationCacheFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationCacheFlagEnum: " + value);
   }
}
