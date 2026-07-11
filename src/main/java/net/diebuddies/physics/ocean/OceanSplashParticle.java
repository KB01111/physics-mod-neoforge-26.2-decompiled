package net.diebuddies.physics.ocean;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class OceanSplashParticle extends SingleQuadParticle {
   private float baseAlpha;

   public OceanSplashParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0, sprite);
      float size = Math.random() * 0.9F + 0.3F;
      this.setSize(size, size);
      this.setLifetime(Math.randomInt(7) + 5);
      this.quadSize = size;
      this.gravity = 0.981F;
      this.roll = Math.random() * (float) java.lang.Math.PI * 2.0F;
      this.oRoll = this.roll;
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      float modifier = ConfigClient.oceanParticleAlpha * 0.5F;
      this.baseAlpha = Math.random() * modifier + modifier;
      this.setPos(x + this.xd, y + this.yd, z + this.zd);
   }

   public void extract(QuadParticleRenderState quadParticleRenderState, Camera camera, float renderPercent) {
      this.setAlpha((1.0F - org.joml.Math.min(1.0F, ((float)this.age + renderPercent) / (float)this.lifetime)) * this.baseAlpha);
      super.extract(quadParticleRenderState, camera, renderPercent);
   }

   public double getX(float renderPercent) {
      return Mth.lerp((double)renderPercent, this.xo, this.x);
   }

   public double getY(float renderPercent) {
      return Mth.lerp((double)renderPercent, this.yo, this.y);
   }

   public double getZ(float renderPercent) {
      return Mth.lerp((double)renderPercent, this.zo, this.z);
   }

   public Layer getLayer() {
      return Layer.TRANSLUCENT;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, RandomSource random
      ) {
         return new OceanSplashParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(Math.fastRandomSource));
      }
   }
}
