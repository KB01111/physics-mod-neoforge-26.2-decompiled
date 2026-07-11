package net.diebuddies.mixins.ocean;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({Particle.class})
public class MixinParticle {
   @Shadow
   @Final
   protected ClientLevel level;
   @Shadow
   protected double xo;
   @Shadow
   protected double yo;
   @Shadow
   protected double zo;
   @Shadow
   protected double x;
   @Shadow
   protected double y;
   @Shadow
   protected double z;
   @Shadow
   protected double xd;
   @Shadow
   protected double yd;
   @Shadow
   protected double zd;
   @Shadow
   protected boolean onGround;
   @Shadow
   protected int lifetime;
   @Shadow
   protected float gravity;

   @Inject(
      at = {@At("HEAD")},
      method = {"getLightCoords"},
      cancellable = true
   )
   protected void getLightCoords(float renderPercent, CallbackInfoReturnable<Integer> info) {
   }
}
