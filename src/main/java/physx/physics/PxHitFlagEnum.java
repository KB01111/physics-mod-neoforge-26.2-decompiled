package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxHitFlagEnum {
   ePOSITION(getePOSITION()),
   eNORMAL(geteNORMAL()),
   eUV(geteUV()),
   eASSUME_NO_INITIAL_OVERLAP(geteASSUME_NO_INITIAL_OVERLAP()),
   eANY_HIT(geteANY_HIT()),
   eMESH_MULTIPLE(geteMESH_MULTIPLE()),
   @Deprecated
   eMESH_ANY(geteMESH_ANY()),
   eMESH_BOTH_SIDES(geteMESH_BOTH_SIDES()),
   ePRECISE_SWEEP(getePRECISE_SWEEP()),
   eMTD(geteMTD()),
   eFACE_INDEX(geteFACE_INDEX()),
   eDEFAULT(geteDEFAULT()),
   eMODIFIABLE_FLAGS(geteMODIFIABLE_FLAGS());

   public final int value;

   private PxHitFlagEnum(int value) {
      this.value = value;
   }

   private static native int _getePOSITION();

   private static int getePOSITION() {
      Loader.load();
      return _getePOSITION();
   }

   private static native int _geteNORMAL();

   private static int geteNORMAL() {
      Loader.load();
      return _geteNORMAL();
   }

   private static native int _geteUV();

   private static int geteUV() {
      Loader.load();
      return _geteUV();
   }

   private static native int _geteASSUME_NO_INITIAL_OVERLAP();

   private static int geteASSUME_NO_INITIAL_OVERLAP() {
      Loader.load();
      return _geteASSUME_NO_INITIAL_OVERLAP();
   }

   private static native int _geteANY_HIT();

   private static int geteANY_HIT() {
      Loader.load();
      return _geteANY_HIT();
   }

   private static native int _geteMESH_MULTIPLE();

   private static int geteMESH_MULTIPLE() {
      Loader.load();
      return _geteMESH_MULTIPLE();
   }

   private static native int _geteMESH_ANY();

   private static int geteMESH_ANY() {
      Loader.load();
      return _geteMESH_ANY();
   }

   private static native int _geteMESH_BOTH_SIDES();

   private static int geteMESH_BOTH_SIDES() {
      Loader.load();
      return _geteMESH_BOTH_SIDES();
   }

   private static native int _getePRECISE_SWEEP();

   private static int getePRECISE_SWEEP() {
      Loader.load();
      return _getePRECISE_SWEEP();
   }

   private static native int _geteMTD();

   private static int geteMTD() {
      Loader.load();
      return _geteMTD();
   }

   private static native int _geteFACE_INDEX();

   private static int geteFACE_INDEX() {
      Loader.load();
      return _geteFACE_INDEX();
   }

   private static native int _geteDEFAULT();

   private static int geteDEFAULT() {
      Loader.load();
      return _geteDEFAULT();
   }

   private static native int _geteMODIFIABLE_FLAGS();

   private static int geteMODIFIABLE_FLAGS() {
      Loader.load();
      return _geteMODIFIABLE_FLAGS();
   }

   public static PxHitFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxHitFlagEnum: " + value);
   }
}
