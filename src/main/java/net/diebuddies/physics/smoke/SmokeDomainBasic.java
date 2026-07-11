package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
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
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
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

public class SmokeDomainBasic extends SmokeDomain {
   private static final double EFFECT_DISTANCE = 0.5;
   private static final double EFFECT_DISTANCE_INV = 2.0;
   private static final double EFFECT_DISTANCE_SQUARED = 0.25;
   private static final double DENSITY_DISTANCE = 1.0;
   private static final double DENSITY_DISTANCE_SQUARED = 1.0;
   private static final double EFFECT_STRENGTH = 2.25;
   private static final float MAX_SPEED = 2.9F;
   private static final float DESPAWN_ANIMATION_TIME = 2.0F;
   private static final float DAMPING_MIN = 0.005F;
   private static final float DAMPING_MAX = 0.04F;
   private static final float GRAVITY_MODIFIER = 0.7F;
   private static final int CHUNK_SIZE = (int)Math.round(Math.ceil(0.5));
   private Animation smokeDespawn;
   private final Map<Vector3i, SmokeDomainBasic.ChunkInfo> chunks;
   private final Object2BooleanMap<Vector3i> masks;
   private final List<SmokeRigidBody> allParticles;
   private final List<SmokeRigidBody> changedParticles;
   private final Vector3i tmp = new Vector3i();
   private final MutableBlockPos blockPos = new MutableBlockPos();
   public int density;
   public Random random;

   public SmokeDomainBasic(PhysicsWorld world) {
      super(world);
      this.chunks = new Object2ObjectOpenHashMap();
      this.masks = new Object2BooleanOpenHashMap();
      this.masks.defaultReturnValue(false);
      this.changedParticles = new ObjectArrayList();
      this.allParticles = new ObjectArrayList();
      this.random = new Random(System.nanoTime());
      this.smokeDespawn = new Animation("smoke_vanish", CurveType.Ease_out, 2.0F);
      this.smokeDespawn.despawnType = AnimationType.Vanish;
   }

   @Override
   public void update(double diff) {
      this.updateParticles(diff);
      super.update(diff);
   }

