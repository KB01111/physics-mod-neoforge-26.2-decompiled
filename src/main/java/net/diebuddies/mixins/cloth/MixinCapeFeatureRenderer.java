package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.physics.PlayerRenderStateExtended;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CapeLayer.class})
public class MixinCapeFeatureRenderer {
   @Inject(
      at = {@At("HEAD")},
      method = {"submit"},
      cancellable = true
   )
   public void physicsmod$skipCapeSubmit(
      PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int light, AvatarRenderState avatarRenderState, float f, float g, CallbackInfo info
   ) {
      if (avatarRenderState instanceof PlayerRenderStateExtended extended
         && extended.physicsmod$getClothRenderState() != null
         && extended.physicsmod$getClothRenderState().skipCape) {
         info.cancel();
      }
   }
}
