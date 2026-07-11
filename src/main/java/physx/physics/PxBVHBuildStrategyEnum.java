package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxBVHBuildStrategyEnum {
   eFAST(geteFAST()),
   eDEFAULT(geteDEFAULT()),
   eSAH(geteSAH());

   public final int value;

   private PxBVHBuildStrategyEnum(int value) {
      this.value = value;
   }

   private static native int _geteFAST();

   private static int geteFAST() {
      Loader.load();
      return _geteFAST();
   }

   private static native int _geteDEFAULT();

   private static int geteDEFAULT() {
      Loader.load();
      return _geteDEFAULT();
   }

   private static native int _geteSAH();

   private static int geteSAH() {
      Loader.load();
      return _geteSAH();
   }

   public static PxBVHBuildStrategyEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxBVHBuildStrategyEnum: " + value);
   }
}
