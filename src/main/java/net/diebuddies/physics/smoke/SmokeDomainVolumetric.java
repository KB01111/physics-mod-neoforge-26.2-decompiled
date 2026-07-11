package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.diebuddies.bridge.WeatherParticlesRegistry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.weather.WeatherEffects;
import net.diebuddies.mixins.MixinParticleResourcesAccessor;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.SmokeRigidBody;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.AnimationType;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;

public class SmokeDomainVolumetric extends SmokeDomain {
   private static final double EFFECT_DISTANCE = 0.5;
   private static final double EFFECT_DISTANCE_INV = 2.0;
   private static final double EFFECT_DISTANCE_SQUARED = 0.25;
   private static final double DENSITY_DISTANCE = 1.0;
   private static final double DENSITY_DISTANCE_SQUARED = 1.0;
   private static final double EFFECT_STRENGTH = 1.55;
   private static final float MAX_SPEED = 2.9F;
   public static final float DESPAWN_ANIMATION_TIME = 2.0F;
   private static final float DAMPING_MIN = 0.015F;
   private static final float DAMPING_MAX = 0.08F;
   private static final float GRAVITY_MODIFIER = 0.7F;
   private static final int CHUNK_SIZE = (int)Math.round(Math.ceil(0.5));
   private static final float TURB_STRENGTH = 0.05F;
   private static final float TURB_FREQ = 0.95F;
   private static final float TURB_SPEED = 0.175F;
   private static final float BUOYANCY = 0.35F;
   public static final float DEFAULT_SIMULATION_SPEED = 1.0F;
   public static final float MIN_SIMULATION_SPEED = 0.0F;
   public static final float MAX_SIMULATION_SPEED = 4.0F;
   private static final float GUST_STRENGTH = 0.35F;
   private static final double SWIRL_FRACTION = 0.22;
   private static final double RADIAL_FRACTION = 0.75;
   private static final float DENSITY_VARIATION = 0.35F;
   private static final float SIZE_VARIATION = 0.25F;
   private static final float ALIVE_RAMP_TIME = 0.85F;
   private static final float ALIVE_MIN_PAIR_MUL = 0.12F;
   private static final float ALIVE_MIN_SWIRL_MUL = 0.35F;
   private static final float ALIVE_MIN_TURB_MUL = 0.35F;
   private static final float ALIVE_MIN_BUOY_MUL = 0.55F;
   private static final Vector3i[] NEIGHBOURS = new Vector3i[26];
   private Animation smokeDespawn;
   private final Map<Vector3i, SmokeDomainVolumetric.ChunkInfo> chunks;
   private final Object2BooleanMap<Vector3i> masks;
   private final List<SmokeRigidBody> allParticles;
   private final List<SmokeRigidBody> changedParticles;
   private final Vector3i tmp = new Vector3i();
   private final MutableBlockPos blockPos = new MutableBlockPos();
   public Random random;
   private double simTime;

   public SmokeDomainVolumetric(PhysicsWorld world) {
      super(world);
      this.chunks = new Object2ObjectOpenHashMap();
      this.masks = new Object2BooleanOpenHashMap();
      this.masks.defaultReturnValue(false);
      this.changedParticles = new ObjectArrayList();
      this.allParticles = new ObjectArrayList();
      this.random = new Random(System.nanoTime());
      this.smokeDespawn = new Animation("smoke_vanish", CurveType.Ease_out, 2.0F);
      this.smokeDespawn.despawnType = AnimationType.Vanish;
      this.simTime = 0.0;
   }

   @Override
   public void update(double diff) {
      double scaledDiff = diff * 0.2;
      this.simTime += scaledDiff;
      this.emitFireSmoke(scaledDiff);
      this.updateParticles(scaledDiff);
      super.update(scaledDiff);
   }

