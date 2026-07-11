package net.diebuddies.mixins.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.smoke.SmokeHelper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Blaze.class})
public class MixinBlaze {
   @Redirect(
      method = {"aiStep"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;addParticle(Lnet/minecraft/core/particles/ParticleOptions;DDDDDD)V"
      )
   )
   private void addParticle(Level level, ParticleOptions options, double x, double y, double z, double vx, double vy, double vz) {
      if (!SmokeHelper.addParticle(level, x, y, z, ConfigClient.smokeBlaze)) {
         PhysicsMod.smokeParticlePrevention = true;
         level.addParticle(options, x, y, z, vx, vy, vz);
         PhysicsMod.smokeParticlePrevention = false;
      }
   }
}
