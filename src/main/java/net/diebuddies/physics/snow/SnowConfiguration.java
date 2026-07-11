package net.diebuddies.physics.snow;

import net.diebuddies.config.ConfigClient;

public class SnowConfiguration {
   public int snowType;
   public boolean snowSmoothShading;
   public int snowQuality;
   public float snowThickness;

   public SnowConfiguration() {
      this.snowType = ConfigClient.snowType;
      this.snowSmoothShading = ConfigClient.snowSmoothShading;
      this.snowQuality = ConfigClient.snowQuality;
      this.snowThickness = ConfigClient.snowThickness;
   }
}
