package net.diebuddies.physics.ocean;

import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class SmallOceanSplashParticle extends OceanSplashParticle {
   public SmallOceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
      super(clientLevel, x, y, z, vx, vy, vz, sprite);
      float size = Math.random() * 0.6F + 0.15F;
      this.setSize(size, size);
      this.quadSize = size;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, RandomSource random
      ) {
         return new SmallOceanSplashParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(Math.fastRandomSource));
      }
   }
}
