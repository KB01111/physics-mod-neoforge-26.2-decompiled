package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxGeometryTypeEnum {
   eSPHERE(geteSPHERE()),
   ePLANE(getePLANE()),
   eCAPSULE(geteCAPSULE()),
   eBOX(geteBOX()),
   eCONVEXMESH(geteCONVEXMESH()),
   eTRIANGLEMESH(geteTRIANGLEMESH()),
   eHEIGHTFIELD(geteHEIGHTFIELD()),
   eCUSTOM(geteCUSTOM());

   public final int value;

   private PxGeometryTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteSPHERE();

   private static int geteSPHERE() {
      Loader.load();
      return _geteSPHERE();
   }

   private static native int _getePLANE();

   private static int getePLANE() {
      Loader.load();
      return _getePLANE();
   }

   private static native int _geteCAPSULE();

   private static int geteCAPSULE() {
      Loader.load();
      return _geteCAPSULE();
   }

   private static native int _geteBOX();

   private static int geteBOX() {
      Loader.load();
      return _geteBOX();
   }

   private static native int _geteCONVEXMESH();

   private static int geteCONVEXMESH() {
      Loader.load();
      return _geteCONVEXMESH();
   }

   private static native int _geteTRIANGLEMESH();

   private static int geteTRIANGLEMESH() {
      Loader.load();
      return _geteTRIANGLEMESH();
   }

   private static native int _geteHEIGHTFIELD();

   private static int geteHEIGHTFIELD() {
      Loader.load();
      return _geteHEIGHTFIELD();
   }

   private static native int _geteCUSTOM();

   private static int geteCUSTOM() {
      Loader.load();
      return _geteCUSTOM();
   }

   public static PxGeometryTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxGeometryTypeEnum: " + value);
   }
}
