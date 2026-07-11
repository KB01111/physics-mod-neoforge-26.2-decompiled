package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleAxesEnum {
   ePosX(getePosX()),
   eNegX(geteNegX()),
   ePosY(getePosY()),
   eNegY(geteNegY()),
   ePosZ(getePosZ()),
   eNegZ(geteNegZ());

   public final int value;

   private PxVehicleAxesEnum(int value) {
      this.value = value;
   }

   private static native int _getePosX();

   private static int getePosX() {
      Loader.load();
      return _getePosX();
   }

   private static native int _geteNegX();

   private static int geteNegX() {
      Loader.load();
      return _geteNegX();
   }

   private static native int _getePosY();

   private static int getePosY() {
      Loader.load();
      return _getePosY();
   }

   private static native int _geteNegY();

   private static int geteNegY() {
      Loader.load();
      return _geteNegY();
   }

   private static native int _getePosZ();

   private static int getePosZ() {
      Loader.load();
      return _getePosZ();
   }

   private static native int _geteNegZ();

   private static int geteNegZ() {
      Loader.load();
      return _geteNegZ();
   }

   public static PxVehicleAxesEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleAxesEnum: " + value);
   }
}
