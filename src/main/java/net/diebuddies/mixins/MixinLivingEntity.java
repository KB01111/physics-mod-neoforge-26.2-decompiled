package net.diebuddies.mixins;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.math.Math;
import net.diebuddies.minecraft.ParticleSpawner;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LivingEntity.class})
public class MixinLivingEntity {
   @Shadow
   @Final
   protected boolean dead;

   @Inject(
      at = {@At("HEAD")},
      method = {"die"}
   )
   public void onDeath(DamageSource source, CallbackInfo info) {
      LivingEntity entity = (LivingEntity)(Object)this;
      MobPhysicsType mobType = ConfigMobs.getMobSetting(entity).getType();
      if (mobType != MobPhysicsType.OFF && entity.level() instanceof ClientLevel clientLevel) {
         PhysicsMod.blockifyEntity(clientLevel, entity);
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"spawnItemParticles"},
      cancellable = true
   )
   private void spawnItemParticles(ItemStack itemStack, int amount, CallbackInfo info) {
      LivingEntity ths = (LivingEntity)(Object)this;
      if (ConfigClient.eatingPhysicsParticles && ths.level() instanceof ClientLevel clientLevel) {
         Camera camera = Minecraft.getInstance().gameRenderer.mainCamera();
         if (camera.isInitialized()
            && camera.position().distanceToSqr(ths.getX(), ths.getEyeY(), ths.getZ()) < ConfigClient.blockPhysicsRange * ConfigClient.blockPhysicsRange) {
            for (int j = 0; j < amount * 2; j++) {
               Vec3 vel = new Vec3(((double)Math.random() - 0.5) * 0.1, (double)Math.random() * 0.1 + 0.1, 0.0);
               vel = vel.xRot(-ths.getXRot() * (float) (java.lang.Math.PI / 180.0));
               vel = vel.yRot(-ths.getYRot() * (float) (java.lang.Math.PI / 180.0));
               double d = (double)(-Math.random()) * 0.6 - 0.3;
               Vec3 pos = new Vec3(((double)Math.random() - 0.5) * 0.3, d, 0.6);
               pos = pos.xRot(-ths.getXRot() * (float) (java.lang.Math.PI / 180.0));
               pos = pos.yRot(-ths.getYRot() * (float) (java.lang.Math.PI / 180.0));
               pos = pos.add(ths.getX(), ths.getEyeY(), ths.getZ());
               ParticleSpawner.spawnEatingPhysicsParticle(itemStack, clientLevel, pos.x, pos.y, pos.z, vel.x * 15.0, vel.y * 15.0, vel.z * 15.0);
            }

            info.cancel();
         }
      }
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"handleEntityEvent"},
      cancellable = true
   )
   private void handleEntityEvent(byte event, CallbackInfo info) {
      LivingEntity ths = (LivingEntity)(Object)this;
      if (ths == Minecraft.getInstance().player) {
         if (event == 47) {
            ItemStack stack = ths.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!stack.isEmpty()) {
               this.spawnItemBreakPhysics(stack, true);
            }
         } else if (event == 48) {
            ItemStack stack = ths.getItemBySlot(EquipmentSlot.OFFHAND);
            if (!stack.isEmpty()) {
               this.spawnItemBreakPhysics(stack, false);
            }
         }
      }
   }

   @Unique
   private void spawnItemBreakPhysics(ItemStack stack, boolean mainHand) {
      if (ConfigClient.itemBreakPhysics && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
         LivingEntity ths = (LivingEntity)(Object)this;
         if (ths.level() instanceof ClientLevel clientLevel) {
            PhysicsMod.blockifyItemStack(clientLevel, stack, mainHand);
         }
      }
   }
}
