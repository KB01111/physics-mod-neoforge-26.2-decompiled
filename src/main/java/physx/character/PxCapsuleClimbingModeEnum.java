package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxCapsuleClimbingModeEnum {
   eEASY(geteEASY()),
   eCONSTRAINED(geteCONSTRAINED());

   public final int value;

   private PxCapsuleClimbingModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteEASY();

   private static int geteEASY() {
      Loader.load();
      return _geteEASY();
   }

   private static native int _geteCONSTRAINED();

   private static int geteCONSTRAINED() {
      Loader.load();
      return _geteCONSTRAINED();
   }

   public static PxCapsuleClimbingModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxCapsuleClimbingModeEnum: " + value);
   }
}
