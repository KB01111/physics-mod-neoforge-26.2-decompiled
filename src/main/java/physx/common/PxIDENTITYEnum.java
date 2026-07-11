package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxIDENTITYEnum {
   PxIdentity(getPxIdentity());

   public final int value;

   private PxIDENTITYEnum(int value) {
      this.value = value;
   }

   private static native int _getPxIdentity();

   private static int getPxIdentity() {
      Loader.load();
      return _getPxIdentity();
   }

   public static PxIDENTITYEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxIDENTITYEnum: " + value);
   }
}
