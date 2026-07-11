package physx.vehicle2;

import de.fabmax.physxjni.Loader;

public enum PxVehicleSimulationContextTypeEnum {
   eDEFAULT(geteDEFAULT()),
   ePHYSX(getePHYSX());

   public final int value;

   private PxVehicleSimulationContextTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteDEFAULT();

   private static int geteDEFAULT() {
      Loader.load();
      return _geteDEFAULT();
   }

   private static native int _getePHYSX();

   private static int getePHYSX() {
      Loader.load();
      return _getePHYSX();
   }

   public static PxVehicleSimulationContextTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVehicleSimulationContextTypeEnum: " + value);
   }
}
