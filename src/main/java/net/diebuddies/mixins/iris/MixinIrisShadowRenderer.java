package net.diebuddies.mixins.iris;

import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.render.MainRenderer;
import net.irisshaders.iris.mixin.LevelRendererAccessor;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({ShadowRenderer.class})
public class MixinIrisShadowRenderer {
   @Shadow
   @Final
   private SubmitNodeStorage submitNodeStorage;

   @Redirect(
      method = {"renderShadows"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"
      )
   )
   private void physicsmod$renderShadowCloth(ProfilerFiller profiler, String name, LevelRendererAccessor levelRenderer, Camera playerCamera) {
      if ("draw entities".equals(name) && levelRenderer instanceof net.diebuddies.minecraft.LevelRendererAccessor rendererAccessor) {
         MainRenderer renderer = rendererAccessor.physicsmod$getMainRenderer();
         renderer.renderDynamicCloth(rendererAccessor.physicsmod$getLevel(), ShadowRenderer.MODELVIEW, this.submitNodeStorage);
      }

      profiler.popPush(name);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"extractVisibleEntities"}
   )
   private void physicsmod$enableClothRendererStart(
      Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo info
   ) {
      PhysicsMod.clothSkipRenderQueue = false;
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"extractVisibleEntities"}
   )
   private void physicsmod$enableClothRendererEnd(
      Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo info
   ) {
      PhysicsMod.clothSkipRenderQueue = true;
   }
}
