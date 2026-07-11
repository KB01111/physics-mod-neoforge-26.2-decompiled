package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorTypeEnum {
   eRIGID_STATIC(geteRIGID_STATIC()),
   eRIGID_DYNAMIC(geteRIGID_DYNAMIC()),
   eARTICULATION_LINK(geteARTICULATION_LINK()),
   eDEFORMABLE_SURFACE(geteDEFORMABLE_SURFACE()),
   eDEFORMABLE_VOLUME(geteDEFORMABLE_VOLUME()),
   eSOFTBODY(geteSOFTBODY()),
   ePBD_PARTICLESYSTEM(getePBD_PARTICLESYSTEM());

   public final int value;

   private PxActorTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteRIGID_STATIC();

   private static int geteRIGID_STATIC() {
      Loader.load();
      return _geteRIGID_STATIC();
   }

   private static native int _geteRIGID_DYNAMIC();

   private static int geteRIGID_DYNAMIC() {
      Loader.load();
      return _geteRIGID_DYNAMIC();
   }

   private static native int _geteARTICULATION_LINK();

   private static int geteARTICULATION_LINK() {
      Loader.load();
      return _geteARTICULATION_LINK();
   }

   private static native int _geteDEFORMABLE_SURFACE();

   private static int geteDEFORMABLE_SURFACE() {
      Loader.load();
      return _geteDEFORMABLE_SURFACE();
   }

   private static native int _geteDEFORMABLE_VOLUME();

   private static int geteDEFORMABLE_VOLUME() {
      Loader.load();
      return _geteDEFORMABLE_VOLUME();
   }

   private static native int _geteSOFTBODY();

   private static int geteSOFTBODY() {
      Loader.load();
      return _geteSOFTBODY();
   }

   private static native int _getePBD_PARTICLESYSTEM();

   private static int getePBD_PARTICLESYSTEM() {
      Loader.load();
      return _getePBD_PARTICLESYSTEM();
   }

   public static PxActorTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxActorTypeEnum: " + value);
   }
}
