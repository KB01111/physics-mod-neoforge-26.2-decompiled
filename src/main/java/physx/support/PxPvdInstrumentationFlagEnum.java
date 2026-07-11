package physx.support;

import de.fabmax.physxjni.Loader;

public enum PxPvdInstrumentationFlagEnum {
   eDEBUG(geteDEBUG()),
   ePROFILE(getePROFILE()),
   eMEMORY(geteMEMORY()),
   eALL(geteALL());

   public final int value;

   private PxPvdInstrumentationFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteDEBUG();

   private static int geteDEBUG() {
      Loader.load();
      return _geteDEBUG();
   }

   private static native int _getePROFILE();

   private static int getePROFILE() {
      Loader.load();
      return _getePROFILE();
   }

   private static native int _geteMEMORY();

   private static int geteMEMORY() {
      Loader.load();
      return _geteMEMORY();
   }

   private static native int _geteALL();

   private static int geteALL() {
      Loader.load();
      return _geteALL();
   }

   public static PxPvdInstrumentationFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxPvdInstrumentationFlagEnum: " + value);
   }
}
