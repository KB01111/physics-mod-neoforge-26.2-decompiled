package net.diebuddies.mixins.smoke;

import net.diebuddies.compat.Iris;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.render.MainRenderer;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({IrisRenderingPipeline.class})
public class MixinFinalPassRenderer {
   @Inject(
      at = {@At("RETURN")},
      method = {"finalizeLevelRendering"},
      cancellable = true
   )
   private void physicsmod$renderVolumetricSmoke(CallbackInfo info) {
      if (Iris.isExtending()) {
         MainRenderer mainRenderer = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).physicsmod$getMainRenderer();
         mainRenderer.renderVolumetricSmoke(Minecraft.getInstance().level);
      }
   }
}
