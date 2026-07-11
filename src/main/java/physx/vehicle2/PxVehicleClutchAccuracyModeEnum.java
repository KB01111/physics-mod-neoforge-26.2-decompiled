package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleClutchAccuracyModeEnum {
   eESTIMATE(geteESTIMATE()),
   eBEST_POSSIBLE(geteBEST_POSSIBLE());

   public final int value;

   private PxVehicleClutchAccuracyModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteESTIMATE();

   private static int geteESTIMATE() {
      Loader.load();
      return _geteESTIMATE();
   }

   private static native int _geteBEST_POSSIBLE();

   private static int geteBEST_POSSIBLE() {
      Loader.load();
      return _geteBEST_POSSIBLE();
   }

   public static PxVehicleClutchAccuracyModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleClutchAccuracyModeEnum: " + value);
   }
}
