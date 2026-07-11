package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxArticulationJointTypeEnum {
   eFIX(geteFIX()),
   ePRISMATIC(getePRISMATIC()),
   eREVOLUTE(geteREVOLUTE()),
   eSPHERICAL(geteSPHERICAL()),
   eUNDEFINED(geteUNDEFINED());

   public final int value;

   private PxArticulationJointTypeEnum(int value) {
      this.value = value;
   }

   private static native int _geteFIX();

   private static int geteFIX() {
      Loader.load();
      return _geteFIX();
   }

   private static native int _getePRISMATIC();

   private static int getePRISMATIC() {
      Loader.load();
      return _getePRISMATIC();
   }

   private static native int _geteREVOLUTE();

   private static int geteREVOLUTE() {
      Loader.load();
      return _geteREVOLUTE();
   }

   private static native int _geteSPHERICAL();

   private static int geteSPHERICAL() {
      Loader.load();
      return _geteSPHERICAL();
   }

   private static native int _geteUNDEFINED();

   private static int geteUNDEFINED() {
      Loader.load();
      return _geteUNDEFINED();
   }

   public static PxArticulationJointTypeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxArticulationJointTypeEnum: " + value);
   }
}
