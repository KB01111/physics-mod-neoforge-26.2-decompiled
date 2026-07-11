package net.diebuddies.mixins;

import java.util.Set;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.Explosion;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPacketListener.class})
public class MixinClientPacketListener {
   @Shadow
   @Final
   private RandomSource random;
   @Shadow
   @Final
   private ClientLevel level;

   @Inject(
      at = {@At("HEAD")},
      method = {"handleExplosion"}
   )
   private void physicsmod$handleExplosion(ClientboundExplodePacket explosionPacket, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(explosionPacket, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());
      Explosion explosion = new Explosion();
      explosion.strength = explosionPacket.radius();
      explosion.position = new Vector3d(explosionPacket.center().x, explosionPacket.center().y, explosionPacket.center().z);
      PhysicsMod mod = PhysicsMod.getInstance(this.level);
      mod.explosions.add(explosion);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleRemoveEntities"}
   )
   private void physicsmod$handleRemoveEntities(ClientboundRemoveEntitiesPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());

      for (int i = 0; i < packet.getEntityIds().size(); i++) {
         int j = packet.getEntityIds().getInt(i);
         Entity entity = this.level.getEntity(j);
         if (entity != null
            && (ConfigClient.pvpServerCompatibility || entity instanceof EnderDragon || entity instanceof Creeper)
            && entity.position().distanceTo(Minecraft.getInstance().player.position()) < 40.0
            && entity instanceof LivingEntity livingEntity) {
            PhysicsMod.blockifyEntity(this.level, livingEntity);
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"handleRespawn"}
   )
   private void physicsmod$handleRespawn(ClientboundRespawnPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());
      PhysicsMod.destroyNextTick = true;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleTeleportEntity"}
   )
   private void physicsmod$handleTeleportEntity(ClientboundTeleportEntityPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());
      Entity entity = this.level.getEntity(packet.id());
      if (entity == Minecraft.getInstance().player && !entity.isPassenger()) {
         this.physicsmod$shouldDestroyWorldsDueToTeleport(entity, packet.change(), packet.relatives());
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleMovePlayer"}
   )
   private void physicsmod$handleTeleportEntity(ClientboundPlayerPositionPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());
      Player player = Minecraft.getInstance().player;
      if (!player.isPassenger()) {
         this.physicsmod$shouldDestroyWorldsDueToTeleport(player, packet.change(), packet.relatives());
      }
   }

   @Unique
   private void physicsmod$shouldDestroyWorldsDueToTeleport(Entity entity, PositionMoveRotation positionMoveRotation, Set<Relative> set) {
      PositionMoveRotation positionMoveRotation2 = PositionMoveRotation.of(entity);
      PositionMoveRotation positionMoveRotation3 = PositionMoveRotation.calculateAbsolute(positionMoveRotation2, positionMoveRotation, set);
      boolean moved64Blocks = positionMoveRotation2.position().distanceToSqr(positionMoveRotation3.position()) > 4096.0;
      if (moved64Blocks) {
         PhysicsMod.destroyNextTick = true;
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleParticleEvent"},
      cancellable = true
   )
   private void physicsmod$handleParticleEvent(ClientboundLevelParticlesPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());

      try {
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         if (ConfigClient.serverBlockPhysicsParticles
            && packet.getParticle() instanceof BlockParticleOption blockParticles
            && camera.isInitialized()
            && camera.position().distanceToSqr(packet.getX(), packet.getY(), packet.getZ()) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            BlockState state = blockParticles.getState();
            if (packet.getCount() == 0) {
               double vx = (double)(packet.getMaxSpeed() * packet.getXDist());
               double vy = (double)(packet.getMaxSpeed() * packet.getYDist());
               double vz = (double)(packet.getMaxSpeed() * packet.getZDist());
               ParticleSpawner.spawnServerBlockPhysicsParticle(
                  state,
                  this.level,
                  packet.getX() + (double)(Math.random() * 0.1F) - 0.05F,
                  packet.getY() + (double)(Math.random() * 0.1F) - 0.05F,
                  packet.getZ() + (double)(Math.random() * 0.1F) - 0.05F,
                  vx,
                  vy,
                  vz
               );
            } else {
               for (int i = 0; i < java.lang.Math.max(packet.getCount() / 3, 1); i++) {
                  double x = this.random.nextGaussian() * (double)packet.getXDist() + (double)(Math.random() * 0.1F) - 0.05F;
                  double y = this.random.nextGaussian() * (double)packet.getYDist() + (double)(Math.random() * 0.1F) - 0.05F;
                  double z = this.random.nextGaussian() * (double)packet.getZDist() + (double)(Math.random() * 0.1F) - 0.05F;
                  double vx = this.random.nextGaussian() * (double)packet.getMaxSpeed();
                  double vy = this.random.nextGaussian() * (double)packet.getMaxSpeed();
                  double vz = this.random.nextGaussian() * (double)packet.getMaxSpeed();
                  ParticleSpawner.spawnServerBlockPhysicsParticle(state, this.level, packet.getX() + x, packet.getY() + y, packet.getZ() + z, vx, vy, vz);
               }
            }

            info.cancel();
         }
      } catch (Exception var19) {
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"handleBlockEntityData"}
   )
   private void physicsmod$handleBlockEntityData(ClientboundBlockEntityDataPacket packet, CallbackInfo info) {
      PacketUtils.ensureRunningOnSameThread(packet, (ClientPacketListener)(Object)this, Minecraft.getInstance().packetProcessor());
      BlockPos blockPos = packet.getPos();
      PhysicsMod.getInstance(this.level).blockUpdates.add(blockPos);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleLogin"}
   )
   private void physicsmod$handleLogin(ClientboundLoginPacket packet, CallbackInfo info) {
      boolean changed = false;

      for (ResourceKey<Level> entry : packet.levels()) {
         changed |= ConfigClient.addGravityBuoyancyEntry(entry.identifier());
      }

      if (changed) {
         ConfigClient.save();
      }
   }
}
