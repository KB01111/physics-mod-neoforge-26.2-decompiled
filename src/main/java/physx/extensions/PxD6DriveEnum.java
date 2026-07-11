package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6DriveEnum {
   eX(geteX()),
   eY(geteY()),
   eZ(geteZ()),
   eSWING(geteSWING()),
   eTWIST(geteTWIST()),
   eSLERP(geteSLERP());

   public final int value;

   private PxD6DriveEnum(int value) {
      this.value = value;
   }

   private static native int _geteX();

   private static int geteX() {
      Loader.load();
      return _geteX();
   }

   private static native int _geteY();

   private static int geteY() {
      Loader.load();
      return _geteY();
   }

   private static native int _geteZ();

   private static int geteZ() {
      Loader.load();
      return _geteZ();
   }

   private static native int _geteSWING();

   private static int geteSWING() {
      Loader.load();
      return _geteSWING();
   }

   private static native int _geteTWIST();

   private static int geteTWIST() {
      Loader.load();
      return _geteTWIST();
   }

   private static native int _geteSLERP();

   private static int geteSLERP() {
      Loader.load();
      return _geteSLERP();
   }

   public static PxD6DriveEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxD6DriveEnum: " + value);
   }
}
