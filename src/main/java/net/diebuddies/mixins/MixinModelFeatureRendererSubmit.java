package net.diebuddies.mixins;

import net.diebuddies.render.util.PhysicsSubmitExtension;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer.Submit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({Submit.class})
public class MixinModelFeatureRendererSubmit implements PhysicsSubmitExtension {
   @Unique
   private RenderLayer<?, ?> physicsmod$renderLayer;

   @Override
   public void physicsmod$setRenderLayer(RenderLayer<?, ?> renderLayer) {
      this.physicsmod$renderLayer = renderLayer;
   }

   @Override
   public RenderLayer<?, ?> physicsmod$getRenderLayer() {
      return this.physicsmod$renderLayer;
   }
}
