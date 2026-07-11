package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleDirectDriveTransmissionCommandStateEnum {
   eREVERSE(geteREVERSE()),
   eNEUTRAL(geteNEUTRAL()),
   eFORWARD(geteFORWARD());

   public final int value;

   private PxVehicleDirectDriveTransmissionCommandStateEnum(int value) {
      this.value = value;
   }

   private static native int _geteREVERSE();

   private static int geteREVERSE() {
      Loader.load();
      return _geteREVERSE();
   }

   private static native int _geteNEUTRAL();

   private static int geteNEUTRAL() {
      Loader.load();
      return _geteNEUTRAL();
   }

   private static native int _geteFORWARD();

   private static int geteFORWARD() {
      Loader.load();
      return _geteFORWARD();
   }

   public static PxVehicleDirectDriveTransmissionCommandStateEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleDirectDriveTransmissionCommandStateEnum: " + value);
   }
}
