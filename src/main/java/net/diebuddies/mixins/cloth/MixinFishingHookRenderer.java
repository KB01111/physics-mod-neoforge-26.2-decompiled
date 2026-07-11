package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.state.FishingHookRenderState;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({FishingHookRenderer.class})
public abstract class MixinFishingHookRenderer {
   @Inject(
      at = {@At("RETURN")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$clothSimulations(FishingHook fishingHook, FishingHookRenderState fishingHookRenderState, float tickDelta, CallbackInfo info) {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"stringVertex"},
      cancellable = true
   )
   private static void physicsmod$cancelLineRender(
      float x, float y, float z, VertexConsumer vertexConsumer, Pose pose, float startPerc, float endPerc, float lineWidth, CallbackInfo info
   ) {
   }
}
