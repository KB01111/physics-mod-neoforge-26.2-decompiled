package net.diebuddies.physics.smoke;

import net.diebuddies.config.ConfigClient;

public final class SmokeColorConfig {
   private SmokeColorConfig() {
   }

   public static float fireVolumeColorRed() {
      return ConfigClient.smokeVolumeColorRed;
   }

   public static float fireVolumeColorGreen() {
      return ConfigClient.smokeVolumeColorGreen;
   }

   public static float fireVolumeColorBlue() {
      return ConfigClient.smokeVolumeColorBlue;
   }

   public static float steamVolumeColorRed() {
      return 0.65F;
   }

   public static float steamVolumeColorGreen() {
      return 0.65F;
   }

   public static float steamVolumeColorBlue() {
      return 0.65F;
   }
}
