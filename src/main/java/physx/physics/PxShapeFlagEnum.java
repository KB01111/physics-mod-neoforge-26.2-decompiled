package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxShapeFlagEnum {
   eSIMULATION_SHAPE(geteSIMULATION_SHAPE()),
   eSCENE_QUERY_SHAPE(geteSCENE_QUERY_SHAPE()),
   eTRIGGER_SHAPE(geteTRIGGER_SHAPE()),
   eVISUALIZATION(geteVISUALIZATION());

   public final int value;

   private PxShapeFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteSIMULATION_SHAPE();

   private static int geteSIMULATION_SHAPE() {
      Loader.load();
      return _geteSIMULATION_SHAPE();
   }

   private static native int _geteSCENE_QUERY_SHAPE();

   private static int geteSCENE_QUERY_SHAPE() {
      Loader.load();
      return _geteSCENE_QUERY_SHAPE();
   }

   private static native int _geteTRIGGER_SHAPE();

   private static int geteTRIGGER_SHAPE() {
      Loader.load();
      return _geteTRIGGER_SHAPE();
   }

   private static native int _geteVISUALIZATION();

   private static int geteVISUALIZATION() {
      Loader.load();
      return _geteVISUALIZATION();
   }

   public static PxShapeFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxShapeFlagEnum: " + value);
   }
}
