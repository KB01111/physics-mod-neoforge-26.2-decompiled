package net.diebuddies.physics.settings.ux;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

public class MainToolTipRenderer {
   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      List<FormattedCharSequence> list,
      GuiGraphicsExtractor guiGraphics,
      float x,
      float width,
      float y,
      float barSize,
      int backgroundColor
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      int heightPerRow = 10;
      int height = list.size() * heightPerRow + padding * 2;
      float depth = -120.0F;
      int yOffset = height;
      if (alignment == MainToolTipRenderer.TooltipAlignment.TOP) {
         yOffset = 0;
      }

      Animator.drawRect(guiGraphics, x, y - (float)yOffset, width, (float)height, depth, backgroundColor);
      int color = BaseColors.HIGHLIGHT_COLOR;
      if (animatable != null) {
         BarRenderer bar = animatable.getAnimator(BarRenderer.class);
         if (bar != null) {
            color = bar.getActiveColor();
         }
      }

      BarRenderer.renderHighlightBar(guiGraphics, BarRenderer.BarAlignment.BOTTOM, x, y - (float)yOffset, width, (float)height, depth + 1.0F, barSize, color);
      float xText = x + (float)padding;

      for (int i = 0; i < list.size(); i++) {
         float yText = y + (float)(i * heightPerRow) + (float)padding - (float)yOffset;
         Animator.drawText(guiGraphics, font, list.get(i), xText, yText);
      }
   }

   public static void renderToolTip(
      @Nullable Animatable animatable,
      List<FormattedCharSequence> list,
      GuiGraphicsExtractor guiGraphics,
      float x,
      float width,
      float y,
      float barSize,
      int color
   ) {
      renderToolTip(MainToolTipRenderer.TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, List<FormattedCharSequence> list, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize
   ) {
      renderToolTip(MainToolTipRenderer.TooltipAlignment.BOTTOM, animatable, list, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      Component component,
      GuiGraphicsExtractor guiGraphics,
      float x,
      float width,
      float y,
      float barSize,
      int color
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      List<FormattedCharSequence> list = font.split(component, (int)width - padding * 2);
      renderToolTip(alignment, animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(
      MainToolTipRenderer.TooltipAlignment alignment,
      @Nullable Animatable animatable,
      Component component,
      GuiGraphicsExtractor guiGraphics,
      float x,
      float width,
      float y,
      float barSize
   ) {
      renderToolTip(alignment, animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, Component component, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize, int color
   ) {
      Font font = Minecraft.getInstance().font;
      int padding = 10;
      List<FormattedCharSequence> list = font.split(component, (int)width - padding * 2);
      renderToolTip(animatable, list, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(
      @Nullable Animatable animatable, Component component, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize
   ) {
      renderToolTip(animatable, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(Component component, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize, int color) {
      renderToolTip(null, component, guiGraphics, x, width, y, barSize, color);
   }

   public static void renderToolTip(Component component, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize) {
      renderToolTip(null, component, guiGraphics, x, width, y, barSize, BaseColors.BACKGROUND_COLOR);
   }

   public static void renderToolTip(List<FormattedCharSequence> list, GuiGraphicsExtractor guiGraphics, float x, float width, float y, float barSize) {
      renderToolTip(null, list, guiGraphics, x, width, y, barSize);
   }

   public static enum TooltipAlignment {
      TOP,
      BOTTOM;
   }
}
