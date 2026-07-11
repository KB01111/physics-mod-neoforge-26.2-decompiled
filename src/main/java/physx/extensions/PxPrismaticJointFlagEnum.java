package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxPrismaticJointFlagEnum {
   eLIMIT_ENABLED(geteLIMIT_ENABLED());

   public final int value;

   private PxPrismaticJointFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteLIMIT_ENABLED();

   private static int geteLIMIT_ENABLED() {
      Loader.load();
      return _geteLIMIT_ENABLED();
   }

   public static PxPrismaticJointFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxPrismaticJointFlagEnum: " + value);
   }
}
