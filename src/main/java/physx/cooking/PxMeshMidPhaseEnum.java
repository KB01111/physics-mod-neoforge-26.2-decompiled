package physx.cooking;

import de.fabmax.physxjni.Loader;

public enum PxMeshMidPhaseEnum {
   @Deprecated
   eBVH33(geteBVH33()),
   eBVH34(geteBVH34());

   public final int value;

   private PxMeshMidPhaseEnum(int value) {
      this.value = value;
   }

   private static native int _geteBVH33();

   private static int geteBVH33() {
      Loader.load();
      return _geteBVH33();
   }

   private static native int _geteBVH34();

   private static int geteBVH34() {
      Loader.load();
      return _geteBVH34();
   }

   public static PxMeshMidPhaseEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxMeshMidPhaseEnum: " + value);
   }
}
