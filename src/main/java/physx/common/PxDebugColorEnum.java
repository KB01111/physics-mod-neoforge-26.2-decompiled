package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxDebugColorEnum {
   eARGB_BLACK(geteARGB_BLACK()),
   eARGB_RED(geteARGB_RED()),
   eARGB_GREEN(geteARGB_GREEN()),
   eARGB_BLUE(geteARGB_BLUE()),
   eARGB_YELLOW(geteARGB_YELLOW()),
   eARGB_MAGENTA(geteARGB_MAGENTA()),
   eARGB_CYAN(geteARGB_CYAN()),
   eARGB_WHITE(geteARGB_WHITE()),
   eARGB_GREY(geteARGB_GREY()),
   eARGB_DARKRED(geteARGB_DARKRED()),
   eARGB_DARKGREEN(geteARGB_DARKGREEN()),
   eARGB_DARKBLUE(geteARGB_DARKBLUE());

   public final int value;

   private PxDebugColorEnum(int value) {
      this.value = value;
   }

   private static native int _geteARGB_BLACK();

   private static int geteARGB_BLACK() {
      Loader.load();
      return _geteARGB_BLACK();
   }

   private static native int _geteARGB_RED();

   private static int geteARGB_RED() {
      Loader.load();
      return _geteARGB_RED();
   }

   private static native int _geteARGB_GREEN();

   private static int geteARGB_GREEN() {
      Loader.load();
      return _geteARGB_GREEN();
   }

   private static native int _geteARGB_BLUE();

   private static int geteARGB_BLUE() {
      Loader.load();
      return _geteARGB_BLUE();
   }

   private static native int _geteARGB_YELLOW();

   private static int geteARGB_YELLOW() {
      Loader.load();
      return _geteARGB_YELLOW();
   }

   private static native int _geteARGB_MAGENTA();

   private static int geteARGB_MAGENTA() {
      Loader.load();
      return _geteARGB_MAGENTA();
   }

   private static native int _geteARGB_CYAN();

   private static int geteARGB_CYAN() {
      Loader.load();
      return _geteARGB_CYAN();
   }

   private static native int _geteARGB_WHITE();

   private static int geteARGB_WHITE() {
      Loader.load();
      return _geteARGB_WHITE();
   }

   private static native int _geteARGB_GREY();

   private static int geteARGB_GREY() {
      Loader.load();
      return _geteARGB_GREY();
   }

   private static native int _geteARGB_DARKRED();

   private static int geteARGB_DARKRED() {
      Loader.load();
      return _geteARGB_DARKRED();
   }

   private static native int _geteARGB_DARKGREEN();

   private static int geteARGB_DARKGREEN() {
      Loader.load();
      return _geteARGB_DARKGREEN();
   }

   private static native int _geteARGB_DARKBLUE();

   private static int geteARGB_DARKBLUE() {
      Loader.load();
      return _geteARGB_DARKBLUE();
   }

   public static PxDebugColorEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxDebugColorEnum: " + value);
   }
}
