package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFrictionTypeEnum {
   ePATCH(getePATCH()),
   eONE_DIRECTIONAL(geteONE_DIRECTIONAL()),
   eTWO_DIRECTIONAL(geteTWO_DIRECTIONAL()),
   eFRICTION_COUNT(geteFRICTION_COUNT());

   public final int value;

   private PxFrictionTypeEnum(int value) {
      this.value = value;
   }

   private static native int _getePATCH();

   private static int getePATCH() {
      Loader.load();
      return _getePATCH();
   }

   private static native int _geteONE_DIRECTIONAL();

   private static int geteONE_DIRECTIONAL() {
      Loader.load();
      return _geteONE_DIRECTIONAL();
   }

   private static native int _geteTWO_DIRECTIONAL();

   private static int geteTWO_DIRECTIONAL() {
      Loader.load();
      return _geteTWO_DIRECTIONAL();
   }

   private static native int _geteFRICTION_COUNT();

   private static int geteFRICTION_COUNT() {
      Loader.load();
      return _geteFRICTION_COUNT();
   }

   public static PxFrictionTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxFrictionTypeEnum: " + value);
   }
}
