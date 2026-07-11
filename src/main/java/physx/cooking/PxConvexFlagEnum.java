package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxConvexFlagEnum {
   e16_BIT_INDICES(gete16_BIT_INDICES()),
   eCOMPUTE_CONVEX(geteCOMPUTE_CONVEX()),
   eCHECK_ZERO_AREA_TRIANGLES(geteCHECK_ZERO_AREA_TRIANGLES()),
   eQUANTIZE_INPUT(geteQUANTIZE_INPUT()),
   eDISABLE_MESH_VALIDATION(geteDISABLE_MESH_VALIDATION()),
   ePLANE_SHIFTING(getePLANE_SHIFTING()),
   eFAST_INERTIA_COMPUTATION(geteFAST_INERTIA_COMPUTATION()),
   eGPU_COMPATIBLE(geteGPU_COMPATIBLE()),
   eSHIFT_VERTICES(geteSHIFT_VERTICES());

   public final int value;

   private PxConvexFlagEnum(int value) {
      this.value = value;
   }

   private static native int _gete16_BIT_INDICES();

   private static int gete16_BIT_INDICES() {
      Loader.load();
      return _gete16_BIT_INDICES();
   }

   private static native int _geteCOMPUTE_CONVEX();

   private static int geteCOMPUTE_CONVEX() {
      Loader.load();
      return _geteCOMPUTE_CONVEX();
   }

   private static native int _geteCHECK_ZERO_AREA_TRIANGLES();

   private static int geteCHECK_ZERO_AREA_TRIANGLES() {
      Loader.load();
      return _geteCHECK_ZERO_AREA_TRIANGLES();
   }

   private static native int _geteQUANTIZE_INPUT();

   private static int geteQUANTIZE_INPUT() {
      Loader.load();
      return _geteQUANTIZE_INPUT();
   }

   private static native int _geteDISABLE_MESH_VALIDATION();

   private static int geteDISABLE_MESH_VALIDATION() {
      Loader.load();
      return _geteDISABLE_MESH_VALIDATION();
   }

   private static native int _getePLANE_SHIFTING();

   private static int getePLANE_SHIFTING() {
      Loader.load();
      return _getePLANE_SHIFTING();
   }

   private static native int _geteFAST_INERTIA_COMPUTATION();

   private static int geteFAST_INERTIA_COMPUTATION() {
      Loader.load();
      return _geteFAST_INERTIA_COMPUTATION();
   }

   private static native int _geteGPU_COMPATIBLE();

   private static int geteGPU_COMPATIBLE() {
      Loader.load();
      return _geteGPU_COMPATIBLE();
   }

   private static native int _geteSHIFT_VERTICES();

   private static int geteSHIFT_VERTICES() {
      Loader.load();
      return _geteSHIFT_VERTICES();
   }

   public static PxConvexFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxConvexFlagEnum: " + value);
   }
}
