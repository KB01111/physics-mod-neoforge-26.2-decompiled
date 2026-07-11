package net.diebuddies.render.util;

import net.minecraft.client.renderer.entity.layers.RenderLayer;

public interface PhysicsSubmitExtension {
   void physicsmod$setRenderLayer(RenderLayer<?, ?> var1);

   RenderLayer<?, ?> physicsmod$getRenderLayer();
}
