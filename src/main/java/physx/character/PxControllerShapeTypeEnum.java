package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerShapeTypeEnum {
   eBOX(geteBOX()),
   eCAPSULE(geteCAPSULE());

   public final int value;

   private PxControllerShapeTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteBOX();

   private static int geteBOX() {
      Loader.load();
      return _geteBOX();
   }

   private static native int _geteCAPSULE();

   private static int geteCAPSULE() {
      Loader.load();
      return _geteCAPSULE();
   }

   public static PxControllerShapeTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxControllerShapeTypeEnum: " + value);
   }
}
