package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxPairFlagEnum {
   eSOLVE_CONTACT(geteSOLVE_CONTACT()),
   eMODIFY_CONTACTS(geteMODIFY_CONTACTS()),
   eNOTIFY_TOUCH_FOUND(geteNOTIFY_TOUCH_FOUND()),
   eNOTIFY_TOUCH_PERSISTS(geteNOTIFY_TOUCH_PERSISTS()),
   eNOTIFY_TOUCH_LOST(geteNOTIFY_TOUCH_LOST()),
   eNOTIFY_TOUCH_CCD(geteNOTIFY_TOUCH_CCD()),
   eNOTIFY_THRESHOLD_FORCE_FOUND(geteNOTIFY_THRESHOLD_FORCE_FOUND()),
   eNOTIFY_THRESHOLD_FORCE_PERSISTS(geteNOTIFY_THRESHOLD_FORCE_PERSISTS()),
   eNOTIFY_THRESHOLD_FORCE_LOST(geteNOTIFY_THRESHOLD_FORCE_LOST()),
   eNOTIFY_CONTACT_POINTS(geteNOTIFY_CONTACT_POINTS()),
   eDETECT_DISCRETE_CONTACT(geteDETECT_DISCRETE_CONTACT()),
   eDETECT_CCD_CONTACT(geteDETECT_CCD_CONTACT()),
   ePRE_SOLVER_VELOCITY(getePRE_SOLVER_VELOCITY()),
   ePOST_SOLVER_VELOCITY(getePOST_SOLVER_VELOCITY()),
   eCONTACT_EVENT_POSE(geteCONTACT_EVENT_POSE()),
   eNEXT_FREE(geteNEXT_FREE()),
   eCONTACT_DEFAULT(geteCONTACT_DEFAULT()),
   eTRIGGER_DEFAULT(geteTRIGGER_DEFAULT());

   public final int value;

   private PxPairFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteSOLVE_CONTACT();

   private static int geteSOLVE_CONTACT() {
      Loader.load();
      return _geteSOLVE_CONTACT();
   }

   private static native int _geteMODIFY_CONTACTS();

   private static int geteMODIFY_CONTACTS() {
      Loader.load();
      return _geteMODIFY_CONTACTS();
   }

   private static native int _geteNOTIFY_TOUCH_FOUND();

   private static int geteNOTIFY_TOUCH_FOUND() {
      Loader.load();
      return _geteNOTIFY_TOUCH_FOUND();
   }

   private static native int _geteNOTIFY_TOUCH_PERSISTS();

   private static int geteNOTIFY_TOUCH_PERSISTS() {
      Loader.load();
      return _geteNOTIFY_TOUCH_PERSISTS();
   }

   private static native int _geteNOTIFY_TOUCH_LOST();

   private static int geteNOTIFY_TOUCH_LOST() {
      Loader.load();
      return _geteNOTIFY_TOUCH_LOST();
   }

   private static native int _geteNOTIFY_TOUCH_CCD();

   private static int geteNOTIFY_TOUCH_CCD() {
      Loader.load();
      return _geteNOTIFY_TOUCH_CCD();
   }

   private static native int _geteNOTIFY_THRESHOLD_FORCE_FOUND();

   private static int geteNOTIFY_THRESHOLD_FORCE_FOUND() {
      Loader.load();
      return _geteNOTIFY_THRESHOLD_FORCE_FOUND();
   }

   private static native int _geteNOTIFY_THRESHOLD_FORCE_PERSISTS();

   private static int geteNOTIFY_THRESHOLD_FORCE_PERSISTS() {
      Loader.load();
      return _geteNOTIFY_THRESHOLD_FORCE_PERSISTS();
   }

   private static native int _geteNOTIFY_THRESHOLD_FORCE_LOST();

   private static int geteNOTIFY_THRESHOLD_FORCE_LOST() {
      Loader.load();
      return _geteNOTIFY_THRESHOLD_FORCE_LOST();
   }

   private static native int _geteNOTIFY_CONTACT_POINTS();

   private static int geteNOTIFY_CONTACT_POINTS() {
      Loader.load();
      return _geteNOTIFY_CONTACT_POINTS();
   }

   private static native int _geteDETECT_DISCRETE_CONTACT();

   private static int geteDETECT_DISCRETE_CONTACT() {
      Loader.load();
      return _geteDETECT_DISCRETE_CONTACT();
   }

   private static native int _geteDETECT_CCD_CONTACT();

   private static int geteDETECT_CCD_CONTACT() {
      Loader.load();
      return _geteDETECT_CCD_CONTACT();
   }

   private static native int _getePRE_SOLVER_VELOCITY();

   private static int getePRE_SOLVER_VELOCITY() {
      Loader.load();
      return _getePRE_SOLVER_VELOCITY();
   }

   private static native int _getePOST_SOLVER_VELOCITY();

   private static int getePOST_SOLVER_VELOCITY() {
      Loader.load();
      return _getePOST_SOLVER_VELOCITY();
   }

   private static native int _geteCONTACT_EVENT_POSE();

   private static int geteCONTACT_EVENT_POSE() {
      Loader.load();
      return _geteCONTACT_EVENT_POSE();
   }

   private static native int _geteNEXT_FREE();

   private static int geteNEXT_FREE() {
      Loader.load();
      return _geteNEXT_FREE();
   }

   private static native int _geteCONTACT_DEFAULT();

   private static int geteCONTACT_DEFAULT() {
      Loader.load();
      return _geteCONTACT_DEFAULT();
   }

   private static native int _geteTRIGGER_DEFAULT();

   private static int geteTRIGGER_DEFAULT() {
      Loader.load();
      return _geteTRIGGER_DEFAULT();
   }

   public static PxPairFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxPairFlagEnum: " + value);
   }
}
