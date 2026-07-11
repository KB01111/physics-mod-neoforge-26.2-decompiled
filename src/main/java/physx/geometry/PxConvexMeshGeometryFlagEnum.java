package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxConvexMeshGeometryFlagEnum {
   eTIGHT_BOUNDS(geteTIGHT_BOUNDS());

   public final int value;

   private PxConvexMeshGeometryFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteTIGHT_BOUNDS();

   private static int geteTIGHT_BOUNDS() {
      Loader.load();
      return _geteTIGHT_BOUNDS();
   }

   public static PxConvexMeshGeometryFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxConvexMeshGeometryFlagEnum: " + value);
   }
}