   private void updateParticles(double diff) {
      Iterator<Entry<Vector3i, SmokeDomainBasic.ChunkInfo>> it = this.chunks.entrySet().iterator();
      this.changedParticles.clear();
      Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
      Vec3 camPos = camera.position();
      double maxSmokeDistance = ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange;
      Vector3d physicsOffset = this.world.getOffset();

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
         Entry<Vector3i, SmokeDomainBasic.ChunkInfo> entry = it.next();
         Vector3i chunk = entry.getKey();
         SmokeDomainBasic.ChunkInfo chunkInfo = entry.getValue();
         List<SmokeRigidBody> particles = chunkInfo.bodies;

         for (int ix = 0; ix < particles.size(); ix++) {
            SmokeRigidBody particle = particles.get(ix);
            if (particle.isDestroyed()) {
               this.allParticles.remove(particle);
               particles.remove(ix--);
            } else {
               Vector3d pos = particle.pos;
               int cx = net.diebuddies.math.Math.fastRound(pos.x) / CHUNK_SIZE;
               int cy = net.diebuddies.math.Math.fastRound(pos.y) / CHUNK_SIZE;
               int cz = net.diebuddies.math.Math.fastRound(pos.z) / CHUNK_SIZE;
               if (cx != chunk.x || cy != chunk.y || cz != chunk.z) {
                  particles.remove(ix--);
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
         SmokeDomainBasic.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomainBasic.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         chunkInfo.bodies.add(particle);
      }

      for (Entry<Vector3i, SmokeDomainBasic.ChunkInfo> entry : this.chunks.entrySet()) {
         Vector3i chunk = entry.getKey();
         SmokeDomainBasic.ChunkInfo info = entry.getValue();

         for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
               for (int z = -1; z <= 1; z++) {
                  if (x != 0 || y != 0 || z != 0) {
                     this.tmp.set(chunk.x + x, chunk.y + y, chunk.z + z);
                     SmokeDomainBasic.ChunkInfo otherInfo = this.chunks.get(this.tmp);
                     if (otherInfo != null && this.masks.getBoolean(this.tmp)) {
                        this.repellParticles(info.bodies, otherInfo.bodies);
                     }
                  }
               }
            }
         }

         this.repellParticles(info.bodies, info.bodies);

         for (int ixx = 0; ixx < info.bodies.size(); ixx++) {
            this.updateParticle(info.bodies.get(ixx), diff);
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
         float damping = particle.damping;
         PxVec3 repellForce = PxVec3.createAt(
            mem,
            MemoryStack::nmalloc,
            -vx * damping - gravity.x * (float)diff * 0.7F + cvx,
            -vy * damping - gravity.y * (float)diff * 0.7F + cvy,
            -vz * damping - gravity.z * (float)diff * 0.7F + cvz
         );
         rigidBody.addForce(repellForce, PxForceModeEnum.eVELOCITY_CHANGE);
      } catch (Throwable var27) {
         if (mem != null) {
            try {
               mem.close();
            } catch (Throwable var26) {
               var27.addSuppressed(var26);
            }
         }

         throw var27;
      }

      if (mem != null) {
         mem.close();
      }

      velocity.set(0.0);
   }

   private void repellParticles(List<SmokeRigidBody> particles, List<SmokeRigidBody> otherParticles) {
      boolean same = particles == otherParticles;

      for (int i = 0; i < particles.size(); i++) {
         SmokeRigidBody particle1 = particles.get(i);
         Vector3d pos1 = particle1.pos;
         if (this.density != -1) {
            particle1.density = (float)this.density;
         }

         for (int j = 0; j < otherParticles.size(); j++) {
            SmokeRigidBody particle2 = otherParticles.get(j);
            if (particle1 != particle2) {
               Vector3d pos2 = particle2.pos;
               double dx = pos1.x - pos2.x;
               double dy = pos1.y - pos2.y;
               double dz = pos1.z - pos2.z;
               double distanceSquared = dx * dx + dy * dy + dz * dz;
               if (distanceSquared < 1.0) {
                  particle1.density++;
                  particle2.density++;
               }

               if (distanceSquared < 0.25) {
                  double length = Math.sqrt(distanceSquared);
                  double effect = 1.0 - length * 2.0;
                  double invLength = 1.0 / length;
                  if (length <= 0.001) {
                     dy = 1.0;
                     effect = 1.0;
                     invLength = 1.0;
                  }

                  dx *= invLength;
                  dy *= invLength;
                  dz *= invLength;
                  double totalStrength = 2.25 * effect;
                  particle1.vel.add(dx * totalStrength, dy * totalStrength, dz * totalStrength);
                  if (!same) {
                     particle2.vel.add(-dx * totalStrength, -dy * totalStrength, -dz * totalStrength);
                  }
               }
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

         PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.SMOKE, null);
         entity.models = null;
         int cx = net.diebuddies.math.Math.fastRound(x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(y) / CHUNK_SIZE;
         int cz = net.diebuddies.math.Math.fastRound(z) / CHUNK_SIZE;
         this.tmp.set(cx, cy, cz);
         SmokeDomainBasic.ChunkInfo chunkInfo = this.chunks.get(this.tmp);
         if (chunkInfo == null) {
            chunkInfo = new SmokeDomainBasic.ChunkInfo();
            this.chunks.put(new Vector3i(this.tmp), chunkInfo);
         }

         entity.getTransformation().translation(x, y, z);
         entity.setAnimation(this.smokeDespawn);
         entity.setColor(SmokeDomain.encodeParticleId(style, this.random.nextInt()));
         SmokeRigidBody body = this.world.addSmokeSphere(entity, 0.15F * scale);
         body.pos.set(x, y, z);
         body.damping = this.random.nextFloat(0.035F) + 0.005F;
         ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity(0.0F);
         body.setGravity(false);
         if (!fadeIn) {
            body.alive = 5.0F;
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
            this.spawnParticle(x, y, z, net.diebuddies.math.Math.random() * 2.5F + 1.0F, false);
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

   private void prepareSmokeInstances(
      PhysicsWorld physics, Level level, Vec3 view, PhysicsRenderable particle, SmokeRigidBody body, double ox, double oy, double oz, long address
   ) {
      Vector3f oldPosition = particle.oldPosition;
      Vector3f position = particle.position;
      this.blockPos.set((double)position.x + ox, (double)position.y + oy, (double)position.z + oz);
      int brightness = particle.getLight(level, this.blockPos);
      int scale = (int)(net.diebuddies.math.Math.remapClamp((double)particle.scale.x, 0.25, 5.0, 0.0, 1.0) * 255.0);
      MemoryUtil.memPutFloat(address, (float)(particle.getBGRA() & 0xFF));
      MemoryUtil.memPutFloat(address + 4L, (float)(particle.getBGRA() >> 8 & 0xFF));
      MemoryUtil.memPutFloat(address + 8L, (float)(brightness >> 4 & 15 | brightness >> 16 & 240));
      MemoryUtil.memPutFloat(address + 12L, (float)(scale & 0xFF));
      double animationScale = (double)particle.getDespawnScale(level);
      MemoryUtil.memPutFloat(address + 16L, oldPosition.x);
      MemoryUtil.memPutFloat(address + 20L, oldPosition.y);
      MemoryUtil.memPutFloat(address + 24L, oldPosition.z);
      MemoryUtil.memPutFloat(address + 28L, Math.min(1.0F, (float)animationScale));
      MemoryUtil.memPutFloat(address + 32L, position.x);
      MemoryUtil.memPutFloat(address + 36L, position.y);
      MemoryUtil.memPutFloat(address + 40L, position.z);
      MemoryUtil.memPutFloat(address + 44L, body.averagedDensity);
   }

   @Override
   public int fillVolume(long address) {
      PhysicsWorld physics = this.getWorld();
      Vector3d physicsOffset = physics.getOffset();
      Level level = this.getWorld().getLevel();
      float renderPercent = (float)physics.getRenderPercent();
      List<SmokeRigidBody> smokeParticles = this.allParticles;
      int count = 0;
      float baseAdd = 51.0F;

      for (int i = 0; i < smokeParticles.size(); i++) {
         SmokeRigidBody body = smokeParticles.get(i);
         if (!body.isDestroyed()) {
            PhysicsRenderable particle = body.getEntity();
            float animationScale = particle.getDespawnScale(level);
            float radiusWorld = Math.min(body.alive * 0.5F, 1.0F) * particle.scale.x;
            Vector3f position = particle.position;
            Vector3f oldPosition = particle.oldPosition;
            this.blockPos.set((double)position.x + physicsOffset.x, (double)position.y + physicsOffset.y, (double)position.z + physicsOffset.z);
            int brightness = particle.getLight(level, this.blockPos);
            MemoryUtil.memPutFloat(address, Mth.lerp(renderPercent, oldPosition.x, position.x));
            MemoryUtil.memPutFloat(address + 4L, Mth.lerp(renderPercent, oldPosition.y, position.y));
            MemoryUtil.memPutFloat(address + 8L, Mth.lerp(renderPercent, oldPosition.z, position.z));
            MemoryUtil.memPutFloat(address + 12L, radiusWorld);
            MemoryUtil.memPutFloat(address + 16L, baseAdd * animationScale);
            MemoryUtil.memPutInt(address + 20L, body.getEntity().getBGRA());
            MemoryUtil.memPutInt(address + 24L, brightness);
            count++;
            address += 32L;
         }
      }

      return count;
   }

   @Override
   public void destroy() {
      for (int i = 0; i < this.allParticles.size(); i++) {
         this.allParticles.get(i).destroy();
      }

      this.allParticles.clear();
   }

   class ChunkInfo {
      public List<SmokeRigidBody> bodies;

      public ChunkInfo() {
         Objects.requireNonNull(SmokeDomainBasic.this);
         super();
         this.bodies = new ObjectArrayList();
      }
   }
}
