package net.diebuddies.physics.settings.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;

public class GuiPhysicsCustomRenderer extends PictureInPictureRenderer<GuiPhysicsCustomRenderState> {
   public Class<GuiPhysicsCustomRenderState> getRenderStateClass() {
      return GuiPhysicsCustomRenderState.class;
   }

   protected void renderToTexture(GuiPhysicsCustomRenderState guiEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
      guiEntityRenderState.renderable().extractRenderState(submitNodeCollector);
   }

   protected String getTextureLabel() {
      return "physics mod custom";
   }
}
