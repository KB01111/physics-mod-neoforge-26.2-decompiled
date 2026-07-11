package net.diebuddies.mixins.item;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Entity.class})
public abstract class MixinEntity {
   @Inject(
      at = {@At("HEAD")},
      method = {"onClientRemoval"}
   )
   public void onClientRemoval(CallbackInfo info) {
   }
}
