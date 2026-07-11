package net.diebuddies.minecraft.weather;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class SnowParticle extends WeatherParticle {
   public SnowParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite textureAtlasSprite) {
      super(clientLevel, x, y, z, vx, vy, vz, textureAtlasSprite);
      this.gravity = 0.002F;
      this.dampingX = 0.999;
      this.dampingY = 0.98;
      this.dampingZ = 0.999;
      this.setColor(255, 255, 255, (int)((float)(155 + (int)((double)Math.random() * 40.0)) * ConfigClient.particleSnowOpacity));
   }

   public void extract(QuadParticleRenderState quadParticleRenderState, Camera camera, float renderPercent) {
      Vec3 cameraPos = camera.position();
      double px = org.joml.Math.lerp(this.xo, this.x, (double)renderPercent);
      double py = org.joml.Math.lerp(this.yo, this.y, (double)renderPercent);
      double pz = org.joml.Math.lerp(this.zo, this.z, (double)renderPercent);
      float currentX = (float)(px - cameraPos.x());
      float currentY = (float)(py - cameraPos.y());
      float currentZ = (float)(pz - cameraPos.z());
      Quaternionf cameraRotation = camera.rotation();
      int light = this.getLightCoords(renderPercent);
      quadParticleRenderState.add(
         this.getLayer(),
         currentX,
         currentY,
         currentZ,
         cameraRotation.x,
         cameraRotation.y,
         cameraRotation.z,
         cameraRotation.w,
         this.quadSize,
         this.getU0(),
         this.getU1(),
         this.getV0(),
         this.getV1(),
         this.argb,
         light
      );
   }

   @Override
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
         return new SnowParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(Math.fastRandomSource));
      }
   }
}
