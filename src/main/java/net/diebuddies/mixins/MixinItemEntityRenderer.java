package net.diebuddies.mixins;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.ItemEntityRenderStatePhysics;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer extends EntityRenderer<ItemEntity, ItemEntityRenderState> {
   protected MixinItemEntityRenderer(Context ctx) {
      super(ctx);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"submit"},
      cancellable = true
   )
   private void physicsmod$renderItemPhysics(
      ItemEntityRenderState itemRenderState,
      PoseStack matrixStack,
      SubmitNodeCollector submitNodeCollector,
      CameraRenderState cameraRenderState,
      CallbackInfo info
   ) {
      if (ConfigClient.hasItemPhysics() && !itemRenderState.item.isEmpty() && itemRenderState instanceof ItemEntityRenderStatePhysics physicsState) {
         matrixStack.pushPose();
         int count = itemRenderState.count;
         float offset = 0.05F;
         matrixStack.mulPose(Axis.YP.rotation(itemRenderState.bobOffset));
         matrixStack.mulPose(Axis.XP.rotation((float)Math.toRadians(90.0) + physicsState.physicsmod$rotation()));
         if (!physicsState.physicsmod$isBlock()) {
            matrixStack.translate(0.0F, 0.0F, (float)(-(count - 1)) * offset);
         } else {
            matrixStack.translate(0.0F, -0.145F, 0.0F);
         }

         ItemStackRenderState itemStackRenderState = itemRenderState.item;
         if (!physicsState.physicsmod$isBlock()) {
            for (int i = 0; i < count; i++) {
               itemStackRenderState.submit(
                  matrixStack, submitNodeCollector, itemRenderState.lightCoords, OverlayTexture.NO_OVERLAY, itemRenderState.outlineColor
               );
               matrixStack.translate(0.0F, 0.0F, offset);
            }
         } else {
            itemStackRenderState.submit(matrixStack, submitNodeCollector, itemRenderState.lightCoords, OverlayTexture.NO_OVERLAY, itemRenderState.outlineColor);
         }

         matrixStack.popPose();
         super.submit(itemRenderState, matrixStack, submitNodeCollector, cameraRenderState);
         info.cancel();
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"extractRenderState"}
   )
   private void extractRenderState(ItemEntity entity, ItemEntityRenderState entityRenderState, float tickDelta, CallbackInfo info) {
      if (ConfigClient.hasItemPhysics() && !entityRenderState.item.isEmpty() && entityRenderState instanceof ItemEntityRenderStatePhysics physicsState) {
         ItemStack itemStack = entity.getItem();
         boolean isBlock = entityRenderState.item.getModelBoundingBox().getZsize() > 0.0625;
         float passedTimeMillis = tickDelta * 1.0F / 20.0F * 1000.0F * 0.1F;
         double rotationSpeed = 0.005 * (double)ConfigClient.itemRotationSpeed;
         if (entity.isInWater()) {
            rotationSpeed = 0.002;
         }

         if (entity.onGround()) {
            if (!(isBlock & !(itemStack.is(Items.TRIDENT) | itemStack.is(Items.SPYGLASS)))) {
               entity.flyDist = 0.0F;
            }
         } else {
            entity.flyDist = (float)((double)entity.flyDist + (double)passedTimeMillis * rotationSpeed);
         }

         physicsState.physicsmod$rotation(entity.flyDist);
         physicsState.physicsmod$isBlock(isBlock);
      }
   }
}
