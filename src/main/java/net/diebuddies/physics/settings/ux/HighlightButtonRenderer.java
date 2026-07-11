package net.diebuddies.physics.settings.ux;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.ARGB;

public class HighlightButtonRenderer extends Animator {
   private float time;

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      this.time += delta;
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth() - 1.0F;
      float height = animatable.getAnimHeight() - 1.0F;
      if (animatable instanceof AbstractWidget widget) {
         x = (float)widget.getX();
         y = (float)widget.getY();
         width = (float)(widget.getWidth() - 1);
         height = (float)(widget.getHeight() - 1);
      }

      float depth = 100.0F;
      int offset = (int)(Math.abs(Math.sin((double)this.time * 5.0)) * 3.0) + 1;
      int lineLength = 3;
      int color = BaseColors.HIGHLIGHT_COLOR;
      int backgroundColor = ARGB.color(255, 60, 90, 60);
      drawLine(
         guiGraphics,
         x - (float)offset + 1.0F,
         y - (float)offset + 1.0F,
         x + (float)lineLength - (float)offset + 1.0F,
         y - (float)offset + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         guiGraphics,
         x - (float)offset + 1.0F,
         y - (float)offset + 1.0F,
         x - (float)offset + 1.0F,
         y - (float)offset + (float)lineLength + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(guiGraphics, x - (float)offset, y - (float)offset, x + (float)lineLength - (float)offset, y - (float)offset, depth, color);
      drawLine(guiGraphics, x - (float)offset, y - (float)offset, x - (float)offset, y - (float)offset + (float)lineLength, depth, color);
      drawLine(
         guiGraphics,
         x + (float)offset + width - (float)lineLength + 1.0F,
         y - (float)offset + 1.0F,
         x + (float)offset + width + 1.0F,
         y - (float)offset + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         guiGraphics,
         x + (float)offset + width + 1.0F,
         y - (float)offset + 1.0F,
         x + (float)offset + width + 1.0F,
         y - (float)offset + (float)lineLength + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(guiGraphics, x + (float)offset + width - (float)lineLength, y - (float)offset, x + (float)offset + width, y - (float)offset, depth, color);
      drawLine(guiGraphics, x + (float)offset + width, y - (float)offset, x + (float)offset + width, y - (float)offset + (float)lineLength, depth, color);
      drawLine(
         guiGraphics,
         x - (float)offset + 1.0F,
         y + (float)offset + height + 1.0F,
         x + (float)lineLength - (float)offset + 1.0F,
         y + (float)offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         guiGraphics,
         x - (float)offset + 1.0F,
         y + (float)offset - (float)lineLength + height + 1.0F,
         x - (float)offset + 1.0F,
         y + (float)offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(guiGraphics, x - (float)offset, y + (float)offset + height, x + (float)lineLength - (float)offset, y + (float)offset + height, depth, color);
      drawLine(guiGraphics, x - (float)offset, y + (float)offset - (float)lineLength + height, x - (float)offset, y + (float)offset + height, depth, color);
      drawLine(
         guiGraphics,
         x + (float)offset + width - (float)lineLength + 1.0F,
         y + (float)offset + height + 1.0F,
         x + (float)offset + width + 1.0F,
         y + (float)offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         guiGraphics,
         x + (float)offset + width + 1.0F,
         y + (float)offset - (float)lineLength + height + 1.0F,
         x + (float)offset + width + 1.0F,
         y + (float)offset + height + 1.0F,
         depth,
         backgroundColor
      );
      drawLine(
         guiGraphics,
         x + (float)offset + width - (float)lineLength,
         y + (float)offset + height,
         x + (float)offset + width,
         y + (float)offset + height,
         depth,
         color
      );
      drawLine(
         guiGraphics,
         x + (float)offset + width,
         y + (float)offset - (float)lineLength + height,
         x + (float)offset + width,
         y + (float)offset + height,
         depth,
         color
      );
      return super.extraxtRenderState(animatable, guiGraphics, mouseX, mouseY, renderPercent, delta);
   }
}
