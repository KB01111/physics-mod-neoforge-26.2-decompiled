package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTetrahedronMeshAnalysisResultEnum {
   eVALID(geteVALID()),
   eDEGENERATE_TETRAHEDRON(geteDEGENERATE_TETRAHEDRON()),
   eMESH_IS_PROBLEMATIC(geteMESH_IS_PROBLEMATIC()),
   eMESH_IS_INVALID(geteMESH_IS_INVALID());

   public final int value;

   private PxTetrahedronMeshAnalysisResultEnum(int value) {
      this.value = value;
   }

   private static native int _geteVALID();

   private static int geteVALID() {
      Loader.load();
      return _geteVALID();
   }

   private static native int _geteDEGENERATE_TETRAHEDRON();

   private static int geteDEGENERATE_TETRAHEDRON() {
      Loader.load();
      return _geteDEGENERATE_TETRAHEDRON();
   }

   private static native int _geteMESH_IS_PROBLEMATIC();

   private static int geteMESH_IS_PROBLEMATIC() {
      Loader.load();
      return _geteMESH_IS_PROBLEMATIC();
   }

   private static native int _geteMESH_IS_INVALID();

   private static int geteMESH_IS_INVALID() {
      Loader.load();
      return _geteMESH_IS_INVALID();
   }

   public static PxTetrahedronMeshAnalysisResultEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxTetrahedronMeshAnalysisResultEnum: " + value);
   }
}
