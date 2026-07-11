package net.diebuddies.mixins.item;

import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Snowball.class})
public abstract class MixinSnowball extends MixinEntity {
   @Override
   public void onClientRemoval(CallbackInfo info) {
   }
}
