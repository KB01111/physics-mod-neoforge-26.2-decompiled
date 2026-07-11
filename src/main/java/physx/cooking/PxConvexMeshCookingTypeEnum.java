package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxConvexMeshCookingTypeEnum {
   eQUICKHULL(geteQUICKHULL());

   public final int value;

   private PxConvexMeshCookingTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteQUICKHULL();

   private static int geteQUICKHULL() {
      Loader.load();
      return _geteQUICKHULL();
   }

   public static PxConvexMeshCookingTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxConvexMeshCookingTypeEnum: " + value);
   }
}
