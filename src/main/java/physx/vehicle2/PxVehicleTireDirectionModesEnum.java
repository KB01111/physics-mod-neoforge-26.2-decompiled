package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleTireDirectionModesEnum {
   eLONGITUDINAL(geteLONGITUDINAL()),
   eLATERAL(geteLATERAL());

   public final int value;

   private PxVehicleTireDirectionModesEnum(int value) {
      this.value = value;
   }

   private static native int _geteLONGITUDINAL();

   private static int geteLONGITUDINAL() {
      Loader.load();
      return _geteLONGITUDINAL();
   }

   private static native int _geteLATERAL();

   private static int geteLATERAL() {
      Loader.load();
      return _geteLATERAL();
   }

   public static PxVehicleTireDirectionModesEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleTireDirectionModesEnum: " + value);
   }
}
