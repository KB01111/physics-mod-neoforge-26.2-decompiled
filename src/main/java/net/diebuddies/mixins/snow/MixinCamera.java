package net.diebuddies.mixins.snow;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Camera.class})
public class MixinCamera {
   @Shadow
   private Level level;
   @Shadow
   private Vec3 position = Vec3.ZERO;
   @Shadow
   @Final
   private Vector3f forwards;
   @Unique
   private Vector3d rayStartPos = new Vector3d();
   @Unique
   private Vector3d rayDirection = new Vector3d();

   @Inject(
      at = {@At("RETURN")},
      method = {"getMaxZoom"},
      cancellable = true
   )
   private void getMaxZoom(float maxZoom, CallbackInfoReturnable<Float> info) {
      if (this.level instanceof ClientLevel var3) {
         ;
      }
   }
}
