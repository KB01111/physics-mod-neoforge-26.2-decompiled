package net.diebuddies.mixins.ocean;

import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({AbstractBoat.class})
public class MixinBoat {
   @Shadow
   private Status status;

   @Inject(
      at = {@At("TAIL")},
      method = {"tick"}
   )
   public void tick(CallbackInfo info) {
      AbstractBoat boat = (AbstractBoat)(Object)this;
   }
}
