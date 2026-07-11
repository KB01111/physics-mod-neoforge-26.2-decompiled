package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxQueryHitType {
   eNONE(geteNONE()),
   eBLOCK(geteBLOCK()),
   eTOUCH(geteTOUCH());

   public final int value;

   private PxQueryHitType(int value) {
      this.value = value;
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   private static native int _geteBLOCK();

   private static int geteBLOCK() {
      Loader.load();
      return _geteBLOCK();
   }

   private static native int _geteTOUCH();

   private static int geteTOUCH() {
      Loader.load();
      return _geteTOUCH();
   }

   public static PxQueryHitType forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxQueryHitType: " + value);
   }
}
