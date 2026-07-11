package net.diebuddies.physics.settings.mobs;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.vines.Adjustable;

public class MobSetting {
   public static final double LIFETIME_MAX = 100.0;
   public static final double LIFETIME_VARIANCE_MAX = 30.0;
   public static final double SCALE_MAX = 8.0;
   @Adjustable(
      id = "Physics Type",
      translationId = "physicsmod.prop.mob.type"
   )
   public MobPhysicsType type;
   @Adjustable(
      id = "Lifetime",
      min = 0.0,
      max = 100.0,
      step = 0.1,
      maxTranslationId = "physicsmod.prop.mainrule",
      translationId = "physicsmod.prop.mob.lifetime"
   )
   public double lifetime;
   @Adjustable(
      id = "Lifetime Variance",
      min = 0.0,
      max = 30.0,
      step = 0.1,
      maxTranslationId = "physicsmod.prop.mainrule",
      translationId = "physicsmod.prop.mob.lifetimevariance"
   )
   public double lifetimeVariance;
   @Adjustable(
      id = "Animation",
      translationId = "physicsmod.animation"
   )
   public Animation animation;

   public MobSetting(MobPhysicsType type, double lifetime, double lifetimeVariance, Animation animation) {
      this.type = type;
      this.lifetime = lifetime;
      this.lifetimeVariance = lifetimeVariance;
      this.animation = animation;
   }

   public MobSetting() {
      this(MobPhysicsType.MAIN_RULE, 100.0, 30.0, null);
   }

   public MobPhysicsType getType() {
      if (!ConfigClient.mobPhysics) {
         return MobPhysicsType.OFF;
      } else {
         return this.type == MobPhysicsType.MAIN_RULE ? ConfigClient.mobSetting.type : this.type;
      }
   }

   public double getLifetime() {
      return this.lifetime == 100.0 ? ConfigClient.mobSetting.lifetime : this.lifetime;
   }

   public double getLifetimeVariance() {
      return this.lifetimeVariance == 30.0 ? ConfigClient.mobSetting.lifetimeVariance : this.lifetimeVariance;
   }

   public Animation getAnimation() {
      return this.animation == null ? ConfigClient.mobSetting.animation : this.animation;
   }

   public MobSetting copy() {
      return new MobSetting(this.type, this.lifetime, this.lifetimeVariance, this.animation);
   }
}
