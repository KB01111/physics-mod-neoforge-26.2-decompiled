package net.diebuddies.mixins.cloth;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({LevelExtractor.class})
public class MixinLevelExtractor {
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
      at = {@At("TAIL")},
      method = {"extractVisibleEntities"}
   )
   private void physicsmod$enableClothRendererEnd(
      Camera camera, Frustum frustum, DeltaTracker deltaTracker, LevelRenderState levelRenderState, CallbackInfo info
   ) {
      PhysicsMod.clothSkipRenderQueue = true;
   }
}
