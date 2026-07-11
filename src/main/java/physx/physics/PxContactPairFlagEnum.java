package physx.physics;

import de.fabmax.physxjni.Loader;

public enum PxContactPairFlagEnum {
   eREMOVED_SHAPE_0(geteREMOVED_SHAPE_0()),
   eREMOVED_SHAPE_1(geteREMOVED_SHAPE_1()),
   eACTOR_PAIR_HAS_FIRST_TOUCH(geteACTOR_PAIR_HAS_FIRST_TOUCH()),
   eACTOR_PAIR_LOST_TOUCH(geteACTOR_PAIR_LOST_TOUCH()),
   eINTERNAL_HAS_IMPULSES(geteINTERNAL_HAS_IMPULSES()),
   eINTERNAL_CONTACTS_ARE_FLIPPED(geteINTERNAL_CONTACTS_ARE_FLIPPED());

   public final int value;

   private PxContactPairFlagEnum(int value) {
      this.value = value;
   }

   private static native int _geteREMOVED_SHAPE_0();

   private static int geteREMOVED_SHAPE_0() {
      Loader.load();
      return _geteREMOVED_SHAPE_0();
   }

   private static native int _geteREMOVED_SHAPE_1();

   private static int geteREMOVED_SHAPE_1() {
      Loader.load();
      return _geteREMOVED_SHAPE_1();
   }

   private static native int _geteACTOR_PAIR_HAS_FIRST_TOUCH();

   private static int geteACTOR_PAIR_HAS_FIRST_TOUCH() {
      Loader.load();
      return _geteACTOR_PAIR_HAS_FIRST_TOUCH();
   }

   private static native int _geteACTOR_PAIR_LOST_TOUCH();

   private static int geteACTOR_PAIR_LOST_TOUCH() {
      Loader.load();
      return _geteACTOR_PAIR_LOST_TOUCH();
   }

   private static native int _geteINTERNAL_HAS_IMPULSES();

   private static int geteINTERNAL_HAS_IMPULSES() {
      Loader.load();
      return _geteINTERNAL_HAS_IMPULSES();
   }

   private static native int _geteINTERNAL_CONTACTS_ARE_FLIPPED();

   private static int geteINTERNAL_CONTACTS_ARE_FLIPPED() {
      Loader.load();
      return _geteINTERNAL_CONTACTS_ARE_FLIPPED();
   }

   public static PxContactPairFlagEnum forValue(int value) {
      for (int i = 0; i < values().length; i++) {
         if (values()[i].value == value) {
            return values()[i];
         }
      }

      throw new IllegalArgumentException("Unknown value for enum PxContactPairFlagEnum: " + value);
   }
}
