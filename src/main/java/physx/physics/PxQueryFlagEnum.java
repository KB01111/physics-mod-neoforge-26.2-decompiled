package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxQueryFlagEnum {
   eSTATIC(geteSTATIC()),
   eDYNAMIC(geteDYNAMIC()),
   ePREFILTER(getePREFILTER()),
   ePOSTFILTER(getePOSTFILTER()),
   eANY_HIT(geteANY_HIT()),
   eNO_BLOCK(geteNO_BLOCK());

   public final int value;

   private PxQueryFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteSTATIC();

   private static int geteSTATIC() {
      Loader.load();
      return _geteSTATIC();
   }

   private static native int _geteDYNAMIC();

   private static int geteDYNAMIC() {
      Loader.load();
      return _geteDYNAMIC();
   }

   private static native int _getePREFILTER();

   private static int getePREFILTER() {
      Loader.load();
      return _getePREFILTER();
   }

   private static native int _getePOSTFILTER();

   private static int getePOSTFILTER() {
      Loader.load();
      return _getePOSTFILTER();
   }

   private static native int _geteANY_HIT();

   private static int geteANY_HIT() {
      Loader.load();
      return _geteANY_HIT();
   }

   private static native int _geteNO_BLOCK();

   private static int geteNO_BLOCK() {
      Loader.load();
      return _geteNO_BLOCK();
   }

   public static PxQueryFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxQueryFlagEnum: " + value);
   }
}
