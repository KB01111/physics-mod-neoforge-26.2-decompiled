package net.diebuddies.minecraft.weather;

import net.diebuddies.config.ConfigClient;
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
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RainParticle extends WeatherParticle {
   private static final Quaternionf Y_PI_ROT = new Quaternionf().rotationY((float) Math.PI);
   private Quaternionf movementRotation;
   private Quaternionf movementRotationFlipped;
   private Vector3f normal;

   public RainParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
      super(clientLevel, x, y, z, vx, vy, vz, sprite);
      this.quadSize = 0.23F;
      this.setColor(255, 255, 255, (int)((float)(175 + (int)((double)net.diebuddies.math.Math.random() * 40.0)) * ConfigClient.particleRainOpacity));
      double lengthSquared = Vector3d.lengthSquared(vx, vy, vz);
      if (lengthSquared != 0.0) {
         double invLength = 1.0 / org.joml.Math.sqrt(lengthSquared);
         vx *= invLength;
         vy *= invLength;
         vz *= invLength;
      } else {
         vy = -1.0;
      }

      this.dampingX = 1.0;
      this.dampingY = 1.0;
      this.dampingZ = 1.0;
      this.gravity = 0.0F;
      this.movementRotation = new Quaternionf();
      this.movementRotationFlipped = new Quaternionf();
      this.rotationTo(this.movementRotation, (float)vx, (float)vy, (float)vz).rotateX(net.diebuddies.math.Math.random() * 10.0F);
      this.normal = new Vector3f(0.0F, 0.0F, 1.0F).rotate(this.movementRotation).normalize();
      this.movementRotationFlipped.set(this.movementRotation).mul(Y_PI_ROT);
   }

   public Quaternionf rotationTo(Quaternionf src, float tx, float ty, float tz) {
      float dot = -tx;
      if (dot < -0.999999F) {
         src.x = 0.0F;
         src.y = 1.0F;
         src.z = 0.0F;
         src.w = 0.0F;
      } else {
         float sd2 = org.joml.Math.sqrt((1.0F + dot) * 2.0F);
         float isd2 = 1.0F / sd2;
         float cx = 0.0F;
         float cz = -ty;
         float x = cx * isd2;
         float y = tz * isd2;
         float z = cz * isd2;
         float w = sd2 * 0.5F;
         float n2 = org.joml.Math.invsqrt(org.joml.Math.fma(x, x, org.joml.Math.fma(y, y, org.joml.Math.fma(z, z, w * w))));
         src.x = x * n2;
         src.y = y * n2;
         src.z = z * n2;
         src.w = w * n2;
      }

      return src;
   }

   public void extract(QuadParticleRenderState quadParticleRenderState, Camera camera, float renderPercent) {
      Vec3 cameraPos = camera.position();
      double px = org.joml.Math.lerp(this.xo, this.x, (double)renderPercent);
      double py = org.joml.Math.lerp(this.yo, this.y, (double)renderPercent);
      double pz = org.joml.Math.lerp(this.zo, this.z, (double)renderPercent);
      float currentX = (float)(px - cameraPos.x());
      float currentY = (float)(py - cameraPos.y());
      float currentZ = (float)(pz - cameraPos.z());
      int light = this.getLightCoords(renderPercent);
      Quaternionf rotation = this.movementRotation;
      if (this.normal.dot(currentX, currentY, currentZ) >= 0.0F) {
         rotation = this.movementRotationFlipped;
      }

      quadParticleRenderState.add(
         this.getLayer(),
         currentX,
         currentY,
         currentZ,
         rotation.x,
         rotation.y,
         rotation.z,
         rotation.w,
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
         return new RainParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(net.diebuddies.math.Math.fastRandomSource));
      }
   }
}
