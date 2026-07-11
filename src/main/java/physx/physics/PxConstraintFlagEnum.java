package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxConstraintFlagEnum {
   eBROKEN(geteBROKEN()),
   eCOLLISION_ENABLED(geteCOLLISION_ENABLED()),
   eVISUALIZATION(geteVISUALIZATION()),
   eDRIVE_LIMITS_ARE_FORCES(geteDRIVE_LIMITS_ARE_FORCES()),
   eIMPROVED_SLERP(geteIMPROVED_SLERP()),
   eDISABLE_PREPROCESSING(geteDISABLE_PREPROCESSING()),
   eENABLE_EXTENDED_LIMITS(geteENABLE_EXTENDED_LIMITS()),
   eGPU_COMPATIBLE(geteGPU_COMPATIBLE()),
   eALWAYS_UPDATE(geteALWAYS_UPDATE()),
   eDISABLE_CONSTRAINT(geteDISABLE_CONSTRAINT());

   public final int value;

   private PxConstraintFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteBROKEN();

   private static int geteBROKEN() {
      Loader.load();
      return _geteBROKEN();
   }

   private static native int _geteCOLLISION_ENABLED();

   private static int geteCOLLISION_ENABLED() {
      Loader.load();
      return _geteCOLLISION_ENABLED();
   }

   private static native int _geteVISUALIZATION();

   private static int geteVISUALIZATION() {
      Loader.load();
      return _geteVISUALIZATION();
   }

   private static native int _geteDRIVE_LIMITS_ARE_FORCES();

   private static int geteDRIVE_LIMITS_ARE_FORCES() {
      Loader.load();
      return _geteDRIVE_LIMITS_ARE_FORCES();
   }

   private static native int _geteIMPROVED_SLERP();

   private static int geteIMPROVED_SLERP() {
      Loader.load();
      return _geteIMPROVED_SLERP();
   }

   private static native int _geteDISABLE_PREPROCESSING();

   private static int geteDISABLE_PREPROCESSING() {
      Loader.load();
      return _geteDISABLE_PREPROCESSING();
   }

   private static native int _geteENABLE_EXTENDED_LIMITS();

   private static int geteENABLE_EXTENDED_LIMITS() {
      Loader.load();
      return _geteENABLE_EXTENDED_LIMITS();
   }

   private static native int _geteGPU_COMPATIBLE();

   private static int geteGPU_COMPATIBLE() {
      Loader.load();
      return _geteGPU_COMPATIBLE();
   }

   private static native int _geteALWAYS_UPDATE();

   private static int geteALWAYS_UPDATE() {
      Loader.load();
      return _geteALWAYS_UPDATE();
   }

   private static native int _geteDISABLE_CONSTRAINT();

   private static int geteDISABLE_CONSTRAINT() {
      Loader.load();
      return _geteDISABLE_CONSTRAINT();
   }

   public static PxConstraintFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxConstraintFlagEnum: " + value);
   }
}
