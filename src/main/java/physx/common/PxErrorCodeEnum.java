package physx.common;

import de.fabmax.physxjni.Loader;

public enum PxErrorCodeEnum {
   eNO_ERROR(geteNO_ERROR()),
   eDEBUG_INFO(geteDEBUG_INFO()),
   eDEBUG_WARNING(geteDEBUG_WARNING()),
   eINVALID_PARAMETER(geteINVALID_PARAMETER()),
   eINVALID_OPERATION(geteINVALID_OPERATION()),
   eOUT_OF_MEMORY(geteOUT_OF_MEMORY()),
   eINTERNAL_ERROR(geteINTERNAL_ERROR()),
   eABORT(geteABORT()),
   ePERF_WARNING(getePERF_WARNING()),
   eMASK_ALL(geteMASK_ALL());

   public final int value;

   private PxErrorCodeEnum(int value) {
      this.value = value;
   }

   private static native int _geteNO_ERROR();

   private static int geteNO_ERROR() {
      Loader.load();
      return _geteNO_ERROR();
   }

   private static native int _geteDEBUG_INFO();

   private static int geteDEBUG_INFO() {
      Loader.load();
      return _geteDEBUG_INFO();
   }

   private static native int _geteDEBUG_WARNING();

   private static int geteDEBUG_WARNING() {
      Loader.load();
      return _geteDEBUG_WARNING();
   }

   private static native int _geteINVALID_PARAMETER();

   private static int geteINVALID_PARAMETER() {
      Loader.load();
      return _geteINVALID_PARAMETER();
   }

   private static native int _geteINVALID_OPERATION();

   private static int geteINVALID_OPERATION() {
      Loader.load();
      return _geteINVALID_OPERATION();
   }

   private static native int _geteOUT_OF_MEMORY();

   private static int geteOUT_OF_MEMORY() {
      Loader.load();
      return _geteOUT_OF_MEMORY();
   }

   private static native int _geteINTERNAL_ERROR();

   private static int geteINTERNAL_ERROR() {
      Loader.load();
      return _geteINTERNAL_ERROR();
   }

   private static native int _geteABORT();

   private static int geteABORT() {
      Loader.load();
      return _geteABORT();
   }

   private static native int _getePERF_WARNING();

   private static int getePERF_WARNING() {
      Loader.load();
      return _getePERF_WARNING();
   }

   private static native int _geteMASK_ALL();

   private static int geteMASK_ALL() {
      Loader.load();
      return _geteMASK_ALL();
   }

   public static PxErrorCodeEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxErrorCodeEnum: " + value);
   }
}
