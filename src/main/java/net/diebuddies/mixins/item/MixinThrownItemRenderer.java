package net.diebuddies.mixins.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.RenderHelper;
import net.diebuddies.minecraft.ThrownItemRenderStatePhysics;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.ThrownItemStateExtended;
import net.diebuddies.physics.ThrownItemType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.throwableitemprojectile.Snowball;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEgg;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ThrownItemRenderer.class})
public abstract class MixinThrownItemRenderer<T extends Entity & ItemSupplier> extends EntityRenderer<T, ThrownItemRenderState> {
   protected MixinThrownItemRenderer(Context context) {
      super(context);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"submit"},
      cancellable = true
   )
   public void physicsmod$customRender(
      ThrownItemRenderState thrownItemRenderState,
      PoseStack poseStack,
      SubmitNodeCollector submitNodeCollector,
      CameraRenderState cameraRenderState,
      CallbackInfo info
   ) {
      ThrownItemRenderStatePhysics light = ((ThrownItemStateExtended)thrownItemRenderState).physicsmod$getThrownItemRenderState();
      if (light instanceof ThrownItemRenderStatePhysics) {
         int lightx = thrownItemRenderState.lightCoords;
         boolean rendered = false;
         if (light.type == ThrownItemType.EGG && ConfigClient.eggModel != 2) {
            RenderHelper.renderMesh(
               submitNodeCollector,
               light.id,
               light.rotation,
               PhysicsMod.EGG_TEXTURE,
               PhysicsMod.eggMesh.get(ConfigClient.eggModel),
               poseStack,
               lightx,
               OverlayTexture.NO_OVERLAY,
               ConfigClient.eggShade
            );
            rendered = true;
         } else if (light.type == ThrownItemType.ENDERPEARL && ConfigClient.enderpearlModel != 2) {
            RenderHelper.renderMesh(
               submitNodeCollector,
               light.id,
               light.rotation,
               PhysicsMod.ENDERPEARL_TEXTURE,
               PhysicsMod.enderpearlMesh.get(ConfigClient.enderpearlModel),
               poseStack,
               lightx,
               OverlayTexture.NO_OVERLAY,
               ConfigClient.enderpearlShade
            );
            rendered = true;
         } else if (light.type == ThrownItemType.SNOWBALL && ConfigClient.snowballModel != 2) {
            RenderHelper.renderMesh(
               submitNodeCollector,
               light.id,
               light.rotation,
               PhysicsMod.SNOWBALL_TEXTURE,
               PhysicsMod.snowballMesh.get(ConfigClient.snowballModel),
               poseStack,
               lightx,
               OverlayTexture.NO_OVERLAY,
               ConfigClient.snowballShade
            );
            rendered = true;
         }

         if (rendered) {
            super.submit(thrownItemRenderState, poseStack, submitNodeCollector, cameraRenderState);
            info.cancel();
         }
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"extractRenderState"},
      cancellable = true
   )
   private void physicsmod$extendRenderState(Entity entity, ThrownItemRenderState entityRenderState, float tickDelta, CallbackInfo info) {
      ThrownItemRenderStatePhysics state = new ThrownItemRenderStatePhysics();
      ((ThrownItemStateExtended)entityRenderState).physicsmod$setThrownItemRenderState(state);
      state.id = entity.getId();
      state.rotation = (float)entity.tickCount + tickDelta;
      if (entity instanceof ThrownEgg) {
         state.type = ThrownItemType.EGG;
      } else if (entity instanceof ThrownEnderpearl) {
         state.type = ThrownItemType.ENDERPEARL;
      } else if (entity instanceof Snowball) {
         state.type = ThrownItemType.SNOWBALL;
      } else {
         state.type = null;
      }
   }
}
