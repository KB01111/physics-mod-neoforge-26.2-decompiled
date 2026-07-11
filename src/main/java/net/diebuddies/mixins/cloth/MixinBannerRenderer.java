package net.diebuddies.mixins.cloth;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.object.banner.BannerFlagModel;
import net.minecraft.client.model.object.banner.BannerModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.world.level.block.BannerBlock.AttachmentType;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BannerRenderer.class})
public abstract class MixinBannerRenderer {
   @Shadow
   @Final
   private SpriteGetter sprites;
   @Shadow
   @Final
   private BannerModel standingModel;
   @Shadow
   @Final
   private BannerModel wallModel;
   @Shadow
   @Final
   private BannerFlagModel standingFlagModel;
   @Shadow
   @Final
   private BannerFlagModel wallFlagModel;

   @Inject(
      at = {@At("RETURN")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$extractRenderState(
      BannerBlockEntity bannerBlockEntity,
      BannerRenderState bannerRenderState,
      float f,
      Vec3 vec3,
      @Nullable CrumblingOverlay crumblingOverlay,
      CallbackInfo info
   ) {
   }

   @Shadow
   private BannerModel bannerModel(AttachmentType type) {
      return null;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"submit"},
      cancellable = true
   )
   private void physicsmod$bannerCloth(
      BannerRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState, CallbackInfo info
   ) {
   }
}
