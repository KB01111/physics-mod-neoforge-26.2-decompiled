package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTetrahedronMeshFormatEnum {
   eTET_MESH(geteTET_MESH()),
   eHEX_MESH(geteHEX_MESH());

   public final int value;

   private PxTetrahedronMeshFormatEnum(int value) {
      this.value = value;
   }

   private static native int _geteTET_MESH();

   private static int geteTET_MESH() {
      Loader.load();
      return _geteTET_MESH();
   }

   private static native int _geteHEX_MESH();

   private static int geteHEX_MESH() {
      Loader.load();
      return _geteHEX_MESH();
   }

   public static PxTetrahedronMeshFormatEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxTetrahedronMeshFormatEnum: " + value);
   }
}
