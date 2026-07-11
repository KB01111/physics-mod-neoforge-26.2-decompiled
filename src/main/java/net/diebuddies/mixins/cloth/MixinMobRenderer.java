package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState.LeashState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EntityRenderer.class},
   priority = 900
)
public abstract class MixinMobRenderer<T extends Entity, S extends EntityRenderState> {
   @Inject(
      at = {@At("RETURN")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$clothSimulations(T entity, S entityRenderState, float tickDelta, CallbackInfo info) {
      List<LeashState> leashStates = entityRenderState.leashStates;
      if (leashStates != null) {
         if (entity instanceof Leashable leashable) {
            leashable.getLeashHolder();
         } else {
            Object var10000 = null;
         }

         for (LeashState var8 : leashStates) {
            ;
         }
      }
   }

   @Redirect(
      method = {"submit"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitLeash(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/entity/state/EntityRenderState$LeashState;)V"
      )
   )
   private void physicsmod$maybeSkipSubmitLeash(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, LeashState leashState) {
      submitNodeCollector.submitLeash(poseStack, leashState);
   }
}
