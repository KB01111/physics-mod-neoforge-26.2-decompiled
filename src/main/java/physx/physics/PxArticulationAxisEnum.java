package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationAxisEnum {
   eTWIST(geteTWIST()),
   eSWING1(geteSWING1()),
   eSWING2(geteSWING2()),
   eX(geteX()),
   eY(geteY()),
   eZ(geteZ());

   public final int value;

   private PxArticulationAxisEnum(int value) {
      this.value = value;
   }

   private static native int _geteTWIST();

   private static int geteTWIST() {
      Loader.load();
      return _geteTWIST();
   }

   private static native int _geteSWING1();

   private static int geteSWING1() {
      Loader.load();
      return _geteSWING1();
   }

   private static native int _geteSWING2();

   private static int geteSWING2() {
      Loader.load();
      return _geteSWING2();
   }

   private static native int _geteX();

   private static int geteX() {
      Loader.load();
      return _geteX();
   }

   private static native int _geteY();

   private static int geteY() {
      Loader.load();
      return _geteY();
   }

   private static native int _geteZ();

   private static int geteZ() {
      Loader.load();
      return _geteZ();
   }

   public static PxArticulationAxisEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationAxisEnum: " + value);
   }
}
