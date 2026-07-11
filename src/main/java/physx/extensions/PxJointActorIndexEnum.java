package physx.extensions;

import de.fabmax.physxjni.Loader;

public enum PxJointActorIndexEnum {
   eACTOR0(geteACTOR0()),
   eACTOR1(geteACTOR1());

   public final int value;

   private PxJointActorIndexEnum(int value) {
      this.value = value;
   }

   private static native int _geteACTOR0();

   private static int geteACTOR0() {
      Loader.load();
      return _geteACTOR0();
   }

   private static native int _geteACTOR1();

   private static int geteACTOR1() {
      Loader.load();
      return _geteACTOR1();
   }

   public static PxJointActorIndexEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxJointActorIndexEnum: " + value);
   }
}
