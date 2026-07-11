package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxForceModeEnum {
   eFORCE(geteFORCE()),
   eIMPULSE(geteIMPULSE()),
   eVELOCITY_CHANGE(geteVELOCITY_CHANGE()),
   eACCELERATION(geteACCELERATION());

   public final int value;

   private PxForceModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteFORCE();

   private static int geteFORCE() {
      Loader.load();
      return _geteFORCE();
   }

   private static native int _geteIMPULSE();

   private static int geteIMPULSE() {
      Loader.load();
      return _geteIMPULSE();
   }

   private static native int _geteVELOCITY_CHANGE();

   private static int geteVELOCITY_CHANGE() {
      Loader.load();
      return _geteVELOCITY_CHANGE();
   }

   private static native int _geteACCELERATION();

   private static int geteACCELERATION() {
      Loader.load();
      return _geteACCELERATION();
   }

   public static PxForceModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxForceModeEnum: " + value);
   }
}
