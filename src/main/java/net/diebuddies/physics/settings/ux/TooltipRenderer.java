package net.diebuddies.physics.settings.ux;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;

public class TooltipRenderer extends Animator {
   private TooltipRenderer.Renderable renderable;

   public TooltipRenderer(TooltipRenderer.Renderable renderable) {
      this.renderable = renderable;
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      if (animatable instanceof AbstractWidget widget && widget.isHoveredOrFocused()) {
         this.renderable.extractRenderState(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
      }

      return false;
   }

   public interface Renderable {
      void extractRenderState(Animatable var1, GuiGraphicsExtractor var2, int var3, int var4, float var5, float var6);
   }
}
