package net.diebuddies.physics.animation;

import net.diebuddies.physics.vines.Adjustable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;

public class ParticleSpawn {
   @Adjustable(
      id = "Type",
      translationId = "physicsmod.prop.particle.type"
   )
   public ParticleOptions particle;
   @Adjustable(
      id = "Amount",
      min = 0.0,
      max = 100.0,
      step = 1.0,
      translationId = "physicsmod.prop.particle.amount"
   )
   public int amount;
   @Adjustable(
      id = "Spread Radius",
      min = 0.0,
      max = 10.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.spread"
   )
   public double spread;
   @Adjustable(
      id = "Spawn Chance",
      min = 0.0,
      max = 1.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.spawnchance"
   )
   public double spawnChance;
   @Adjustable(
      id = "Velocity X",
      min = -5.0,
      max = 5.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.velocityx"
   )
   public double vx;
   @Adjustable(
      id = "Velocity Y",
      min = -5.0,
      max = 5.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.velocityy"
   )
   public double vy;
   @Adjustable(
      id = "Velocity Z",
      min = -5.0,
      max = 5.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.velocityz"
   )
   public double vz;
   @Adjustable(
      id = "Sound Volume",
      min = 0.0,
      max = 2.0,
      step = 0.01,
      translationId = "physicsmod.prop.particle.soundvolume"
   )
   public double soundVolume;
   @Adjustable(
      id = "Sound",
      translationId = "physicsmod.prop.particle.sound"
   )
   public SoundEvent sound;

   public ParticleSpawn(
      ParticleOptions particle, int amount, double spread, double spawnChance, double vx, double vy, double vz, double soundVolume, SoundEvent sound
   ) {
      this.particle = particle;
      this.amount = amount;
      this.spread = spread;
      this.spawnChance = spawnChance;
      this.vx = vx;
      this.vy = vy;
      this.vz = vz;
      this.sound = sound;
      this.soundVolume = soundVolume;
   }

   public ParticleSpawn() {
      this.particle = ParticleTypes.SMOKE;
      this.amount = 1;
      this.spread = 0.2;
      this.spawnChance = 1.0;
      this.soundVolume = 0.1;
   }

   public ParticleSpawn copy() {
      return new ParticleSpawn(this.particle, this.amount, this.spread, this.spawnChance, this.vx, this.vy, this.vz, this.soundVolume, this.sound);
   }
}
