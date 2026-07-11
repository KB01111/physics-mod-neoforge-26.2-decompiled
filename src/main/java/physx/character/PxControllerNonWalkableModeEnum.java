package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerNonWalkableModeEnum {
   ePREVENT_CLIMBING(getePREVENT_CLIMBING()),
   ePREVENT_CLIMBING_AND_FORCE_SLIDING(getePREVENT_CLIMBING_AND_FORCE_SLIDING());

   public final int value;

   private PxControllerNonWalkableModeEnum(int value) {
      this.value = value;
   }

   private static native int _getePREVENT_CLIMBING();

   private static int getePREVENT_CLIMBING() {
      Loader.load();
      return _getePREVENT_CLIMBING();
   }

   private static native int _getePREVENT_CLIMBING_AND_FORCE_SLIDING();

   private static int getePREVENT_CLIMBING_AND_FORCE_SLIDING() {
      Loader.load();
      return _getePREVENT_CLIMBING_AND_FORCE_SLIDING();
   }

   public static PxControllerNonWalkableModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxControllerNonWalkableModeEnum: " + value);
   }
}
