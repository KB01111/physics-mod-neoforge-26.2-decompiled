package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFilterFlagEnum {
   eKILL(geteKILL()),
   eSUPPRESS(geteSUPPRESS()),
   eCALLBACK(geteCALLBACK()),
   eNOTIFY(geteNOTIFY()),
   eDEFAULT(geteDEFAULT());

   public final int value;

   private PxFilterFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteKILL();

   private static int geteKILL() {
      Loader.load();
      return _geteKILL();
   }

   private static native int _geteSUPPRESS();

   private static int geteSUPPRESS() {
      Loader.load();
      return _geteSUPPRESS();
   }

   private static native int _geteCALLBACK();

   private static int geteCALLBACK() {
      Loader.load();
      return _geteCALLBACK();
   }

   private static native int _geteNOTIFY();

   private static int geteNOTIFY() {
      Loader.load();
      return _geteNOTIFY();
   }

   private static native int _geteDEFAULT();

   private static int geteDEFAULT() {
      Loader.load();
      return _geteDEFAULT();
   }

   public static PxFilterFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxFilterFlagEnum: " + value);
   }
}
