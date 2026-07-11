package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPairFilteringModeEnum {
   eKEEP(geteKEEP()),
   eSUPPRESS(geteSUPPRESS()),
   eKILL(geteKILL()),
   eDEFAULT(geteDEFAULT());

   public final int value;

   private PxPairFilteringModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteKEEP();

   private static int geteKEEP() {
      Loader.load();
      return _geteKEEP();
   }

   private static native int _geteSUPPRESS();

   private static int geteSUPPRESS() {
      Loader.load();
      return _geteSUPPRESS();
   }

   private static native int _geteKILL();

   private static int geteKILL() {
      Loader.load();
      return _geteKILL();
   }

   private static native int _geteDEFAULT();

   private static int geteDEFAULT() {
      Loader.load();
      return _geteDEFAULT();
   }

   public static PxPairFilteringModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxPairFilteringModeEnum: " + value);
   }
}
