package net.diebuddies.mixins.item;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ThrownEgg.class})
public abstract class MixinEgg extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
   }
}
