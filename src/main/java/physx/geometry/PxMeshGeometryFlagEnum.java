package physx.geometry;

import de.fabmax.physxjni.Loader;

public enum PxMeshGeometryFlagEnum {
   eDOUBLE_SIDED(geteDOUBLE_SIDED());

   public final int value;

   private PxMeshGeometryFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteDOUBLE_SIDED();

   private static int geteDOUBLE_SIDED() {
      Loader.load();
      return _geteDOUBLE_SIDED();
   }

   public static PxMeshGeometryFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxMeshGeometryFlagEnum: " + value);
   }
}
