package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum {
   eSUSPENSION(geteSUSPENSION()),
   eROAD_GEOMETRY_NORMAL(geteROAD_GEOMETRY_NORMAL()),
   eNONE(geteNONE());

   public final int value;

   private PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(int value) {
      this.value = value;
   }

   private static native int _geteSUSPENSION();

   private static int geteSUSPENSION() {
      Loader.load();
      return _geteSUSPENSION();
   }

   private static native int _geteROAD_GEOMETRY_NORMAL();

   private static int geteROAD_GEOMETRY_NORMAL() {
      Loader.load();
      return _geteROAD_GEOMETRY_NORMAL();
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   public static PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum: " + value);
   }
}
