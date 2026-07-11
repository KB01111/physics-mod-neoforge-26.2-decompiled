package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehiclePhysXConstraintLimitsEnum {
   eNB_DOFS_PER_PXCONSTRAINT(geteNB_DOFS_PER_PXCONSTRAINT()),
   eNB_DOFS_PER_WHEEL(geteNB_DOFS_PER_WHEEL()),
   eNB_WHEELS_PER_PXCONSTRAINT(geteNB_WHEELS_PER_PXCONSTRAINT()),
   eNB_CONSTRAINTS_PER_VEHICLE(geteNB_CONSTRAINTS_PER_VEHICLE());

   public final int value;

   private PxVehiclePhysXConstraintLimitsEnum(int value) {
      this.value = value;
   }

   private static native int _geteNB_DOFS_PER_PXCONSTRAINT();

   private static int geteNB_DOFS_PER_PXCONSTRAINT() {
      Loader.load();
      return _geteNB_DOFS_PER_PXCONSTRAINT();
   }

   private static native int _geteNB_DOFS_PER_WHEEL();

   private static int geteNB_DOFS_PER_WHEEL() {
      Loader.load();
      return _geteNB_DOFS_PER_WHEEL();
   }

   private static native int _geteNB_WHEELS_PER_PXCONSTRAINT();

   private static int geteNB_WHEELS_PER_PXCONSTRAINT() {
      Loader.load();
      return _geteNB_WHEELS_PER_PXCONSTRAINT();
   }

   private static native int _geteNB_CONSTRAINTS_PER_VEHICLE();

   private static int geteNB_CONSTRAINTS_PER_VEHICLE() {
      Loader.load();
      return _geteNB_CONSTRAINTS_PER_VEHICLE();
   }

   public static PxVehiclePhysXConstraintLimitsEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehiclePhysXConstraintLimitsEnum: " + value);
   }
}
