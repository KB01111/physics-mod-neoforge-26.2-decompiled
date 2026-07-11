package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleCommandValueResponseTableEnum {
   eMAX_NB_SPEED_RESPONSES(geteMAX_NB_SPEED_RESPONSES());

   public final int value;

   private PxVehicleCommandValueResponseTableEnum(int value) {
      this.value = value;
   }

   private static native int _geteMAX_NB_SPEED_RESPONSES();

   private static int geteMAX_NB_SPEED_RESPONSES() {
      Loader.load();
      return _geteMAX_NB_SPEED_RESPONSES();
   }

   public static PxVehicleCommandValueResponseTableEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleCommandValueResponseTableEnum: " + value);
   }
}
