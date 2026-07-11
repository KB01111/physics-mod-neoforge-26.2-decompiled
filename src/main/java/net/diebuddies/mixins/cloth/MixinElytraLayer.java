package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.PlayerRenderStateExtended;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.WingsLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({WingsLayer.class})
public class MixinElytraLayer {
   @Inject(
      at = {@At("HEAD")},
      method = {"submit"},
      cancellable = true
   )
   private void physicsmod$replaceElytra(
      PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, HumanoidRenderState humanoidRenderState, float f, float g, CallbackInfo info
   ) {
      if (humanoidRenderState instanceof PlayerRenderStateExtended extended
         && extended.physicsmod$getClothRenderState() != null
         && extended.physicsmod$getClothRenderState().skipElytra) {
         info.cancel();
      }
   }
}
