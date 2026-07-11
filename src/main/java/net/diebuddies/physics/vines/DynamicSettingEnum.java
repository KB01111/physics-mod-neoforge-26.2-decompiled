package net.diebuddies.physics.vines;

public enum DynamicSettingEnum {
   VINE(0, VineSetting.class),
   DOOR(1, DoorSetting.class),
   TRAPDOOR(2, TrapdoorSetting.class),
   HANGING_SIGN(3, HangingSignSetting.class);

   private final int id;
   private final Class<?> clazz;

   private DynamicSettingEnum(int id, Class<?> clazz) {
      this.id = id;
      this.clazz = clazz;
   }

   public int getID() {
      return this.id;
   }

   public Class<?> getType() {
      return this.clazz;
   }
}
