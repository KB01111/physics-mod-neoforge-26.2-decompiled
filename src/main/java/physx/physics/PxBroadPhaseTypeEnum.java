package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxBroadPhaseTypeEnum {
   eSAP(geteSAP()),
   eMBP(geteMBP()),
   eABP(geteABP()),
   ePABP(getePABP()),
   eGPU(geteGPU());

   public final int value;

   private PxBroadPhaseTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteSAP();

   private static int geteSAP() {
      Loader.load();
      return _geteSAP();
   }

   private static native int _geteMBP();

   private static int geteMBP() {
      Loader.load();
      return _geteMBP();
   }

   private static native int _geteABP();

   private static int geteABP() {
      Loader.load();
      return _geteABP();
   }

   private static native int _getePABP();

   private static int getePABP() {
      Loader.load();
      return _getePABP();
   }

   private static native int _geteGPU();

   private static int geteGPU() {
      Loader.load();
      return _geteGPU();
   }

   public static PxBroadPhaseTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxBroadPhaseTypeEnum: " + value);
   }
}
