package physx.support;

import de.fabmax.physxjni.Loader;

public enum PxVisualizationParameterEnum {
   eSCALE(geteSCALE()),
   eWORLD_AXES(geteWORLD_AXES()),
   eBODY_AXES(geteBODY_AXES()),
   eBODY_MASS_AXES(geteBODY_MASS_AXES()),
   eBODY_LIN_VELOCITY(geteBODY_LIN_VELOCITY()),
   eBODY_ANG_VELOCITY(geteBODY_ANG_VELOCITY()),
   eCONTACT_POINT(geteCONTACT_POINT()),
   eCONTACT_NORMAL(geteCONTACT_NORMAL()),
   eCONTACT_ERROR(geteCONTACT_ERROR()),
   eCONTACT_FORCE(geteCONTACT_FORCE()),
   eACTOR_AXES(geteACTOR_AXES()),
   eCOLLISION_AABBS(geteCOLLISION_AABBS()),
   eCOLLISION_SHAPES(geteCOLLISION_SHAPES()),
   eCOLLISION_AXES(geteCOLLISION_AXES()),
   eCOLLISION_COMPOUNDS(geteCOLLISION_COMPOUNDS()),
   eCOLLISION_FNORMALS(geteCOLLISION_FNORMALS()),
   eCOLLISION_EDGES(geteCOLLISION_EDGES()),
   eCOLLISION_STATIC(geteCOLLISION_STATIC()),
   eCOLLISION_DYNAMIC(geteCOLLISION_DYNAMIC()),
   eJOINT_LOCAL_FRAMES(geteJOINT_LOCAL_FRAMES()),
   eJOINT_LIMITS(geteJOINT_LIMITS()),
   eCULL_BOX(geteCULL_BOX()),
   eMBP_REGIONS(geteMBP_REGIONS()),
   eSIMULATION_MESH(geteSIMULATION_MESH()),
   eSDF(geteSDF()),
   eNUM_VALUES(geteNUM_VALUES()),
   eFORCE_DWORD(geteFORCE_DWORD());

   public final int value;

   private PxVisualizationParameterEnum(int value) {
      this.value = value;
   }

   private static native int _geteSCALE();

   private static int geteSCALE() {
      Loader.load();
      return _geteSCALE();
   }

   private static native int _geteWORLD_AXES();

   private static int geteWORLD_AXES() {
      Loader.load();
      return _geteWORLD_AXES();
   }

   private static native int _geteBODY_AXES();

   private static int geteBODY_AXES() {
      Loader.load();
      return _geteBODY_AXES();
   }

   private static native int _geteBODY_MASS_AXES();

   private static int geteBODY_MASS_AXES() {
      Loader.load();
      return _geteBODY_MASS_AXES();
   }

   private static native int _geteBODY_LIN_VELOCITY();

   private static int geteBODY_LIN_VELOCITY() {
      Loader.load();
      return _geteBODY_LIN_VELOCITY();
   }

   private static native int _geteBODY_ANG_VELOCITY();

   private static int geteBODY_ANG_VELOCITY() {
      Loader.load();
      return _geteBODY_ANG_VELOCITY();
   }

   private static native int _geteCONTACT_POINT();

   private static int geteCONTACT_POINT() {
      Loader.load();
      return _geteCONTACT_POINT();
   }

   private static native int _geteCONTACT_NORMAL();

   private static int geteCONTACT_NORMAL() {
      Loader.load();
      return _geteCONTACT_NORMAL();
   }

   private static native int _geteCONTACT_ERROR();

   private static int geteCONTACT_ERROR() {
      Loader.load();
      return _geteCONTACT_ERROR();
   }

   private static native int _geteCONTACT_FORCE();

   private static int geteCONTACT_FORCE() {
      Loader.load();
      return _geteCONTACT_FORCE();
   }

   private static native int _geteACTOR_AXES();

   private static int geteACTOR_AXES() {
      Loader.load();
      return _geteACTOR_AXES();
   }

   private static native int _geteCOLLISION_AABBS();

   private static int geteCOLLISION_AABBS() {
      Loader.load();
      return _geteCOLLISION_AABBS();
   }

   private static native int _geteCOLLISION_SHAPES();

   private static int geteCOLLISION_SHAPES() {
      Loader.load();
      return _geteCOLLISION_SHAPES();
   }

   private static native int _geteCOLLISION_AXES();

   private static int geteCOLLISION_AXES() {
      Loader.load();
      return _geteCOLLISION_AXES();
   }

   private static native int _geteCOLLISION_COMPOUNDS();

   private static int geteCOLLISION_COMPOUNDS() {
      Loader.load();
      return _geteCOLLISION_COMPOUNDS();
   }

   private static native int _geteCOLLISION_FNORMALS();

   private static int geteCOLLISION_FNORMALS() {
      Loader.load();
      return _geteCOLLISION_FNORMALS();
   }

   private static native int _geteCOLLISION_EDGES();

   private static int geteCOLLISION_EDGES() {
      Loader.load();
      return _geteCOLLISION_EDGES();
   }

   private static native int _geteCOLLISION_STATIC();

   private static int geteCOLLISION_STATIC() {
      Loader.load();
      return _geteCOLLISION_STATIC();
   }

   private static native int _geteCOLLISION_DYNAMIC();

   private static int geteCOLLISION_DYNAMIC() {
      Loader.load();
      return _geteCOLLISION_DYNAMIC();
   }

   private static native int _geteJOINT_LOCAL_FRAMES();

   private static int geteJOINT_LOCAL_FRAMES() {
      Loader.load();
      return _geteJOINT_LOCAL_FRAMES();
   }

   private static native int _geteJOINT_LIMITS();

   private static int geteJOINT_LIMITS() {
      Loader.load();
      return _geteJOINT_LIMITS();
   }

   private static native int _geteCULL_BOX();

   private static int geteCULL_BOX() {
      Loader.load();
      return _geteCULL_BOX();
   }

   private static native int _geteMBP_REGIONS();

   private static int geteMBP_REGIONS() {
      Loader.load();
      return _geteMBP_REGIONS();
   }

   private static native int _geteSIMULATION_MESH();

   private static int geteSIMULATION_MESH() {
      Loader.load();
      return _geteSIMULATION_MESH();
   }

   private static native int _geteSDF();

   private static int geteSDF() {
      Loader.load();
      return _geteSDF();
   }

   private static native int _geteNUM_VALUES();

   private static int geteNUM_VALUES() {
      Loader.load();
      return _geteNUM_VALUES();
   }

   private static native int _geteFORCE_DWORD();

   private static int geteFORCE_DWORD() {
      Loader.load();
      return _geteFORCE_DWORD();
   }

   public static PxVisualizationParameterEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxVisualizationParameterEnum: " + value);
   }
}
