package net.diebuddies.mixins.ocean;

import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class MixinCamera {
   @Shadow
   private Vec3 position;
   @Shadow
   private boolean initialized;
   @Shadow
   private Entity entity;
   @Shadow
   private Level level;

   @Inject(
      at = {@At("RETURN")},
      method = {"getFluidInCamera"},
      cancellable = true
   )
   private void getFluidInCamera(CallbackInfoReturnable<FogType> info) {
      if (this.initialized) {
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"alignWithEntity"},
      cancellable = true
   )
   private void physicsmod$alignWithEntity(float partialTicks, CallbackInfo info) {
   }
}
