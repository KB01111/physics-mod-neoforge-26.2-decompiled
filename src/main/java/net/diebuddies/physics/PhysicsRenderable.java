package net.diebuddies.physics;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.ParticleSpawn;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PhysicsRenderable {
   public Vector3f position;
   public Vector3f oldPosition;
   public Quaternionf rotation;
   public Quaternionf oldRotation;
   public Vector3f scale;
   public final Matrix4f renderTransformation;
   public List<Model> models = new ObjectArrayList(1);
   private float boundingSphereRadius;
   public Object info;
   public SoundType sound;
   public long lastSoundTime;
   public PhysicsEntity.Type type;
   private Animation animation;
   public float time;
   private int color;
   private int cachedBrightness;
   private long cachedBrightnessPos = Long.MAX_VALUE;
   private boolean dead;

   public PhysicsRenderable(PhysicsEntity entity) {
      this.models = entity.models;
      this.renderTransformation = new Matrix4f(entity.getTransformation());
      this.position = this.renderTransformation.getTranslation(new Vector3f());
      this.rotation = this.renderTransformation.getNormalizedRotation(new Quaternionf());
      this.scale = this.renderTransformation.getScale(new Vector3f());
      this.oldPosition = new Vector3f(this.position);
      this.oldRotation = new Quaternionf(this.rotation);
      this.animation = entity.getAnimation();
      this.info = entity.info;
      this.type = entity.type;
      this.time = entity.time;
      this.color = entity.getBGRA();
      this.sound = entity.sound;
      this.calculateBoundingSphereRadius();
   }

   public Matrix4f getRenderTransformation() {
      return this.renderTransformation;
   }

   private void calculateBoundingSphereRadius() {
      if (this.models != null && this.models.size() != 0) {
         for (int i = 0; i < this.models.size(); i++) {
            Mesh mesh = this.models.get(i).mesh;
            if (mesh != null) {
               this.boundingSphereRadius = Math.max(this.boundingSphereRadius, mesh.getRadius());
            }
         }

         this.boundingSphereRadius = this.boundingSphereRadius * Math.max(this.scale.x, Math.max(this.scale.y, this.scale.z));
      }
   }

   public float getBoundingSphereRadius() {
      return this.boundingSphereRadius;
   }

   public void destroy() {
      this.destroyModels();
   }

   public void destroyModels() {
      if (this.models != null) {
         for (int i = 0; i < this.models.size(); i++) {
            Model model = this.models.get(i);
            model.freeRenderData();
         }
      }
   }

   public int getLight(Level level, MutableBlockPos blockPos) {
      if (!StarterClient.disableLightingCache) {
         long newPos = MutableBlockPos.asLong(blockPos.getX(), blockPos.getY(), blockPos.getZ());
         if (this.cachedBrightnessPos == newPos) {
            return this.cachedBrightness;
         }

         this.cachedBrightnessPos = newPos;
      }

      BlockState bState = level.getBlockState(blockPos);
      int x = blockPos.getX();
      int y = blockPos.getY();
      int z = blockPos.getZ();
      int brightness = 0;
      if (!bState.canOcclude()) {
         brightness = LightCoordsUtil.getLightCoords(level, blockPos);
      } else {
         bState = level.getBlockState(blockPos.set(x, y + 1, z));
         if (!bState.canOcclude()) {
            brightness = LightCoordsUtil.getLightCoords(level, blockPos);
         } else {
            bState = level.getBlockState(blockPos.set(x, y - 1, z));
            if (!bState.canOcclude()) {
               brightness = LightCoordsUtil.getLightCoords(level, blockPos);
            } else {
               bState = level.getBlockState(blockPos.set(x, y, z - 1));
               if (!bState.canOcclude()) {
                  brightness = LightCoordsUtil.getLightCoords(level, blockPos);
               } else {
                  bState = level.getBlockState(blockPos.set(x + 1, y, z));
                  if (!bState.canOcclude()) {
                     brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                  } else {
                     bState = level.getBlockState(blockPos.set(x, y, z + 1));
                     if (!bState.canOcclude()) {
                        brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                     } else {
                        bState = level.getBlockState(blockPos.set(x - 1, y, z));
                        if (!bState.canOcclude()) {
                           brightness = LightCoordsUtil.getLightCoords(level, blockPos);
                        }
                     }
                  }
               }
            }
         }
      }

      blockPos.set(x, y, z);
      if (!StarterClient.disableLightingCache) {
         this.cachedBrightness = brightness;
      }

      return brightness;
   }

   public void invalidateBrightness() {
      this.cachedBrightnessPos = Long.MAX_VALUE;
   }

   public long getCachedBrightnessPos() {
      return this.cachedBrightnessPos;
   }

   public double getDespawnSpeed() {
      return (double)this.animation.speed;
   }

   public void spawnDeathAnimation(PhysicsWorld world, boolean playSound) {
      if (!this.dead) {
         Level level = world.getWorld();
         List<ParticleSpawn> particleSpawns = this.animation.particleSpawns;

         for (int i = 0; i < particleSpawns.size(); i++) {
            ParticleSpawn particleSpawn = particleSpawns.get(i);
            if (particleSpawn.particle != null && (double)net.diebuddies.math.Math.random() < particleSpawn.spawnChance) {
               for (int j = 0; j < particleSpawn.amount; j++) {
                  double halfSpread = particleSpawn.spread * 0.5;
                  double px = (double)this.position.x + world.getOffset().x + (double)net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  double py = (double)this.position.y + world.getOffset().y + (double)net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  double pz = (double)this.position.z + world.getOffset().z + (double)net.diebuddies.math.Math.random() * particleSpawn.spread - halfSpread;
                  level.addParticle(particleSpawn.particle, px, py, pz, particleSpawn.vx, particleSpawn.vy, particleSpawn.vz);
               }

               if (particleSpawn.sound != null && playSound) {
                  float pitch = 0.85F + net.diebuddies.math.Math.random() * 0.3F;
                  level.playLocalSound(
                     (double)this.position.x + world.getOffset().x,
                     (double)this.position.y + world.getOffset().y,
                     (double)this.position.z + world.getOffset().z,
                     particleSpawn.sound,
                     SoundSource.HOSTILE,
                     (float)particleSpawn.soundVolume,
                     pitch,
                     true
                  );
               }
            }
         }

         this.dead = true;
      }
   }

   public void startDespawnAnimation(Level level) {
      if (this.time > this.animation.speed) {
         this.time = this.animation.speed;
      }
   }

   public boolean isDespawning() {
      return this.time <= this.animation.speed;
   }

   public float getDespawnScale(Level level) {
      if (this.time > this.animation.speed) {
         return 1.0F;
      } else {
         return (double)this.time <= 0.0 ? 0.0F : this.animation.getCurve().get(this.time / this.animation.speed);
      }
   }

   public AnimationType getAnimationType() {
      return this.animation.despawnType;
   }

   public float getRed() {
      return (float)(this.color >> 16 & 0xFF) * 0.003921569F;
   }

   public float getGreen() {
      return (float)(this.color >> 8 & 0xFF) * 0.003921569F;
   }

   public float getBlue() {
      return (float)(this.color & 0xFF) * 0.003921569F;
   }

   public float getAlpha() {
      return (float)(this.color >> 24 & 0xFF) * 0.003921569F;
   }

   public int getBGRA() {
      return this.color;
   }
}
