package net.diebuddies.mixins;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin({LivingEntityRenderer.class})
public class MixinLivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
   @ModifyVariable(
      method = {"submit"},
      at = @At("STORE"),
      ordinal = 0
   )
   private RenderLayer<?, ?> physicsmod$captureLoopVar(RenderLayer<S, M> value) {
      PhysicsMod mod = PhysicsMod.getCurrentInstance();
      if (mod != null && mod.blockify) {
         mod.blockifyFeature = value;
      }

      return value;
   }
}
