package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXRoadGeometryQueryTypeEnum {
   eNONE(geteNONE()),
   eRAYCAST(geteRAYCAST()),
   eSWEEP(geteSWEEP());

   public final int value;

   private PxVehiclePhysXRoadGeometryQueryTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   private static native int _geteRAYCAST();

   private static int geteRAYCAST() {
      Loader.load();
      return _geteRAYCAST();
   }

   private static native int _geteSWEEP();

   private static int geteSWEEP() {
      Loader.load();
      return _geteSWEEP();
   }

   public static PxVehiclePhysXRoadGeometryQueryTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXRoadGeometryQueryTypeEnum: " + value);
   }
}
