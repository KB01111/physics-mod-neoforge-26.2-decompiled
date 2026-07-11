package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxFilterObjectFlagEnum {
   eKINEMATIC(geteKINEMATIC()),
   eTRIGGER(geteTRIGGER());

   public final int value;

   private PxFilterObjectFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteKINEMATIC();

   private static int geteKINEMATIC() {
      Loader.load();
      return _geteKINEMATIC();
   }

   private static native int _geteTRIGGER();

   private static int geteTRIGGER() {
      Loader.load();
      return _geteTRIGGER();
   }

   public static PxFilterObjectFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxFilterObjectFlagEnum: " + value);
   }
}
