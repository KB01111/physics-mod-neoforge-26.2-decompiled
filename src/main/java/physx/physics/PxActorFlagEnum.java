package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxActorFlagEnum {
   eVISUALIZATION(geteVISUALIZATION()),
   eDISABLE_GRAVITY(geteDISABLE_GRAVITY()),
   eSEND_SLEEP_NOTIFIES(geteSEND_SLEEP_NOTIFIES()),
   eDISABLE_SIMULATION(geteDISABLE_SIMULATION());

   public final int value;

   private PxActorFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteVISUALIZATION();

   private static int geteVISUALIZATION() {
      Loader.load();
      return _geteVISUALIZATION();
   }

   private static native int _geteDISABLE_GRAVITY();

   private static int geteDISABLE_GRAVITY() {
      Loader.load();
      return _geteDISABLE_GRAVITY();
   }

   private static native int _geteSEND_SLEEP_NOTIFIES();

   private static int geteSEND_SLEEP_NOTIFIES() {
      Loader.load();
      return _geteSEND_SLEEP_NOTIFIES();
   }

   private static native int _geteDISABLE_SIMULATION();

   private static int geteDISABLE_SIMULATION() {
      Loader.load();
      return _geteDISABLE_SIMULATION();
   }

   public static PxActorFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxActorFlagEnum: " + value);
   }
}
