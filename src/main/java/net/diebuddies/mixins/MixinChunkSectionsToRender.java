package net.diebuddies.mixins;

import com.mojang.blaze3d.textures.GpuSampler;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.minecraft.ChunkSectionsToRenderExtension;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.MainRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ChunkSectionsToRender.class},
   priority = 2010
)
public class MixinChunkSectionsToRender implements ChunkSectionsToRenderExtension {
   @Unique
   private LevelRendererAccessor physicsmod$renderer;

   @Inject(
      at = {@At("HEAD")},
      method = {"renderGroup"}
   )
   private void physicsmod$renderLiquids(ChunkSectionLayerGroup layerGroup, GpuSampler gpuSampler, CallbackInfo info) {
      if (!StarterClient.sodium && this.physicsmod$renderer != null) {
         if (layerGroup == ChunkSectionLayerGroup.TRANSLUCENT) {
            this.physicsmod$renderer.physicsmod$getMainRenderer().renderLiquid(this.physicsmod$renderer.physicsmod$getLevel(), ChunkSectionLayer.TRANSLUCENT);
         }
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"renderGroup"}
   )
   private void physicsmod$renderSolids(ChunkSectionLayerGroup layerGroup, GpuSampler gpuSampler, CallbackInfo info) {
      if (!StarterClient.sodium && this.physicsmod$renderer != null) {
         if (layerGroup == ChunkSectionLayerGroup.OPAQUE) {
            MainRenderer mainRenderer = this.physicsmod$renderer.physicsmod$getMainRenderer();
            if (StarterClient.iris() && Iris.isShadowPass()) {
               Iris.setShadowMatrices(mainRenderer);
            } else if (StarterClient.optifabric && Optifine.isShadowPass()) {
               Optifine.setShadowMatrices(mainRenderer);
            }

            mainRenderer.renderAll(this.physicsmod$renderer.physicsmod$getLevel(), ChunkSectionLayer.CUTOUT);
            mainRenderer.renderStaticCloth(Minecraft.getInstance().levelExtractor.level);
         }
      }
   }

   @Override
   public void physicsmod$setRenderer(LevelRendererAccessor renderer) {
      this.physicsmod$renderer = renderer;
   }
}
