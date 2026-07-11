package net.diebuddies.mixins.vines;

import java.util.ArrayList;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import net.caffeinemc.mods.sodium.client.render.chunk.UniformBufferManager;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.BuilderTaskOutput;
import net.caffeinemc.mods.sodium.client.render.viewport.Viewport;
import net.diebuddies.physics.vines.RenderSectionExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin({RenderSectionManager.class})
public class MixinRenderSectionManagerSodium {
   @Inject(
      at = {@At("HEAD")},
      method = {"processChunkBuildResults"},
      cancellable = true
   )
   private void physicsmod$processChunkMeshUpdates(
      ArrayList<BuilderTaskOutput> results, Viewport viewport, UniformBufferManager uniforms, CallbackInfoReturnable<Integer> info
   ) {
      for (BuilderTaskOutput result : results) {
         ((RenderSectionExtension)result.section).chunkUpdated();
      }
   }
}
