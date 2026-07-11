package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxD6JointDriveFlagEnum {
   eACCELERATION(geteACCELERATION());

   public final int value;

   private PxD6JointDriveFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteACCELERATION();

   private static int geteACCELERATION() {
      Loader.load();
      return _geteACCELERATION();
   }

   public static PxD6JointDriveFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxD6JointDriveFlagEnum: " + value);
   }
}
