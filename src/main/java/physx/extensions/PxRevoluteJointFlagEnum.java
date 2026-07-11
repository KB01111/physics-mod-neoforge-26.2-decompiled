package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxRevoluteJointFlagEnum {
   eLIMIT_ENABLED(geteLIMIT_ENABLED()),
   eDRIVE_ENABLED(geteDRIVE_ENABLED()),
   eDRIVE_FREESPIN(geteDRIVE_FREESPIN());

   public final int value;

   private PxRevoluteJointFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteLIMIT_ENABLED();

   private static int geteLIMIT_ENABLED() {
      Loader.load();
      return _geteLIMIT_ENABLED();
   }

   private static native int _geteDRIVE_ENABLED();

   private static int geteDRIVE_ENABLED() {
      Loader.load();
      return _geteDRIVE_ENABLED();
   }

   private static native int _geteDRIVE_FREESPIN();

   private static int geteDRIVE_FREESPIN() {
      Loader.load();
      return _geteDRIVE_FREESPIN();
   }

   public static PxRevoluteJointFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxRevoluteJointFlagEnum: " + value);
   }
}
