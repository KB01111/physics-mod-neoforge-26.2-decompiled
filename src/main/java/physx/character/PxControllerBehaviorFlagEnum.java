package physx.character;

import de.fabmax.physxjni.Loader;

public enum PxControllerBehaviorFlagEnum {
   eCCT_CAN_RIDE_ON_OBJECT(geteCCT_CAN_RIDE_ON_OBJECT()),
   eCCT_SLIDE(geteCCT_SLIDE()),
   eCCT_USER_DEFINED_RIDE(geteCCT_USER_DEFINED_RIDE());

   public final int value;

   private PxControllerBehaviorFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteCCT_CAN_RIDE_ON_OBJECT();

   private static int geteCCT_CAN_RIDE_ON_OBJECT() {
      Loader.load();
      return _geteCCT_CAN_RIDE_ON_OBJECT();
   }

   private static native int _geteCCT_SLIDE();

   private static int geteCCT_SLIDE() {
      Loader.load();
      return _geteCCT_SLIDE();
   }

   private static native int _geteCCT_USER_DEFINED_RIDE();

   private static int geteCCT_USER_DEFINED_RIDE() {
      Loader.load();
      return _geteCCT_USER_DEFINED_RIDE();
   }

   public static PxControllerBehaviorFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxControllerBehaviorFlagEnum: " + value);
   }
}
