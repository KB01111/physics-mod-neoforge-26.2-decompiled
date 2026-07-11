package net.diebuddies.mixins.ocean;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({EntityRenderDispatcher.class})
public class MixinEntityRenderDispatcher {
   @Inject(
      method = {"submit"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V",
         shift = Shift.AFTER,
         ordinal = 0
      )}
   )
   private <S extends EntityRenderState> void physicsmod$applyOceanOffset(
      S entityRenderState,
      CameraRenderState cameraRenderState,
      double x,
      double y,
      double z,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      CallbackInfo info
   ) {
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"extractEntity"}
   )
   private <T extends Entity, S extends EntityRenderState> void physicsmod$extractEntity(
      T entity, float renderPercent, CallbackInfoReturnable<EntityRenderState> info
   ) {
   }
}
