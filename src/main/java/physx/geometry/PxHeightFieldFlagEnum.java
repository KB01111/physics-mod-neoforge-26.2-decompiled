package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxHeightFieldFlagEnum {
   eNO_BOUNDARY_EDGES(geteNO_BOUNDARY_EDGES());

   public final int value;

   private PxHeightFieldFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteNO_BOUNDARY_EDGES();

   private static int geteNO_BOUNDARY_EDGES() {
      Loader.load();
      return _geteNO_BOUNDARY_EDGES();
   }

   public static PxHeightFieldFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxHeightFieldFlagEnum: " + value);
   }
}
