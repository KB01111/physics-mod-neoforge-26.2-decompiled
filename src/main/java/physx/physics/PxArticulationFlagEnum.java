package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationFlagEnum {
   eFIX_BASE(geteFIX_BASE()),
   eDRIVE_LIMITS_ARE_FORCES(geteDRIVE_LIMITS_ARE_FORCES()),
   eDISABLE_SELF_COLLISION(geteDISABLE_SELF_COLLISION());

   public final int value;

   private PxArticulationFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteFIX_BASE();

   private static int geteFIX_BASE() {
      Loader.load();
      return _geteFIX_BASE();
   }

   private static native int _geteDRIVE_LIMITS_ARE_FORCES();

   private static int geteDRIVE_LIMITS_ARE_FORCES() {
      Loader.load();
      return _geteDRIVE_LIMITS_ARE_FORCES();
   }

   private static native int _geteDISABLE_SELF_COLLISION();

   private static int geteDISABLE_SELF_COLLISION() {
      Loader.load();
      return _geteDISABLE_SELF_COLLISION();
   }

   public static PxArticulationFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationFlagEnum: " + value);
   }
}
