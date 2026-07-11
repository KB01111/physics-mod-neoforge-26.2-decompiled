package net.diebuddies.mixins.ocean;

import net.diebuddies.physics.ocean.EntityOcean;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {
   @Inject(
      at = {@At("RETURN")},
      method = {"getBoundingBoxForCulling"},
      cancellable = true
   )
   private void physicsmod$modifyBoundingBoxForCulling(Entity entity, CallbackInfoReturnable<AABB> info) {
      AABB aabb = (AABB)info.getReturnValue();
      if (aabb != null) {
         info.setReturnValue(aabb.move(0.0, ((EntityOcean)entity).getPhysicsYOffset(), 0.0));
      }
   }
}
