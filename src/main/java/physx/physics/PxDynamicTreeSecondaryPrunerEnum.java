package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxDynamicTreeSecondaryPrunerEnum {
   eNONE(geteNONE()),
   eBUCKET(geteBUCKET()),
   eINCREMENTAL(geteINCREMENTAL()),
   eBVH(geteBVH());

   public final int value;

   private PxDynamicTreeSecondaryPrunerEnum(int value) {
      this.value = value;
   }

   private static native int _geteNONE();

   private static int geteNONE() {
      Loader.load();
      return _geteNONE();
   }

   private static native int _geteBUCKET();

   private static int geteBUCKET() {
      Loader.load();
      return _geteBUCKET();
   }

   private static native int _geteINCREMENTAL();

   private static int geteINCREMENTAL() {
      Loader.load();
      return _geteINCREMENTAL();
   }

   private static native int _geteBVH();

   private static int geteBVH() {
      Loader.load();
      return _geteBVH();
   }

   public static PxDynamicTreeSecondaryPrunerEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxDynamicTreeSecondaryPrunerEnum: " + value);
   }
}
