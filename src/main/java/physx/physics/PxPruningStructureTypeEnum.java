package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPruningStructureTypeEnum {
   eNONE(geteNONE()),
   eDYNAMIC_AABB_TREE(geteDYNAMIC_AABB_TREE()),
   eSTATIC_AABB_TREE(geteSTATIC_AABB_TREE());

   public final int value;

   private PxPruningStructureTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   private static native int _geteDYNAMIC_AABB_TREE();

   private static int geteDYNAMIC_AABB_TREE() {
      Loader.load();
      return _geteDYNAMIC_AABB_TREE();
   }

   private static native int _geteSTATIC_AABB_TREE();

   private static int geteSTATIC_AABB_TREE() {
      Loader.load();
      return _geteSTATIC_AABB_TREE();
   }

   public static PxPruningStructureTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxPruningStructureTypeEnum: " + value);
   }
}
