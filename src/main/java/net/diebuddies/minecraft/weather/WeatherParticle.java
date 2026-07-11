package net.diebuddies.minecraft.weather;

import net.diebuddies.math.Math;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.vines.VineHelper;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SingleQuadParticle.Layer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;

public abstract class WeatherParticle extends SingleQuadParticle {
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

   public WeatherParticle(ClientLevel clientLevel, double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite textureAtlasSprite) {
      super(clientLevel, x, y, z, textureAtlasSprite);
      this.lastX = x;
      this.lastY = y;
      this.lastZ = z;
      this.tmpPos = new MutableBlockPos();
      this.lastBlockPos = new MutableBlockPos();
      this.gravity = 0.06F;
      this.xd = vx;
      this.yd = vy;
      this.zd = vz;
      this.quadSize = 0.14F;
      this.lifetime = Math.randomInt(200) + 200;
      this.aabb = new AABB3D(x - 0.01, y - 0.01, z - 0.01, x + 0.01, y + 0.01, z + 0.01);
      this.move();
      this.calculateLight();
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

   public int getLightCoords(float renderPercent) {
      return this.cachedBrightness;
   }

   private void calculateLight() {
      this.tmpPos.set(this.xo, this.yo, this.zo);
      if (WeatherEffects.invalidateLight || this.level.hasChunkAt(this.tmpPos)) {
         this.cachedBrightness = LightCoordsUtil.getLightCoords(this.level, this.tmpPos);
      }
   }

   public void tick() {
      WeatherEffects.aliveParticles++;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else if (!this.removed) {
         this.yd = this.yd - (double)this.gravity;
         this.move();
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         Vec3 cameraPos = camera.position();
         float currentX = (float)(this.x - cameraPos.x());
         float currentY = (float)(this.y - cameraPos.y());
         float currentZ = (float)(this.z - cameraPos.z());
         if ((double)(currentX * currentX + currentY * currentY + currentZ * currentZ) > 196.0) {
            this.remove();
         } else {
            this.xd = this.xd * this.dampingX;
            this.yd = this.yd * this.dampingY;
            this.zd = this.zd * this.dampingZ;
            boolean requiresCheck = !this.lastBlockPos.equals(this.tmpPos.set(this.x, this.y, this.z));
            if (requiresCheck) {
               this.calculateLight();
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
   }
}
