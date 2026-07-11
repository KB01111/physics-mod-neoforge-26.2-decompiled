package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxTriangleMeshFlagEnum {
   e16_BIT_INDICES(gete16_BIT_INDICES()),
   eADJACENCY_INFO(geteADJACENCY_INFO());

   public final int value;

   private PxTriangleMeshFlagEnum(int value) {
      this.value = value;
   }

   private static native int _gete16_BIT_INDICES();

   private static int gete16_BIT_INDICES() {
      Loader.load();
      return _gete16_BIT_INDICES();
   }

   private static native int _geteADJACENCY_INFO();

   private static int geteADJACENCY_INFO() {
      Loader.load();
      return _geteADJACENCY_INFO();
   }

   public static PxTriangleMeshFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxTriangleMeshFlagEnum: " + value);
   }
}
