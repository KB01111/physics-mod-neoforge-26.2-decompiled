package net.diebuddies.physics.ocean;

import net.diebuddies.math.Math;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

public class ExplosionOceanSplashParticle extends OceanSplashParticle {
   private static MutableBlockPos tmp = new MutableBlockPos();

   public ExplosionOceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0, sprite);
      float size = Math.random() * 0.9F + 0.3F;
      this.setSize(size, size);
      this.setLifetime(Math.randomInt(17) + 25);
      this.quadSize = size;
      this.gravity = 0.981F;
      this.roll = Math.random() * (float) java.lang.Math.PI * 2.0F;
      this.oRoll = this.roll;
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      this.setPos(x + this.xd, y + this.yd, z + this.zd);
   }

   public void tick() {
      super.tick();
      if (!this.level.getBlockState(tmp.set(this.x, this.y, this.z)).isAir()) {
         this.remove();
      }
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, RandomSource random
      ) {
         return new ExplosionOceanSplashParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(Math.fastRandomSource));
      }
   }
}
