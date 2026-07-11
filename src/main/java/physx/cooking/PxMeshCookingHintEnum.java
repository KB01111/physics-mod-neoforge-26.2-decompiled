package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshCookingHintEnum {
   eSIM_PERFORMANCE(geteSIM_PERFORMANCE()),
   eCOOKING_PERFORMANCE(geteCOOKING_PERFORMANCE());

   public final int value;

   private PxMeshCookingHintEnum(int value) {
      this.value = value;
   }

   private static native int _geteSIM_PERFORMANCE();

   private static int geteSIM_PERFORMANCE() {
      Loader.load();
      return _geteSIM_PERFORMANCE();
   }

   private static native int _geteCOOKING_PERFORMANCE();

   private static int geteCOOKING_PERFORMANCE() {
      Loader.load();
      return _geteCOOKING_PERFORMANCE();
   }

   public static PxMeshCookingHintEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxMeshCookingHintEnum: " + value);
   }
}
