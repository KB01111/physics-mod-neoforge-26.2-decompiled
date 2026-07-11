package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTetrahedronMeshFlagEnum {
   e16_BIT_INDICES(gete16_BIT_INDICES());

   public final int value;

   private PxTetrahedronMeshFlagEnum(int value) {
      this.value = value;
   }

   private static native int _gete16_BIT_INDICES();

   private static int gete16_BIT_INDICES() {
      Loader.load();
      return _gete16_BIT_INDICES();
   }

   public static PxTetrahedronMeshFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxTetrahedronMeshFlagEnum: " + value);
   }
}
