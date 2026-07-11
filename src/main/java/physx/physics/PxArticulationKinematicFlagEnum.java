package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationKinematicFlagEnum {
   ePOSITION(getePOSITION()),
   eVELOCITY(geteVELOCITY());

   public final int value;

   private PxArticulationKinematicFlagEnum(int value) {
      this.value = value;
   }

   private static native int _getePOSITION();

   private static int getePOSITION() {
      Loader.load();
      return _getePOSITION();
   }

   private static native int _geteVELOCITY();

   private static int geteVELOCITY() {
      Loader.load();
      return _geteVELOCITY();
   }

   public static PxArticulationKinematicFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationKinematicFlagEnum: " + value);
   }
}
