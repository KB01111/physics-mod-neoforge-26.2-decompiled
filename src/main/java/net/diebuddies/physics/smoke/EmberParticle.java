package net.diebuddies.physics.smoke;

import net.diebuddies.math.Math;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class EmberParticle extends SingleQuadParticle {
   private static final double TURB_STRENGTH = 0.00825;
   private static final double TURB_FREQ = 1.1;
   private static final double TURB_SPEED = 0.06;
   private static final double GUST_STRENGTH = 0.22;
   private static final double TURB_RAMP_TICKS = 3.0;
   protected double lastX;
   protected double lastY;
   protected double lastZ;
   protected MutableBlockPos tmpPos;
   protected MutableBlockPos lastBlockPos;
   protected int cachedBrightness;
   protected double dampingX = 0.98;
   protected double dampingY = 0.98;
   protected double dampingZ = 0.98;
   protected int r;
   protected int g;
   protected int b;
   protected int a;
   protected int argb;
   protected AABB3D aabb;
   protected final int flowSeed;

   public EmberParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite textureAtlasSprite) {
      super(clientLevel, x, y, z, textureAtlasSprite);
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.tmpPos = new MutableBlockPos();
      this.lastBlockPos = new MutableBlockPos();
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      this.quadSize = 0.14F;
      this.lifetime = Math.randomInt(40) + 20;
      this.aabb = new AABB3D(x - 0.01, y - 0.01, z - 0.01, x + 0.01, y + 0.01, z + 0.01);
      this.gravity = -0.003F;
      this.dampingX = 0.999;
      this.dampingY = 0.98;
      this.dampingZ = 0.999;
      this.flowSeed = mix32(
         Double.hashCode(x)
            ^ Integer.rotateLeft(Double.hashCode(y), 11)
            ^ Integer.rotateLeft(Double.hashCode(z), 22)
            ^ Integer.rotateLeft(Double.hashCode(vx), 7)
            ^ Integer.rotateLeft(Double.hashCode(vy), 17)
            ^ Integer.rotateLeft(Double.hashCode(vz), 27)
      );
      float brightness = Math.random();
      brightness *= brightness * brightness;
      this.setColor(255, 120 + (int)(brightness * 125.0F), (int)(brightness * 255.0F), 255);
      this.move();
   }

   private void move() {
      Vector3d start = this.aabb.start;
      Vector3d end = this.aabb.end;
      start.x = start.x + this.xd;
      start.y = start.y + this.yd;
      start.z = start.z + this.zd;
      end.x = end.x + this.xd;
      end.y = end.y + this.yd;
      end.z = end.z + this.zd;
      this.x = this.x + this.xd;
      this.y = this.y + this.yd;
      this.z = this.z + this.zd;
   }

   private void addTurbulence() {
      double ramp = java.lang.Math.min((double)this.age / 3.0, 1.0);
      ramp = ramp * ramp * (3.0 - 2.0 * ramp);
      int h0 = mix32(this.flowSeed ^ -1640531527);
      int h1 = mix32(h0 ^ 2135587861);
      double TAU = java.lang.Math.PI * 2;
      double K = 0.006135923151542565;
      double p1 = (double)(h0 & 1023) * 0.006135923151542565;
      double p2 = (double)(h0 >>> 10 & 1023) * 0.006135923151542565;
      double p3 = (double)(h0 >>> 20 & 1023) * 0.006135923151542565;
      double p4 = (double)(h1 & 1023) * 0.006135923151542565;
      double p5 = (double)(h1 >>> 10 & 1023) * 0.006135923151542565;
      double p6 = (double)(h1 >>> 20 & 1023) * 0.006135923151542565;
      double freqMul = 0.75 + 0.6 * (double)hash01(this.flowSeed ^ 324508639);
      double strMul = 0.7 + 0.8 * (double)hash01(this.flowSeed ^ 610839776);
      double f = 1.1 * freqMul;
      double t = (double)(this.level.getGameTime() + (long)this.age) * 0.06;
      double x = this.x;
      double y = this.y;
      double z = this.z;
      double f2 = f * 1.13;
      double f3 = f * 0.93;
      double f4 = f * 1.07;
      double f5 = f * 1.11;
      double f6 = f * 0.89;
      double t2 = t * 1.21;
      double t3 = t * 0.97;
      double t4 = t * 1.17;
      double t5 = t * 1.09;
      double t6 = t * 0.91;
      double dFz_dy = java.lang.Math.cos(y * f6 + t6 + p6) * f6;
      double dFy_dz = java.lang.Math.cos(z * f3 + t3 + p3) * f3;
      double cx = dFz_dy - dFy_dz;
      double dFx_dz = java.lang.Math.cos(z * f2 + t2 + p2) * f2;
      double dFz_dx = java.lang.Math.cos(x * f5 + t5 + p5) * f5;
      double cy = dFx_dz - dFz_dx;
      double dFy_dx = java.lang.Math.cos(x * f4 + t4 + p4) * f4;
      double dFx_dy = java.lang.Math.cos(y * f + t + p1) * f;
      double cz = dFy_dx - dFx_dy;
      double gust = 1.0;
      double g = 0.5 + 0.5 * java.lang.Math.sin(t * 0.55 + p3);
      gust = 1.0 + g * g * 0.22;
      g = 0.00825 * strMul * gust * ramp;
      this.xd += cx * g;
      this.yd += cy * g * 0.45;
      this.zd += cz * g;
   }

   public void setColor(int r, int g, int b, int a) {
      this.r = r;
      this.g = g;
      this.b = b;
      this.a = a;
      this.argb = (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
   }

   public Layer getLayer() {
      return Layer.OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else if (!this.removed) {
         this.yd = this.yd - (double)this.gravity;
         this.addTurbulence();
         this.move();
         this.xd = this.xd * this.dampingX;
         this.yd = this.yd * this.dampingY;
         this.zd = this.zd * this.dampingZ;
         boolean requiresCheck = !this.lastBlockPos.equals(this.tmpPos.set(this.x, this.y, this.z));
         if (requiresCheck) {
            BlockHitResult result = this.level
               .clip(
                  new ClipContext(
                     new Vec3(this.lastX, this.lastY, this.lastZ), new Vec3(this.x, this.y, this.z), Block.COLLIDER, Fluid.ANY, CollisionContext.empty()
                  )
               );
            if (result.getType() == Type.BLOCK) {
               boolean checkDynamicBlock = false;
               if (checkDynamicBlock) {
                  BlockState state = this.level.getBlockState(result.getBlockPos());
                  if (VineHelper.getSetting(state) == null) {
                     this.remove();
                  }
               } else {
                  this.remove();
               }
            }

            this.lastX = this.x;
            this.lastY = this.y;
            this.lastZ = this.z;
            this.lastBlockPos.set(this.tmpPos);
         }
      }
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

   private static int mix32(int x) {
      x ^= x >>> 16;
      x *= 2146121005;
      x ^= x >>> 15;
      x *= -2073254261;
      return x ^ x >>> 16;
   }

   private static float hash01(int x) {
      int h = mix32(x);
      return (float)(h >>> 8 & 16777215) * 5.9604645E-8F;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet spriteSet) {
         this.sprite = spriteSet;
      }

      public Particle createParticle(
         SimpleParticleType simpleParticleType, ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, RandomSource random
      ) {
         return new EmberParticle(clientLevel, x, y, z, vx, vy, vz, this.sprite.get(Math.fastRandomSource));
      }
   }
}
