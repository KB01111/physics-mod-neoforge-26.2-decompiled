package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxSceneQueryUpdateModeEnum {
   eBUILD_ENABLED_COMMIT_ENABLED(geteBUILD_ENABLED_COMMIT_ENABLED()),
   eBUILD_ENABLED_COMMIT_DISABLED(geteBUILD_ENABLED_COMMIT_DISABLED()),
   eBUILD_DISABLED_COMMIT_DISABLED(geteBUILD_DISABLED_COMMIT_DISABLED());

   public final int value;

   private PxSceneQueryUpdateModeEnum(int value) {
      this.value = value;
   }

   private static native int _geteBUILD_ENABLED_COMMIT_ENABLED();

   private static int geteBUILD_ENABLED_COMMIT_ENABLED() {
      Loader.load();
      return _geteBUILD_ENABLED_COMMIT_ENABLED();
   }

   private static native int _geteBUILD_ENABLED_COMMIT_DISABLED();

   private static int geteBUILD_ENABLED_COMMIT_DISABLED() {
      Loader.load();
      return _geteBUILD_ENABLED_COMMIT_DISABLED();
   }

   private static native int _geteBUILD_DISABLED_COMMIT_DISABLED();

   private static int geteBUILD_DISABLED_COMMIT_DISABLED() {
      Loader.load();
      return _geteBUILD_DISABLED_COMMIT_DISABLED();
   }

   public static PxSceneQueryUpdateModeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxSceneQueryUpdateModeEnum: " + value);
   }
}
