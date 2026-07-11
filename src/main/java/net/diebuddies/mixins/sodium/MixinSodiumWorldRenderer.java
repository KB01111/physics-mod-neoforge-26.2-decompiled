package net.diebuddies.mixins.sodium;

import com.mojang.blaze3d.textures.GpuSampler;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;
import net.diebuddies.compat.Iris;
import net.diebuddies.minecraft.ChunkSectionsToRenderExtension;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({SodiumWorldRenderer.class})
public class MixinSodiumWorldRenderer implements ChunkSectionsToRenderExtension {
   @Unique
   private LevelRendererAccessor physicsmod$renderer;

   @Inject(
      at = {@At("HEAD")},
      method = {"drawChunkLayer"}
   )
   private void physicsmod$renderLiquids(
      ChunkSectionLayerGroup layerGroup, @Coerce Object matrices, double x, double y, double z, GpuSampler gpuSampler, CallbackInfo info
   ) {
      if (this.physicsmod$renderer != null) {
         if (layerGroup == ChunkSectionLayerGroup.TRANSLUCENT) {
            this.physicsmod$renderer.physicsmod$getMainRenderer().renderLiquid(this.physicsmod$renderer.physicsmod$getLevel(), ChunkSectionLayer.TRANSLUCENT);
         }
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"drawChunkLayer"}
   )
   private void physicsmod$renderSolids(
      ChunkSectionLayerGroup layerGroup, @Coerce Object matrices, double x, double y, double z, GpuSampler gpuSampler, CallbackInfo info
   ) {
      if (this.physicsmod$renderer != null) {
         if (layerGroup == ChunkSectionLayerGroup.OPAQUE) {
            MainRenderer mainRenderer = this.physicsmod$renderer.physicsmod$getMainRenderer();
            if (StarterClient.iris() && Iris.isShadowPass()) {
               Iris.setShadowMatrices(mainRenderer);
               Iris.setRenderingPipelineToEntities(mainRenderer);
            }

            mainRenderer.renderAll(this.physicsmod$renderer.physicsmod$getLevel(), ChunkSectionLayer.CUTOUT);
            mainRenderer.renderStaticCloth(this.physicsmod$renderer.physicsmod$getLevel());
         }
      }
   }

   @Override
   public void physicsmod$setRenderer(LevelRendererAccessor renderer) {
      this.physicsmod$renderer = renderer;
   }
}
