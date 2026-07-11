package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleCommandNonLinearResponseParamsEnum {
   eMAX_NB_COMMAND_VALUES(geteMAX_NB_COMMAND_VALUES());

   public final int value;

   private PxVehicleCommandNonLinearResponseParamsEnum(int value) {
      this.value = value;
   }

   private static native int _geteMAX_NB_COMMAND_VALUES();

   private static int geteMAX_NB_COMMAND_VALUES() {
      Loader.load();
      return _geteMAX_NB_COMMAND_VALUES();
   }

   public static PxVehicleCommandNonLinearResponseParamsEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleCommandNonLinearResponseParamsEnum: " + value);
   }
}
