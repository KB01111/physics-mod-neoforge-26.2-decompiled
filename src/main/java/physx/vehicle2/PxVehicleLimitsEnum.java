package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleLimitsEnum {
   eMAX_NB_WHEELS(geteMAX_NB_WHEELS()),
   eMAX_NB_AXLES(geteMAX_NB_AXLES());

   public final int value;

   private PxVehicleLimitsEnum(int value) {
      this.value = value;
   }

   private static native int _geteMAX_NB_WHEELS();

   private static int geteMAX_NB_WHEELS() {
      Loader.load();
      return _geteMAX_NB_WHEELS();
   }

   private static native int _geteMAX_NB_AXLES();

   private static int geteMAX_NB_AXLES() {
      Loader.load();
      return _geteMAX_NB_AXLES();
   }

   public static PxVehicleLimitsEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleLimitsEnum: " + value);
   }
}
