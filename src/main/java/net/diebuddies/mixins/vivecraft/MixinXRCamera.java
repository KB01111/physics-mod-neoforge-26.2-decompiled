package net.diebuddies.mixins.vivecraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.render.XRCamera;

@Mixin({XRCamera.class})
public class MixinXRCamera {
   @Inject(
      at = {@At("TAIL")},
      method = {"setup"}
   )
   private void physicsmod$oceanOffset(Level level, Entity entity, boolean thirdPerson, boolean thirdPersonInverted, float renderPercent, CallbackInfo info) {
   }
}
