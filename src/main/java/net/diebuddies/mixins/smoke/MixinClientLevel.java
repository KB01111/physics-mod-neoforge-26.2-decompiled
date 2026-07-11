package net.diebuddies.mixins.smoke;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientLevel.class})
public class MixinClientLevel {
   @Unique
   private Set<ParticleType<?>> smokeSpawners;

   @Inject(
      at = {@At("HEAD")},
      method = {"doAddParticle(Lnet/minecraft/core/particles/ParticleOptions;ZZDDDDDD)V"},
      cancellable = true
   )
   public void addParticle(
      ParticleOptions particleOptions, boolean bl, boolean bl2, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info
   ) {
      this.spawnPhysicsSmoke(particleOptions, x, y, z, vx, vy, vz, info);
   }

   @Unique
   private void spawnPhysicsSmoke(ParticleOptions particleOptions, double x, double y, double z, double vx, double vy, double vz, CallbackInfo info) {
      boolean spawnSmoke = false;
      if (!PhysicsMod.smokeParticlePrevention) {
         if (spawnSmoke && this.getSmokeSpawners().contains(particleOptions.getType())) {
            if (SmokeHelper.addParticle((ClientLevel)(Object)this, x, y, z, ConfigClient.smokeOther)) {
               info.cancel();
            }
         }
      }
   }

   @Unique
   private Set<ParticleType<?>> getSmokeSpawners() {
      if (this.smokeSpawners == null) {
         this.smokeSpawners = new ObjectOpenHashSet();
         this.smokeSpawners.add(ParticleTypes.LARGE_SMOKE);
         this.smokeSpawners.add(ParticleTypes.CAMPFIRE_COSY_SMOKE);
         this.smokeSpawners.add(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE);
      }

      return this.smokeSpawners;
   }
}
