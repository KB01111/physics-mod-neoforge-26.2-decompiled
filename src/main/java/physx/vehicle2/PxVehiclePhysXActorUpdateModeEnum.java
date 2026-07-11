package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXActorUpdateModeEnum {
   eAPPLY_VELOCITY(geteAPPLY_VELOCITY()),
   eAPPLY_ACCELERATION(geteAPPLY_ACCELERATION());

   public final int value;

   private PxVehiclePhysXActorUpdateModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteAPPLY_VELOCITY();

   private static int geteAPPLY_VELOCITY() {
      Loader.load();
      return _geteAPPLY_VELOCITY();
   }

   private static native int _geteAPPLY_ACCELERATION();

   private static int geteAPPLY_ACCELERATION() {
      Loader.load();
      return _geteAPPLY_ACCELERATION();
   }

   public static PxVehiclePhysXActorUpdateModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXActorUpdateModeEnum: " + value);
   }
}
