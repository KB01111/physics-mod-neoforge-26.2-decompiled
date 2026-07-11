package net.diebuddies.physics.settings.blocks;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.vines.Adjustable;

public class BlockSetting {
   public static final double LIFETIME_MAX = 100.0;
   public static final double LIFETIME_VARIANCE_MAX = 30.0;
   public static final double SCALE_MAX = 8.0;
   @Adjustable(
      id = "Physics Type",
      translationId = "physicsmod.prop.block.type"
   )
   public BlockPhysicsType type;
   @Adjustable(
      id = "Lifetime",
      min = 0.0,
      max = 100.0,
      step = 0.1,
      maxTranslationId = "physicsmod.prop.mainrule",
      translationId = "physicsmod.prop.block.lifetime"
   )
   public double lifetime;
   @Adjustable(
      id = "Lifetime Variance",
      min = 0.0,
      max = 30.0,
      step = 0.1,
      maxTranslationId = "physicsmod.prop.mainrule",
      translationId = "physicsmod.prop.block.lifetimevariance"
   )
   public double lifetimeVariance;
   @Adjustable(
      id = "Scale",
      min = 0.05,
      max = 8.0,
      step = 0.01,
      maxTranslationId = "physicsmod.prop.mainrule",
      translationId = "physicsmod.prop.block.scale"
   )
   public double scale;
   @Adjustable(
      id = "Animation",
      translationId = "physicsmod.animation"
   )
   public Animation animation;

   public BlockSetting(BlockPhysicsType type, double lifetime, double lifetimeVariance, double scale, Animation animation) {
      this.type = type;
      this.lifetime = lifetime;
      this.lifetimeVariance = lifetimeVariance;
      this.scale = scale;
      this.animation = animation;
   }

   public BlockSetting() {
      this(BlockPhysicsType.MAIN_RULE, 100.0, 30.0, 8.0, null);
   }

   public BlockPhysicsType getType() {
      if (!ConfigClient.blockPhysics) {
         return BlockPhysicsType.OFF;
      } else {
         return this.type == BlockPhysicsType.MAIN_RULE ? ConfigClient.blockSetting.type : this.type;
      }
   }

   public double getLifetime() {
      return this.lifetime == 100.0 ? ConfigClient.blockSetting.lifetime : this.lifetime;
   }

   public double getLifetimeVariance() {
      return this.lifetimeVariance == 30.0 ? ConfigClient.blockSetting.lifetimeVariance : this.lifetimeVariance;
   }

   public double getScale() {
      return this.scale == 8.0 ? ConfigClient.blockSetting.scale : this.scale;
   }

   public Animation getAnimation() {
      return this.animation == null ? ConfigClient.blockSetting.animation : this.animation;
   }

   public BlockSetting copy() {
      return new BlockSetting(this.type, this.lifetime, this.lifetimeVariance, this.scale, this.animation);
   }
}
