package net.diebuddies.physics.animation;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.math.Curve;
import net.diebuddies.physics.vines.Adjustable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;

public class Animation {
   @Adjustable(
      id = "Name",
      translationId = "physicsmod.prop.animation.name"
   )
   public String name;
   @Adjustable(
      id = "Animation Curve",
      translationId = "physicsmod.prop.animation.curve"
   )
   public CurveType curve;
   @Adjustable(
      id = "Despawn Speed",
      min = 0.0,
      max = 30.0,
      step = 0.01,
      translationId = "physicsmod.prop.animation.length"
   )
   public float speed;
   @Adjustable(
      id = "Type",
      translationId = "physicsmod.prop.animation.despawntype"
   )
   public AnimationType despawnType;
   @Adjustable(
      id = "Particle Spawn",
      translationId = "physicsmod.prop.animation.particlespawn"
   )
   public List<ParticleSpawn> particleSpawns;

   public Animation(String name, CurveType curve, float speed) {
      this.name = name;
      this.curve = curve;
      this.speed = speed;
      this.particleSpawns = new ObjectArrayList();
      this.despawnType = AnimationType.Shrink;
   }

   public Animation() {
      this.particleSpawns = new ObjectArrayList();
   }

   public void addParticleSpawn(
      ParticleOptions particle, int amount, double spread, double spawnChance, double vx, double vy, double vz, double soundVolume, SoundEvent sound
   ) {
      this.particleSpawns.add(new ParticleSpawn(particle, amount, spread, spawnChance, vx, vy, vz, soundVolume, sound));
   }

   public void removeParticleSpawn(ParticleSpawn particleSpawn) {
      this.particleSpawns.remove(particleSpawn);
   }

   public Curve getCurve() {
      return this.curve.getCurve();
   }

   public void set(Animation animation) {
      this.name = animation.name;
      this.curve = animation.curve;
      this.speed = animation.speed;
      this.particleSpawns = animation.particleSpawns;
      this.despawnType = animation.despawnType;
   }

   public Animation copy() {
      Animation animation = new Animation(this.name, this.curve, this.speed);
      animation.despawnType = this.despawnType;

      for (ParticleSpawn spawn : this.particleSpawns) {
         animation.particleSpawns.add(spawn.copy());
      }

      return animation;
   }
}
