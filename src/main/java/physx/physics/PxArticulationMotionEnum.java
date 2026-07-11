package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationMotionEnum {
   eLOCKED(geteLOCKED()),
   eLIMITED(geteLIMITED()),
   eFREE(geteFREE());

   public final int value;

   private PxArticulationMotionEnum(int value) {
      this.value = value;
   }

   private static native int _geteLOCKED();

   private static int geteLOCKED() {
      Loader.load();
      return _geteLOCKED();
   }

   private static native int _geteLIMITED();

   private static int geteLIMITED() {
      Loader.load();
      return _geteLIMITED();
   }

   private static native int _geteFREE();

   private static int geteFREE() {
      Loader.load();
      return _geteFREE();
   }

   public static PxArticulationMotionEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationMotionEnum: " + value);
   }
}
