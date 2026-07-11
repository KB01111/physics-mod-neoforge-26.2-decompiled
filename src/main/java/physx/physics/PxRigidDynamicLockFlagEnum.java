package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxRigidDynamicLockFlagEnum {
   eLOCK_LINEAR_X(geteLOCK_LINEAR_X()),
   eLOCK_LINEAR_Y(geteLOCK_LINEAR_Y()),
   eLOCK_LINEAR_Z(geteLOCK_LINEAR_Z()),
   eLOCK_ANGULAR_X(geteLOCK_ANGULAR_X()),
   eLOCK_ANGULAR_Y(geteLOCK_ANGULAR_Y()),
   eLOCK_ANGULAR_Z(geteLOCK_ANGULAR_Z());

   public final int value;

   private PxRigidDynamicLockFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteLOCK_LINEAR_X();

   private static int geteLOCK_LINEAR_X() {
      Loader.load();
      return _geteLOCK_LINEAR_X();
   }

   private static native int _geteLOCK_LINEAR_Y();

   private static int geteLOCK_LINEAR_Y() {
      Loader.load();
      return _geteLOCK_LINEAR_Y();
   }

   private static native int _geteLOCK_LINEAR_Z();

   private static int geteLOCK_LINEAR_Z() {
      Loader.load();
      return _geteLOCK_LINEAR_Z();
   }

   private static native int _geteLOCK_ANGULAR_X();

   private static int geteLOCK_ANGULAR_X() {
      Loader.load();
      return _geteLOCK_ANGULAR_X();
   }

   private static native int _geteLOCK_ANGULAR_Y();

   private static int geteLOCK_ANGULAR_Y() {
      Loader.load();
      return _geteLOCK_ANGULAR_Y();
   }

   private static native int _geteLOCK_ANGULAR_Z();

   private static int geteLOCK_ANGULAR_Z() {
      Loader.load();
      return _geteLOCK_ANGULAR_Z();
   }

   public static PxRigidDynamicLockFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxRigidDynamicLockFlagEnum: " + value);
   }
}
