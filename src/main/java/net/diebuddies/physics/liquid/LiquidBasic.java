package net.diebuddies.physics.liquid;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.LiquidRigidBody;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsRenderable;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;
import physx.physics.PxRigidDynamic;

public class LiquidBasic extends Liquid {
   private static final float SPAWN_ANIMATION_TIME = 0.1F;
   public List<LiquidRigidBody> particles = new ObjectArrayList();
   private final MutableBlockPos blockPos = new MutableBlockPos();

   public LiquidBasic(LiquidController controller) {
      super(controller);
   }

   @Override
   public boolean update(PhysicsWorld physicsWorld, double diff) {
      this.removeDead();
      this.controller.update(this, diff);
      return this.particles.isEmpty();
   }

   private void removeDead() {
      for (int i = 0; i < this.particles.size(); i++) {
         if (this.particles.get(i).isDestroyed()) {
            this.particles.remove(i--);
         }
      }
   }

   @Override
   public int particleCount() {
      return this.particles.size();
   }

   @Override
   public void spawnParticle(double x, double y, double z) {
      PhysicsEntity entity = new PhysicsEntity(PhysicsEntity.Type.LIQUID, null);
      entity.getTransformation().translation(x, y, z);
      entity.models = null;
      LiquidRigidBody body = this.world.addLiquidsSphere(entity, ConfigClient.liquidParticleSize * 0.5F);
      body.startLifetime = entity.time;
      ((PxRigidDynamic)body.getRigidBody()).setMaxAngularVelocity((float)Math.toRadians(360.0));
      ((PxRigidDynamic)body.getRigidBody()).setLinearDamping(0.0F);
      ((PxRigidDynamic)body.getRigidBody()).setAngularDamping(0.0F);
      ((PxRigidDynamic)body.getRigidBody()).setMaxDepenetrationVelocity(1.0F);
      this.particles.add(body);
   }

   @Override
   public int fillInstances(PhysicsWorld physicsWorld, long address) {
      Vector3d physicsOffset = physicsWorld.getOffset();

      for (int i = 0; i < this.particles.size(); i++) {
         this.prepareFluidInstances(physicsWorld, physicsWorld.getLevel(), this.particles.get(i), physicsOffset.x, physicsOffset.y, physicsOffset.z, address);
         address += (long)PhysicsShaders.LIQUID_INSTANCE_FORMAT.getVertexSize();
      }

      return this.particles.size();
   }

   private void prepareFluidInstances(PhysicsWorld physics, Level level, LiquidRigidBody body, double ox, double oy, double oz, long address) {
      PhysicsRenderable particle = body.getEntity();
      Vector3f position = particle.position;
      Vector3f positionOld = particle.oldPosition;
      float scale = particle.getDespawnScale(physics.getWorld());
      float spawnAnimation = body.startLifetime - particle.time;
      if (spawnAnimation <= 0.1F) {
         scale = spawnAnimation / 0.1F;
      }

      this.blockPos.set((double)position.x + ox, (double)position.y + oy, (double)position.z + oz);
      int brightness = particle.getLight(physics.getWorld(), this.blockPos);
      float packedLight = (float)(brightness >> 4 & 15 | brightness >> 16 & 240);
      MemoryUtil.memPutFloat(address, positionOld.x);
      MemoryUtil.memPutFloat(address + 4L, positionOld.y);
      MemoryUtil.memPutFloat(address + 8L, positionOld.z);
      MemoryUtil.memPutFloat(address + 12L, packedLight);
      MemoryUtil.memPutFloat(address + 16L, position.x);
      MemoryUtil.memPutFloat(address + 20L, position.y);
      MemoryUtil.memPutFloat(address + 24L, position.z);
      MemoryUtil.memPutFloat(address + 28L, ConfigClient.liquidParticleSize * 1.2F * scale);
   }

   @Override
   public void destroy(PhysicsWorld world) {
      this.removeDead();

      for (int i = 0; i < this.particles.size(); i++) {
         IRigidBody particle = this.particles.get(i);
         world.removeBody(particle);
         if (particle.getLastChunk() != Long.MAX_VALUE && !particle.isKinematicOrFrozen()) {
            world.removeLoadedChunkEntity(particle.getLastChunk());
         }

         world.getDynamicsWorld().removeActor(particle.getRigidBody());
         particle.destroy();
      }

      this.particles.clear();
   }
}
