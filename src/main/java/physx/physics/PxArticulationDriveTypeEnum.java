package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationDriveTypeEnum {
   eFORCE(geteFORCE()),
   eACCELERATION(geteACCELERATION()),
   @Deprecated
   eTARGET(geteTARGET()),
   @Deprecated
   eVELOCITY(geteVELOCITY()),
   eNONE(geteNONE());

   public final int value;

   private PxArticulationDriveTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteFORCE();

   private static int geteFORCE() {
      Loader.load();
      return _geteFORCE();
   }

   private static native int _geteACCELERATION();

   private static int geteACCELERATION() {
      Loader.load();
      return _geteACCELERATION();
   }

   private static native int _geteTARGET();

   private static int geteTARGET() {
      Loader.load();
      return _geteTARGET();
   }

   private static native int _geteVELOCITY();

   private static int geteVELOCITY() {
      Loader.load();
      return _geteVELOCITY();
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   public static PxArticulationDriveTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationDriveTypeEnum: " + value);
   }
}
