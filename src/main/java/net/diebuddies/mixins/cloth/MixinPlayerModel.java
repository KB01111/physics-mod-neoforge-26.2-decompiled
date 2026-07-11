package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.PlayerRenderStateExtended;
import net.diebuddies.physics.settings.cloth.ClothConstants;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerModel.class})
public class MixinPlayerModel {
   @Inject(
      at = {@At("HEAD")},
      method = {"setupAnim"}
   )
   private void physicsmod$fixHeadInvisible(AvatarRenderState avatarRenderState, CallbackInfo info) {
      if (avatarRenderState instanceof PlayerRenderStateExtended extended && extended.physicsmod$getClothRenderState() != null) {
         ((PlayerModel)(Object)this).head.visible = true;
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"setupAnim"}
   )
   private void physicsmod$hideBodyParts(AvatarRenderState avatarRenderState, CallbackInfo info) {
      if (avatarRenderState instanceof PlayerRenderStateExtended extended && extended.physicsmod$getClothRenderState() != null) {
         ClothConstants.hideProperParts((PlayerModel)(Object)this, extended.physicsmod$getClothRenderState().hiddenModelParts);
      }
   }
}
