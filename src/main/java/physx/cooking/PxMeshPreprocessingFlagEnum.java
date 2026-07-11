package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshPreprocessingFlagEnum {
   eWELD_VERTICES(geteWELD_VERTICES()),
   eDISABLE_CLEAN_MESH(geteDISABLE_CLEAN_MESH()),
   eDISABLE_ACTIVE_EDGES_PRECOMPUTE(geteDISABLE_ACTIVE_EDGES_PRECOMPUTE()),
   eFORCE_32BIT_INDICES(geteFORCE_32BIT_INDICES());

   public final int value;

   private PxMeshPreprocessingFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteWELD_VERTICES();

   private static int geteWELD_VERTICES() {
      Loader.load();
      return _geteWELD_VERTICES();
   }

   private static native int _geteDISABLE_CLEAN_MESH();

   private static int geteDISABLE_CLEAN_MESH() {
      Loader.load();
      return _geteDISABLE_CLEAN_MESH();
   }

   private static native int _geteDISABLE_ACTIVE_EDGES_PRECOMPUTE();

   private static int geteDISABLE_ACTIVE_EDGES_PRECOMPUTE() {
      Loader.load();
      return _geteDISABLE_ACTIVE_EDGES_PRECOMPUTE();
   }

   private static native int _geteFORCE_32BIT_INDICES();

   private static int geteFORCE_32BIT_INDICES() {
      Loader.load();
      return _geteFORCE_32BIT_INDICES();
   }

   public static PxMeshPreprocessingFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxMeshPreprocessingFlagEnum: " + value);
   }
}
