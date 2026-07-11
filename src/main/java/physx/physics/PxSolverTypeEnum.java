package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxSolverTypeEnum {
   ePGS(getePGS()),
   eTGS(geteTGS());

   public final int value;

   private PxSolverTypeEnum(int value) {
      this.value = value;
   }

   private static native int _getePGS();

   private static int getePGS() {
      Loader.load();
      return _getePGS();
   }

   private static native int _geteTGS();

   private static int geteTGS() {
      Loader.load();
      return _geteTGS();
   }

   public static PxSolverTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxSolverTypeEnum: " + value);
   }
}
