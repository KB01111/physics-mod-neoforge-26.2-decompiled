package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxTriggerPairFlagEnum {
   eREMOVED_SHAPE_TRIGGER(geteREMOVED_SHAPE_TRIGGER()),
   eREMOVED_SHAPE_OTHER(geteREMOVED_SHAPE_OTHER()),
   eNEXT_FREE(geteNEXT_FREE());

   public final int value;

   private PxTriggerPairFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteREMOVED_SHAPE_TRIGGER();

   private static int geteREMOVED_SHAPE_TRIGGER() {
      Loader.load();
      return _geteREMOVED_SHAPE_TRIGGER();
   }

   private static native int _geteREMOVED_SHAPE_OTHER();

   private static int geteREMOVED_SHAPE_OTHER() {
      Loader.load();
      return _geteREMOVED_SHAPE_OTHER();
   }

   private static native int _geteNEXT_FREE();

   private static int geteNEXT_FREE() {
      Loader.load();
      return _geteNEXT_FREE();
   }

   public static PxTriggerPairFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxTriggerPairFlagEnum: " + value);
   }
}
