package net.diebuddies.mixins.ocean;

import net.diebuddies.compat.ValkyrienSkies;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanRippleImpulse;
import net.diebuddies.physics.ocean.OceanWorld;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Entity.class})
public class MixinEntity implements EntityOcean {
   @Unique
   private double physicsOffset;
   @Unique
   private double physicsOldOffset;
   @Unique
   private double physicsRoll;
   @Unique
   private double physicsOldRoll;
   @Unique
   private double physicsPitch;
   @Unique
   private double physicsOldPitch;
   @Unique
   private double velocityY;
   @Unique
   private double velocityRoll;
   @Unique
   private double velocityPitch;
   @Unique
   private boolean wasInAir;
   @Unique
   private double rippleTime;
   @Unique
   private boolean wasEyeUnderwater;

   @Inject(
      at = {@At("HEAD")},
      method = {"doWaterSplashEffect"}
   )
   private void physicsmod$doOceanRippleEffect(CallbackInfo info) {
      Entity entity = (Entity)(Object)this;
   }

   @Unique
   private void spawnSplashRipple(Entity entity) {
      Vec3 deltaMovement = entity.getDeltaMovement();
      double vy = Math.abs(deltaMovement.y);
      if (!(vy < 0.1)) {
         if (entity.level() instanceof ClientLevel clientLevel) {
            float objectSize = Math.max(1.5F, entity.getBbWidth() * 1.2F);
            OceanWorld oceanWorld = PhysicsMod.getInstance(clientLevel).getPhysicsWorld().getOceanWorld();
            oceanWorld.queueRippleImpulse(OceanRippleImpulse.radial(entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), objectSize, 1.0F));
         }
      }
   }

   @Unique
   private void spawnSplashParticles(Entity entity) {
      Vec3 deltaMovement = entity.getDeltaMovement();
      double vy = Math.abs(deltaMovement.y);
      if (!(vy < 0.25)) {
         int splashamount = (int)net.diebuddies.math.Math.remapClamp(vy, 0.1, 2.0, 10.0, 75.0);
         double intensity = net.diebuddies.math.Math.remapClamp(vy, 0.1, 2.0, 0.075, 0.5);
         float volume = (float)intensity * ConfigClient.oceanSplashVolume;
         float pitch = net.diebuddies.math.Math.random() * 0.4F + 0.7F;
         Level level = entity.level();
         level.playLocalSound(
            entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), WeatherEffects.SPLASH_SOUND_EVENT, SoundSource.AMBIENT, volume, pitch, true
         );
         if (ConfigClient.oceanParticles) {
            OceanWorld.createWaterSplash(
               level, entity.getX(), entity.getY() + this.getPhysicsYOffset(), entity.getZ(), 0.0, 0.0, 0.0, 0.25, intensity, splashamount
            );
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   private void physicsmod$spawnMovementRippleEffect(CallbackInfo info) {
      Entity entity = (Entity)(Object)this;
   }

   @Unique
   private void spawnMovementRipple(OceanWorld oceanWorld, Entity entity, double x, double y, double z, double speed) {
      if (entity instanceof AbstractBoat) {
         oceanWorld.spawnBoatRipple(x, y, z, speed);
      } else {
         oceanWorld.spawnRipple(Math.max(1.5F, entity.getBbWidth() * 1.2F), x, y, z, speed);
      }
   }

   @Unique
   private boolean isCausingSplash(Entity entity) {
      boolean isEyeUnderwater = entity.isEyeInFluid(FluidTags.WATER);
      boolean result = !isEyeUnderwater && this.wasEyeUnderwater;
      this.wasEyeUnderwater = isEyeUnderwater;
      return result;
   }

   @Unique
   private boolean isCausingRipples(Entity entity) {
      boolean isOnSurface = true;
      if (!ConfigClient.oceanStickyEntities && entity instanceof AbstractBoat) {
         isOnSurface = !((EntityOcean)entity).isInPhysicsAir();
      }

      return isOnSurface
         && !entity.isSpectator()
         && entity.isInWater()
         && (
            entity.level().getBlockState(entity.blockPosition().above()).isAir()
               || entity instanceof AbstractClientPlayer && entity.level().getBlockState(entity.blockPosition().above().above()).isAir()
         );
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   private void physicsmod$updateOceanTransformations(CallbackInfo info) {
      Entity entity = (Entity)(Object)this;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"pick"},
      cancellable = true
   )
   private void physicsmod$modifyBlockPickingForOceanTransformations(double range, float renderPercent, boolean bl, CallbackInfoReturnable<HitResult> info) {
      Entity entity = (Entity)(Object)this;
   }

   @Override
   public double getPhysicsYOffset(float renderPercent) {
      return PhysicsMod.stopOceanDisplacement ? 0.0 : Mth.lerp((double)renderPercent, this.getPhysicsOldYOffset(), this.getPhysicsYOffset());
   }

   @Override
   public double getPhysicsPitch(float renderPercent) {
      return PhysicsMod.stopOceanDisplacement ? 0.0 : Mth.lerp((double)renderPercent, this.getPhysicsOldPitch(), this.getPhysicsPitch());
   }

   @Override
   public double getPhysicsRoll(float renderPercent) {
      return PhysicsMod.stopOceanDisplacement ? 0.0 : Mth.lerp((double)renderPercent, this.getPhysicsOldRoll(), this.getPhysicsRoll());
   }

   @Override
   public double getPhysicsYOffset() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsYOffset() : this.physicsOffset;
   }

   @Override
   public double getPhysicsOldYOffset() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsOldYOffset() : this.physicsOldOffset;
   }

   @Override
   public double getPhysicsPitch() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsPitch() : this.physicsPitch;
   }

   @Override
   public double getPhysicsOldPitch() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsOldPitch() : this.physicsOldPitch;
   }

   @Override
   public double getPhysicsRoll() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsRoll() : this.physicsRoll;
   }

   @Override
   public double getPhysicsOldRoll() {
      Entity entity = (Entity)(Object)this;
      EntityOcean vehicle = (EntityOcean)entity.getVehicle();
      if (StarterClient.valkyrienSkies && vehicle == null) {
         vehicle = ValkyrienSkies.hasShipMount(entity);
      }

      return vehicle != null ? vehicle.getPhysicsOldRoll() : this.physicsOldRoll;
   }

   @Override
   public boolean isInPhysicsAir() {
      return this.wasInAir;
   }
}
