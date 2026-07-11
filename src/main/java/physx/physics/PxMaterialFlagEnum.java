package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxMaterialFlagEnum {
   eDISABLE_FRICTION(geteDISABLE_FRICTION()),
   eDISABLE_STRONG_FRICTION(geteDISABLE_STRONG_FRICTION()),
   eIMPROVED_PATCH_FRICTION(geteIMPROVED_PATCH_FRICTION());

   public final int value;

   private PxMaterialFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteDISABLE_FRICTION();

   private static int geteDISABLE_FRICTION() {
      Loader.load();
      return _geteDISABLE_FRICTION();
   }

   private static native int _geteDISABLE_STRONG_FRICTION();

   private static int geteDISABLE_STRONG_FRICTION() {
      Loader.load();
      return _geteDISABLE_STRONG_FRICTION();
   }

   private static native int _geteIMPROVED_PATCH_FRICTION();

   private static int geteIMPROVED_PATCH_FRICTION() {
      Loader.load();
      return _geteIMPROVED_PATCH_FRICTION();
   }

   public static PxMaterialFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxMaterialFlagEnum: " + value);
   }
}
