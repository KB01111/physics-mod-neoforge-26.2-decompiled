package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleSuspensionJounceCalculationTypeEnum {
   eRAYCAST(geteRAYCAST()),
   eSWEEP(geteSWEEP());

   public final int value;

   private PxVehicleSuspensionJounceCalculationTypeEnum(int value) {
      this.value = value;
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

   public static PxVehicleSuspensionJounceCalculationTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleSuspensionJounceCalculationTypeEnum: " + value);
   }
}
