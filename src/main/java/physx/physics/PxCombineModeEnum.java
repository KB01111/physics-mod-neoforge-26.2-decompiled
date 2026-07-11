package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxCombineModeEnum {
   eAVERAGE(geteAVERAGE()),
   eMIN(geteMIN()),
   eMULTIPLY(geteMULTIPLY()),
   eMAX(geteMAX());

   public final int value;

   private PxCombineModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteAVERAGE();

   private static int geteAVERAGE() {
      Loader.load();
      return _geteAVERAGE();
   }

   private static native int _geteMIN();

   private static int geteMIN() {
      Loader.load();
      return _geteMIN();
   }

   private static native int _geteMULTIPLY();

   private static int geteMULTIPLY() {
      Loader.load();
      return _geteMULTIPLY();
   }

   private static native int _geteMAX();

   private static int geteMAX() {
      Loader.load();
      return _geteMAX();
   }

   public static PxCombineModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxCombineModeEnum: " + value);
   }
}
