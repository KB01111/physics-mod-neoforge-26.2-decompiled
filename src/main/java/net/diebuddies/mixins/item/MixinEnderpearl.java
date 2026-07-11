package net.diebuddies.mixins.item;

import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ThrownEnderpearl.class})
public abstract class MixinEnderpearl extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
   }
}
