package net.diebuddies.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.minecraft.PlayerPhysicsHealth;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.diebuddies.physics.vines.DynamicLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GameRenderer.class})
public class MixinGameRenderer {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      at = {@At("RETURN")},
      method = {"tick"}
   )
   public void physicsmod$destroyOnTeleport(CallbackInfo info) {
      if (PhysicsMod.destroyNextTick) {
         ObjectIterator var2 = PhysicsMod.getInstances().values().iterator();

         while (var2.hasNext()) {
            PhysicsMod mod = (PhysicsMod)var2.next();
            mod.getPhysicsWorld().destroy();
         }

         PhysicsMod.getInstances().clear();
         PhysicsMod.destroyNextTick = false;
      }
   }

   @WrapOperation(
      method = {"renderLevel"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
      )}
   )
   private GpuBufferSlice physicsmod$captureLevelProjectionMatrix(ProjectionMatrixBuffer buffer, Matrix4f projectionMatrix, Operation<GpuBufferSlice> original) {
      ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).physicsmod$getMainRenderer().storeProjectionMatrix(projectionMatrix);
      return (GpuBufferSlice)original.call(new Object[]{buffer, projectionMatrix});
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"render"}
   )
   public void physicsmod$renderUpdate(DeltaTracker deltaTracker, boolean render, CallbackInfo info) {
      Iterator<Entry<ClientLevel, PhysicsMod>> it = PhysicsMod.getInstances().entrySet().iterator();

      while (it.hasNext()) {
         Entry<ClientLevel, PhysicsMod> entry = it.next();
         PhysicsMod mod = entry.getValue();

         while (!mod.sodiumRemoveRagdolls.isEmpty()) {
            Ragdoll ragdoll = mod.sodiumRemoveRagdolls.poll();
            mod.physicsWorld.removeRagdoll(ragdoll);
         }
      }

      LocalPlayer clientPlayer = this.minecraft.player;
      long currentTime = Util.getNanos();
      it = PhysicsMod.getInstances().entrySet().iterator();
      PhysicsMod.calculatePlaybackSpeed();

      while (it.hasNext()) {
         Entry<ClientLevel, PhysicsMod> entry = it.next();
         ClientLevel level = entry.getKey();
         PhysicsMod mod = entry.getValue();
         ((DynamicLoader)level.getChunkSource()).setPhysicsMod(mod);
         if ((double)(currentTime - mod.time) / 1000000.0 > 4000.0) {
            mod.time = currentTime;
         }

         double diff = PhysicsMod.getPlaybackSpeed((double)(currentTime - mod.time) / 1.0E9);
         PhysicsWorld physics = mod.getPhysicsWorld();
         if (!physics.isActive()) {
            physics.destroy();
            it.remove();
         } else {
            physics.update(diff);
            mod.time = currentTime;

            for (AbstractClientPlayer player : new ObjectArrayList<>(level.players())) {
               PlayerPhysicsHealth health = (PlayerPhysicsHealth)player;
               if (health.getPhysicsHealth() > 0.0F && player.getHealth() <= 0.0F && ConfigMobs.getMobSetting(player).getType() != MobPhysicsType.OFF) {
                  PhysicsMod.blockifyEntity(level, player);
               }

               health.setPhysicsHealth(player.getHealth());
            }
         }
      }
   }
}
