package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxBaseFlagEnum {
   eOWNS_MEMORY(geteOWNS_MEMORY()),
   eIS_RELEASABLE(geteIS_RELEASABLE());

   public final int value;

   private PxBaseFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteOWNS_MEMORY();

   private static int geteOWNS_MEMORY() {
      Loader.load();
      return _geteOWNS_MEMORY();
   }

   private static native int _geteIS_RELEASABLE();

   private static int geteIS_RELEASABLE() {
      Loader.load();
      return _geteIS_RELEASABLE();
   }

   public static PxBaseFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxBaseFlagEnum: " + value);
   }
}
