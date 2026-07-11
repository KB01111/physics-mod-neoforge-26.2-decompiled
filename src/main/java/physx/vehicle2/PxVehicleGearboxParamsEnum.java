package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleGearboxParamsEnum {
   eMAX_NB_GEARS(geteMAX_NB_GEARS());

   public final int value;

   private PxVehicleGearboxParamsEnum(int value) {
      this.value = value;
   }

   private static native int _geteMAX_NB_GEARS();

   private static int geteMAX_NB_GEARS() {
      Loader.load();
      return _geteMAX_NB_GEARS();
   }

   public static PxVehicleGearboxParamsEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleGearboxParamsEnum: " + value);
   }
}