   private void emitFireSmoke(double diff) {
      boolean spawnSmoke = false;
      if (spawnSmoke) {
         Vec3 view = Minecraft.getInstance().gameRenderer.mainCamera().position();
         double smokeChunkRangeSquared = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange + 256.0;
         double smokeRangeSquared = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
         float fireSmokeChance = Mth.clamp(ConfigClient.smokeVolumeFire, 0.0F, 1.0F);
         if (!(fireSmokeChance <= 0.0F)) {
            ObjectIterator var10 = this.loadedColumns.long2ObjectEntrySet().iterator();

            while (var10.hasNext()) {
               Entry<LongSet> entry = (Entry<LongSet>)var10.next();
               long chunkIndex = entry.getLongKey();
               LongSet fire = (LongSet)entry.getValue();
               if (!fire.isEmpty()) {
                  int chunkX = SectionPos.x(chunkIndex);
                  int chunkZ = SectionPos.z(chunkIndex);
                  double chunkDistSquared = Vector2d.distanceSquared((double)(chunkX * 16 + 8), (double)(chunkZ * 16 + 8), view.x, view.z);
                  if (!(chunkDistSquared > smokeChunkRangeSquared)) {
                     LongIterator fireIt = fire.iterator();

                     while (fireIt.hasNext()) {
                        long fireIndex = fireIt.nextLong();
                        int x = BlockPos.getX(fireIndex) + chunkX * 16;
                        int y = BlockPos.getY(fireIndex);
                        int z = BlockPos.getZ(fireIndex) + chunkZ * 16;
                        double fireDistSquared = Vector3d.distanceSquared((double)x, (double)y, (double)z, view.x, view.y, view.z);
                        if (fireDistSquared < smokeRangeSquared && this.random.nextFloat() < fireSmokeChance) {
                           this.spawnFireSmoke(
                              (double)((float)x + 0.5F) + this.random.nextDouble() * 0.4 - 0.2,
                              (double)((float)y + 0.5F) + this.random.nextDouble() * 0.4 - 0.2,
                              (double)((float)z + 0.5F) + this.random.nextDouble() * 0.4 - 0.2
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void spawnFireSmoke(double x, double y, double z) {
      boolean useEmbers = ConfigClient.smokeEmberParticlesAmount > 0.0F;
      if (useEmbers && this.random.nextFloat() < 0.35F * ConfigClient.smokeEmberParticlesAmount) {
         ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
         Map<Identifier, ParticleProvider<?>> providers = ((MixinParticleResourcesAccessor)Minecraft.getInstance().particleEngine.resourceManager)
            .getParticleProviders();
         ParticleProvider<ParticleOptions> provider = (ParticleProvider<ParticleOptions>)providers.get(WeatherParticlesRegistry.EMBER_RESOURCE);
         particleEngine.add(
            provider.createParticle(
               WeatherEffects.PHYSICS_EMBER,
               this.world.getLevel(),
               x + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
               y + (double)net.diebuddies.math.Math.random() * 0.4,
               z + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
               ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
               0.181,
               ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
               RandomSource.createThreadLocalInstance()
            )
         );
      }

      this.world.queue(() -> this.spawnParticle(x, y, z, 1.0F, true));
   }

   private void updateParticles(double diff) {
      Iterator<java.util.Map.Entry<Vector3i, SmokeDomainVolumetric.ChunkInfo>> it = this.chunks.entrySet().iterator();
      this.changedParticles.clear();
      Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
      Vec3 camPos = camera.position();
      double maxSmokeDistance = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
      Vector3d physicsOffset = this.world.getOffset();
      boolean useEmbers = ConfigClient.smokeEmberParticlesAmount > 0.0F;

      for (int i = 0; i < this.allParticles.size(); i++) {
         SmokeRigidBody body = this.allParticles.get(i);
         if (!body.isDestroyed()) {
            PxTransform transform = body.getRigidBody().getGlobalPose();
            float posX = MemoryUtil.memGetFloat(transform.getAddress() + 16L);
            float posY = MemoryUtil.memGetFloat(transform.getAddress() + 20L);
            float posZ = MemoryUtil.memGetFloat(transform.getAddress() + 24L);
            body.pos.set((double)posX, (double)posY, (double)posZ);
            PhysicsRenderable entity = body.getEntity();
            entity.oldPosition.set(entity.position);
            entity.position.set(posX, posY, posZ);
            SmokeDomain.SmokeParticleStyle style = SmokeDomain.particleStyleFromId(entity.getBGRA());
            if (useEmbers
               && style == SmokeDomain.SmokeParticleStyle.FIRE
               && !body.airDespawn
               && this.random.nextFloat() < 0.05F * ConfigClient.smokeEmberParticlesAmount) {
               ParticleEngine particleEngine = Minecraft.getInstance().particleEngine;
               Map<Identifier, ParticleProvider<?>> providers = ((MixinParticleResourcesAccessor)Minecraft.getInstance().particleEngine.resourceManager)
                  .getParticleProviders();
               ParticleProvider<ParticleOptions> provider = (ParticleProvider<ParticleOptions>)providers.get(WeatherParticlesRegistry.EMBER_RESOURCE);
               particleEngine.add(
                  provider.createParticle(
                     WeatherEffects.PHYSICS_EMBER,
                     this.world.getLevel(),
                     (double)posX + physicsOffset.x + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     (double)posY + physicsOffset.y + (double)net.diebuddies.math.Math.random() * 0.4,
                     (double)posZ + physicsOffset.z + ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     0.181,
                     ((double)net.diebuddies.math.Math.random() - 0.5) * 0.3,
                     RandomSource.createThreadLocalInstance()
                  )
               );
            }

            body.averagedDensity = org.joml.Math.lerp(body.density, body.averagedDensity, 0.94F);
            body.density = 1.0F;
            if (camPos.distanceToSqr(body.pos.x + physicsOffset.x, body.pos.y + physicsOffset.y, body.pos.z + physicsOffset.z) > maxSmokeDistance) {
               this.world.removeBody(body);
               if (body.getLastChunk() != Long.MAX_VALUE && !body.isKinematicOrFrozen()) {
                  this.world.removeLoadedChunkEntity(body.getLastChunk());
               }

               this.world.getDynamicsWorld().removeActor(body.getRigidBody());
               body.destroy();
            }
         }
      }

      while (it.hasNext()) {
         java.util.Map.Entry<Vector3i, SmokeDomainVolumetric.ChunkInfo> entry = it.next();
         Vector3i chunk = entry.getKey();
         SmokeDomainVolumetric.ChunkInfo chunkInfo = entry.getValue();
         List<SmokeRigidBody> particles = chunkInfo.bodies;

         for (int ix = 0; ix < particles.size(); ix++) {
            SmokeRigidBody particle = particles.get(ix);
            if (particle.isDestroyed()) {
               swapRemove(this.allParticles, particle);
               swapRemove(particles, ix--);
            } else {
               Vector3d pos = particle.pos;
               int cx = net.diebuddies.math.Math.fastRound(pos.x) / CHUNK_SIZE;
               int cy = net.diebuddies.math.Math.fastRound(pos.y) / CHUNK_SIZE;
               int cz = net.diebuddies.math.Math.fastRound(pos.z) / CHUNK_SIZE;
               if (cx != chunk.x || cy != chunk.y || cz != chunk.z) {
                  swapRemove(particles, ix--);
                  this.changedParticles.add(particle);
               }
            }
         }

         if (particles.size() == 0) {
            it.remove();
         }
      }

      for (int ixx = 0; ixx < this.changedParticles.size(); ixx++) {
         SmokeRigidBody particle = this.changedParticles.get(ixx);
         Vector3d pos = particle.pos;
         int cx = net.diebuddies.math.Math.fastRound(pos.x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(pos.y) / CHUNK_SIZE;
         int cz = net.diebuddies.math.Math.fastRound(pos.z) / CHUNK_SIZE;
         this.tmp.set(cx, cy, cz);
         SmokeDomainVolumetric.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomainVolumetric.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         chunkInfo.bodies.add(particle);
      }

      for (java.util.Map.Entry<Vector3i, SmokeDomainVolumetric.ChunkInfo> entry : this.chunks.entrySet()) {
         Vector3i chunk = entry.getKey();
         SmokeDomainVolumetric.ChunkInfo info = entry.getValue();

         for (int ixx = 0; ixx < NEIGHBOURS.length; ixx++) {
            Vector3i neighbour = NEIGHBOURS[ixx];
            this.tmp.set(chunk.x + neighbour.x, chunk.y + neighbour.y, chunk.z + neighbour.z);
            SmokeDomainVolumetric.ChunkInfo otherInfo = this.chunks.get(this.tmp);
            if (otherInfo != null && this.masks.getBoolean(this.tmp)) {
               this.repellParticles(info.bodies, otherInfo.bodies);
            }
         }

         this.repellParticlesSame(info.bodies);

         for (int ixxx = 0; ixxx < info.bodies.size(); ixxx++) {
            this.updateParticle(info.bodies.get(ixxx), diff);
         }

         this.masks.put(chunk, true);
      }

      this.masks.clear();
   }

   private void updateParticle(SmokeRigidBody particle, double diff) {
      PhysicsRenderable entity = particle.getEntity();
      Vector3d pos = particle.pos;
      double x = pos.x;
      double y = pos.y;
      double z = pos.z;
      Vector3d offset = this.world.getOffset();
      if (!particle.airDespawn && isInOpenAir(this.world.getLevel(), Mth.floor(x + offset.x), Mth.floor(y + offset.y), Mth.floor(z + offset.z))) {
         particle.startDespawn(2.0F);
      }

      this.addTurbulence(particle, diff);
      PxRigidBody rigidBody = (PxRigidBody)particle.getRigidBody();
      Vector3f gravity = this.world.getDynamicsWorld().getGravity();
      Vector3d velocity = particle.vel;
      MemoryStack mem = MemoryStack.stackPush();

      try {
         PxVec3 v = rigidBody.getLinearVelocity();
         float vx = MemoryUtil.memGetFloat(v.getAddress());
         float vy = MemoryUtil.memGetFloat(v.getAddress() + 4L);
         float vz = MemoryUtil.memGetFloat(v.getAddress() + 8L);
         float cvx = (float)net.diebuddies.math.Math.clamp(velocity.x, -2.9F, 2.9F);
         float cvy = (float)net.diebuddies.math.Math.clamp(velocity.y, -2.9F, 2.9F);
         float cvz = (float)net.diebuddies.math.Math.clamp(velocity.z, -2.9F, 2.9F);
         int seed = entity.getBGRA();
         float turbMul = 0.6F + 0.9F * hash01(seed ^ -1582119980);
         float damping = particle.damping * Mth.clamp(1.15F - 0.25F * turbMul, 0.65F, 1.15F);
         PxVec3 repellForce = PxVec3.createAt(
            mem,
            MemoryStack::nmalloc,
            -vx * damping - gravity.x * (float)diff * 0.7F + cvx,
            -vy * damping - gravity.y * (float)diff * 0.7F + cvy,
            -vz * damping - gravity.z * (float)diff * 0.7F + cvz
         );
         rigidBody.addForce(repellForce, PxForceModeEnum.eVELOCITY_CHANGE);
      } catch (Throwable var29) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var28) {
               var29.addSuppressed(var28);
            }
         }

         throw var29;
      }

      if (mem != null) {
         mem.close();
      }

      velocity.set(0.0);
   }

   private void addTurbulence(SmokeRigidBody particle, double diff) {
      PhysicsRenderable entity = particle.getEntity();
      int seed = entity.getBGRA();
      float a01 = alive01(particle);
      float turbRamp = rampMul(0.35F, a01);
      float buoyRamp = rampMul(0.55F, a01);
      int h0 = mix32(seed ^ -1640531527);
      int h1 = mix32(h0 ^ 2135587861);
      double TAU = Math.PI * 2;
      double K = 0.006135923151542565;
      double p1 = (double)(h0 & 1023) * 0.006135923151542565;
      double p2 = (double)(h0 >>> 10 & 1023) * 0.006135923151542565;
      double p3 = (double)(h0 >>> 20 & 1023) * 0.006135923151542565;
      double p4 = (double)(h1 & 1023) * 0.006135923151542565;
      double p5 = (double)(h1 >>> 10 & 1023) * 0.006135923151542565;
      double p6 = (double)(h1 >>> 20 & 1023) * 0.006135923151542565;
      float freqMul = 0.65F + 0.75F * hash01(seed ^ 324508639);
      float strMul = 0.6F + 1.0F * hash01(seed ^ 610839776);
      double f = (double)(0.95F * freqMul);
      double t = this.simTime * 0.175F;
      Vector3d pos = particle.pos;
      double x = pos.x;
      double y = pos.y;
      double z = pos.z;
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
      double dFz_dy = Math.cos(y * f6 + t6 + p6) * f6;
      double dFy_dz = Math.cos(z * f3 + t3 + p3) * f3;
      double cx = dFz_dy - dFy_dz;
      double dFx_dz = Math.cos(z * f2 + t2 + p2) * f2;
      double dFz_dx = Math.cos(x * f5 + t5 + p5) * f5;
      double cy = dFx_dz - dFz_dx;
      double dFy_dx = Math.cos(x * f4 + t4 + p4) * f4;
      double dFx_dy = Math.cos(y * f + t + p1) * f;
      double cz = dFy_dx - dFx_dy;
      double gust = 1.0;
      double g = 0.5 + 0.5 * Math.sin(t * 0.55 + p3);
      gust = 1.0 + g * g * 0.35F;
      g = (double)(0.05F * strMul) * gust * (double)turbRamp;
      float crowd = Mth.clamp((particle.averagedDensity - 1.0F) / 6.0F, 0.0F, 1.0F);
      double up = (double)(0.35F * buoyRamp) * (0.7 + 0.9 * (double)crowd) * (0.75 + 0.5 * (double)hash01(seed ^ -889275714));
      particle.vel.add(cx * g, cy * g + up, cz * g);
   }

   private void repellParticles(List<SmokeRigidBody> particles, List<SmokeRigidBody> otherParticles) {
      for (int i = 0; i < particles.size(); i++) {
         SmokeRigidBody particle1 = particles.get(i);
         Vector3d pos1 = particle1.pos;
         float a1 = alive01(particle1);
         float p1RadMul = rampMul(0.12F, a1);
         float p1SwirlMul = rampMul(0.35F, a1);

         for (int j = 0; j < otherParticles.size(); j++) {
            SmokeRigidBody particle2 = otherParticles.get(j);
            if (particle1 != particle2) {
               Vector3d pos2 = particle2.pos;
               double dx = pos1.x - pos2.x;
               double dy = pos1.y - pos2.y;
               double dz = pos1.z - pos2.z;
               double distanceSquared = dx * dx + dy * dy + dz * dz;
               if (!(distanceSquared >= 1.0)) {
                  particle1.density++;
                  particle2.density++;
                  if (!(distanceSquared >= 0.25)) {
                     double length = Math.sqrt(distanceSquared);
                     double x = length * 2.0;
                     double effect = 1.0 - x * x;
                     double invLength = 1.0 / length;
                     if (length <= 0.001) {
                        dy = 1.0;
                        effect = 1.0;
                        invLength = 1.0;
                     }

                     dx *= invLength;
                     dy *= invLength;
                     dz *= invLength;
                     double totalStrength = 1.55 * effect;
                     float a2 = alive01(particle2);
                     float p2RadMul = rampMul(0.12F, a2);
                     float p2SwirlMul = rampMul(0.35F, a2);
                     double radialBase = totalStrength * 0.75;
                     double radial1 = radialBase * (double)p1RadMul;
                     particle1.vel.add(dx * radial1, dy * radial1, dz * radial1);
                     double radial2 = radialBase * (double)p2RadMul;
                     particle2.vel.add(-dx * radial2, -dy * radial2, -dz * radial2);
                     int s = ((particle1.getEntity().getBGRA() ^ particle2.getEntity().getBGRA()) & 1) == 0 ? 1 : -1;
                     double swirlBase = totalStrength * 0.22 * (double)s;
                     double tz = -dx;
                     double swirl1 = swirlBase * (double)p1SwirlMul;
                     particle1.vel.add(dz * swirl1, 0.0, tz * swirl1);
                     double swirl2 = swirlBase * (double)p2SwirlMul;
                     particle2.vel.add(-dz * swirl2, 0.0, -tz * swirl2);
                  }
               }
            }
         }
      }
   }

   private void repellParticlesSame(List<SmokeRigidBody> particles) {
      for (int i = 0; i < particles.size(); i++) {
         SmokeRigidBody p1 = particles.get(i);
         Vector3d pos1 = p1.pos;
         float a1 = alive01(p1);
         float p1RadMul = rampMul(0.12F, a1);
         float p1SwirlMul = rampMul(0.35F, a1);

         for (int j = i + 1; j < particles.size(); j++) {
            SmokeRigidBody p2 = particles.get(j);
            Vector3d pos2 = p2.pos;
            double dx = pos1.x - pos2.x;
            double dy = pos1.y - pos2.y;
            double dz = pos1.z - pos2.z;
            double d2 = dx * dx + dy * dy + dz * dz;
            if (d2 < 1.0) {
               p1.density += 2.0F;
               p2.density += 2.0F;
            }

            if (d2 < 0.25) {
               double len = Math.sqrt(d2);
               double effect;
               double invLen;
               if (len <= 0.001) {
                  dx = 0.0;
                  dy = 1.0;
                  dz = 0.0;
                  effect = 1.0;
                  invLen = 1.0;
               } else {
                  double x = len * 2.0;
                  effect = 1.0 - x * x;
                  invLen = 1.0 / len;
               }

               dx *= invLen;
               dy *= invLen;
               dz *= invLen;
               double totalStrength = 1.55 * effect;
               float a2 = alive01(p2);
               float p2RadMul = rampMul(0.12F, a2);
               float p2SwirlMul = rampMul(0.35F, a2);
               double radialBase = totalStrength * 0.75;
               double radial1 = radialBase * (double)p1RadMul;
               double radial2 = radialBase * (double)p2RadMul;
               p1.vel.add(dx * radial1, dy * radial1, dz * radial1);
               p2.vel.add(-dx * radial2, -dy * radial2, -dz * radial2);
               int s = ((p1.getEntity().getBGRA() ^ p2.getEntity().getBGRA()) & 1) == 0 ? 1 : -1;
               double swirlBase = totalStrength * 0.22 * (double)s;
               double tz = -dx;
               double swirl1 = swirlBase * (double)p1SwirlMul;
               double swirl2 = swirlBase * (double)p2SwirlMul;
               p1.vel.add(dz * swirl1, 0.0, tz * swirl1);
               p2.vel.add(-dz * swirl2, 0.0, -tz * swirl2);
            }
         }
      }
   }

   @Override
   public void clearParticles() {
      for (int i = 0; i < this.allParticles.size(); i++) {
         this.allParticles.get(i).getEntity().time = -1.0F;
      }
   }

   @Override
   public void spawnParticle(double x, double y, double z, float scale, boolean fadeIn, SmokeDomain.SmokeParticleStyle style) {
      if (ConfigClient.smokePhysics) {
         if (this.allParticles.size() > ConfigClient.smokeParticleLimit) {
            for (int i = 0; i < this.allParticles.size(); i++) {
               IRigidBody body = this.allParticles.get(i);
               if (!body.isDestroyed() && (double)body.getEntity().time >= 0.0) {
                  body.getEntity().time = -1.0F;
                  break;
               }
            }
         }

         PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.SMOKE_VOLUMETRIC, null);
         entity.models = null;
         int cx = net.diebuddies.math.Math.fastRound(x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(y) / CHUNK_SIZE;
         int cz = net.diebuddies.math.Math.fastRound(z) / CHUNK_SIZE;
         this.tmp.set(cx, cy, cz);
         SmokeDomainVolumetric.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomainVolumetric.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         entity.getTransformation().translation(x, y, z);
         entity.setAnimation(this.smokeDespawn);
         entity.setColor(SmokeDomain.encodeParticleId(style, this.random.nextInt()));
         float sizeMul = 1.0F;
         sizeMul = 1.0F + (this.random.nextFloat() * 2.0F - 1.0F) * 0.25F;
         sizeMul = Mth.clamp(sizeMul, 0.75F, 1.35F);
         SmokeRigidBody body = this.world.addSmokeSphere(entity, 0.15F * scale * sizeMul);
         body.pos.set(x, y, z);
         body.damping = this.random.nextFloat(0.065F) + 0.015F;
         ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity(0.0F);
         body.setGravity(false);
         body.smokeSuppression = 56.1F;
         if (style == SmokeDomain.SmokeParticleStyle.STEAM) {
            this.world.queue(() -> body.vel.set(0.0, 100.0, 0.0));
         }

         if (!fadeIn) {
            body.alive = 5.0F;
            body.smokeSuppression = 112.2F;
            body.getEntity().scale.x *= 3.0F;
         }

         chunkInfo.bodies.add(body);
         this.allParticles.add(body);
      }
   }

   @Override
   public void executeExplosion(Explosion explosion) {
      MutableBlockPos blockPos = new MutableBlockPos();

      for (int i = 0; i < 300; i++) {
         double x = (double)net.diebuddies.math.Math.random() - 0.5;
         double y = (double)net.diebuddies.math.Math.random() - 0.5;
         double z = (double)net.diebuddies.math.Math.random() - 0.5;

         double vectorLength;
         for (vectorLength = Math.sqrt(x * x + y * y + z * z); vectorLength == 0.0; vectorLength = Math.sqrt(x * x + y * y + z * z)) {
            x = (double)net.diebuddies.math.Math.random() - 0.5;
            y = (double)net.diebuddies.math.Math.random() - 0.5;
            z = (double)net.diebuddies.math.Math.random() - 0.5;
         }

         x /= vectorLength;
         y /= vectorLength;
         z /= vectorLength;
         double length = (double)net.diebuddies.math.Math.random() * Math.max(1.0, (double)explosion.strength);
         x = x * length + explosion.position.x;
         y = y * length + explosion.position.y;
         z = z * length + explosion.position.z;
         blockPos.set(x, y, z);
         Level level = this.world.getLevel();
         BlockState state = level.getBlockState(blockPos);
         FluidState fluidState = state.getFluidState();
         if (fluidState.getAmount() == 0 && (!Block.isShapeFullBlock(state.getShape(level, blockPos)) || state.getCollisionShape(level, blockPos).isEmpty())) {
            this.spawnParticle(x, y, z, net.diebuddies.math.Math.random() * 4.5F + 1.0F, false);
         }
      }
   }

   @Override
   public int particleCount() {
      return this.allParticles.size();
   }

   @Override
   public int fillInstances(Vec3 cameraPos, long address) {
      PhysicsWorld physics = this.getWorld();
      Vector3d physicsOffset = physics.getOffset();
      List<SmokeRigidBody> smokeParticles = this.allParticles;
      int count = 0;

      for (int i = 0; i < smokeParticles.size(); i++) {
         SmokeRigidBody body = smokeParticles.get(i);
         if (!body.isDestroyed()) {
            this.prepareSmokeInstances(
               physics, physics.getLevel(), cameraPos, body.getEntity(), body, physicsOffset.x, physicsOffset.y, physicsOffset.z, address
            );
            address += (long)PhysicsShaders.SMOKE_INSTANCE_FORMAT.getVertexSize();
            count++;
         }
      }

      return count;
   }

   @Override
   public int fillVolume(long address) {
      PhysicsWorld physics = this.getWorld();
      Vector3d physicsOffset = physics.getOffset();
      Level level = this.getWorld().getLevel();
      float renderPercent = (float)physics.getRenderPercent();
      List<SmokeRigidBody> smokeParticles = this.allParticles;
      int count = 0;

      for (int i = 0; i < smokeParticles.size(); i++) {
         SmokeRigidBody body = smokeParticles.get(i);
         if (!body.isDestroyed()) {
            PhysicsRenderable particle = body.getEntity();
            float animationScale = particle.getDespawnScale(level);
            float particleResize = net.diebuddies.math.Math.remap(animationScale, 0.0F, 1.0F, 2.0F, 1.0F);
            int seed = particle.getBGRA();
            float r0 = hash01(seed ^ 1374496523);
            float crowd = Mth.clamp((body.averagedDensity - 1.0F) / 6.0F, 0.0F, 1.0F);
            float radiusWorld = Math.min(body.alive * 1.5F, 1.0F) * particle.scale.x * ConfigClient.smokeVolumeRadius * particleResize;
            radiusWorld *= 0.95F + 0.1F * crowd;
            Vector3f position = particle.position;
            Vector3f oldPosition = particle.oldPosition;
            this.blockPos.set((double)position.x + physicsOffset.x, (double)position.y + physicsOffset.y, (double)position.z + physicsOffset.z);
            int brightness = particle.getLight(level, this.blockPos);
            float var = 1.0F;
            var = 1.0F + (r0 * 2.0F - 1.0F) * 0.35F;
            var = Mth.clamp(var, 0.65F, 1.55F);
            float crowdMul = 0.9F + 0.55F * crowd;
            float addScale = body.smokeSuppression * animationScale * var * crowdMul * ConfigClient.smokeDensity;
            MemoryUtil.memPutFloat(address, Mth.lerp(renderPercent, oldPosition.x, position.x));
            MemoryUtil.memPutFloat(address + 4L, Mth.lerp(renderPercent, oldPosition.y, position.y));
            MemoryUtil.memPutFloat(address + 8L, Mth.lerp(renderPercent, oldPosition.z, position.z));
            MemoryUtil.memPutFloat(address + 12L, radiusWorld);
            MemoryUtil.memPutFloat(address + 16L, addScale);
            MemoryUtil.memPutInt(address + 20L, body.getEntity().getBGRA());
            MemoryUtil.memPutInt(address + 24L, brightness);
            count++;
            address += 32L;
         }
      }

      return count;
   }

   private void prepareSmokeInstances(
      PhysicsWorld physics, Level level, Vec3 view, PhysicsRenderable particle, SmokeRigidBody body, double ox, double oy, double oz, long address
   ) {
      Vector3f oldPosition = particle.oldPosition;
      Vector3f position = particle.position;
      this.blockPos.set((double)position.x + ox, (double)position.y + oy, (double)position.z + oz);
      int brightness = particle.getLight(level, this.blockPos);
      float animationScale = particle.getDespawnScale(level);
      float particleResize = net.diebuddies.math.Math.remap(animationScale, 0.0F, 1.0F, 2.0F, 1.0F);
      float radiusWorld = Math.min(body.alive * 1.5F, 1.0F) * particle.scale.x * ConfigClient.smokeVolumeRadius * particleResize;
      int scale = (int)(net.diebuddies.math.Math.remapClamp((double)radiusWorld, 0.25, 5.0, 0.0, 1.0) * 255.0);
      int seed = particle.getBGRA();
      float r0 = hash01(seed ^ 1374496523);
      float crowd = Mth.clamp((body.averagedDensity - 1.0F) / 6.0F, 0.0F, 1.0F);
      radiusWorld *= 0.95F + 0.1F * crowd;
      float var = 1.0F;
      var = 1.0F + (r0 * 2.0F - 1.0F) * 0.35F;
      var = Mth.clamp(var, 0.65F, 1.55F);
      float crowdMul = 0.9F + 0.55F * crowd;
      float transparency = animationScale * var * crowdMul * ConfigClient.smokeDensity;
      MemoryUtil.memPutFloat(address, (float)(particle.getBGRA() & 0xFF));
      MemoryUtil.memPutFloat(address + 4L, (float)(particle.getBGRA() >> 8 & 0xFF));
      MemoryUtil.memPutFloat(address + 8L, (float)(brightness >> 4 & 15 | brightness >> 16 & 240));
      MemoryUtil.memPutFloat(address + 12L, (float)(scale & 0xFF));
      MemoryUtil.memPutFloat(address + 16L, oldPosition.x);
      MemoryUtil.memPutFloat(address + 20L, oldPosition.y);
      MemoryUtil.memPutFloat(address + 24L, oldPosition.z);
      MemoryUtil.memPutFloat(address + 28L, Math.min(1.0F, transparency));
      MemoryUtil.memPutFloat(address + 32L, position.x);
      MemoryUtil.memPutFloat(address + 36L, position.y);
      MemoryUtil.memPutFloat(address + 40L, position.z);
      MemoryUtil.memPutFloat(address + 44L, body.averagedDensity);
   }

   @Override
   public void destroy() {
      for (int i = 0; i < this.allParticles.size(); i++) {
         this.allParticles.get(i).destroy();
      }

      this.allParticles.clear();
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

   private static float alive01(SmokeRigidBody p) {
      float a = p.alive;
      a /= Math.max(0.85F, 1.0E-6F);
      a = Mth.clamp(a, 0.0F, 1.0F);
      return a * a * (3.0F - 2.0F * a);
   }

   private static float rampMul(float minMul, float a01) {
      return minMul + (1.0F - minMul) * a01;
   }

   private static <T> void swapRemove(List<T> list, int index) {
      int last = list.size() - 1;
      if (index != last) {
         list.set(index, list.get(last));
      }

      list.remove(last);
   }

   private static <T> boolean swapRemove(List<T> list, T obj) {
      int index = list.indexOf(obj);
      if (index < 0) {
         return false;
      } else {
         swapRemove(list, index);
         return true;
      }
   }

   static {
      int idx = 0;

      for (int x = -1; x <= 1; x++) {
         for (int y = -1; y <= 1; y++) {
            for (int z = -1; z <= 1; z++) {
               if (x != 0 || y != 0 || z != 0) {
                  NEIGHBOURS[idx++] = new Vector3i(x, y, z);
               }
            }
         }
      }
   }

   class ChunkInfo {
      public List<SmokeRigidBody> bodies;

      public ChunkInfo() {
         Objects.requireNonNull(SmokeDomainVolumetric.this);
         super();
         this.bodies = new ObjectArrayList();
      }
   }
}
