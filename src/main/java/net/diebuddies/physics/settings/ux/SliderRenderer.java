package net.diebuddies.physics.settings.ux;

import net.diebuddies.math.Math;
import net.diebuddies.mixins.guiphysics.MixinAbstractSliderButtonAccessor;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

public class SliderRenderer extends Animator {
   private TextAlignment alignment;
   private boolean renderTooltips = true;
   private ChatFormatting chatFormatting;

   public SliderRenderer(TextAlignment alignment, ChatFormatting chatFormatting) {
      this.chatFormatting = chatFormatting;
      this.alignment = alignment;
   }

   public SliderRenderer(TextAlignment alignment) {
      this(alignment, null);
   }

   public SliderRenderer() {
      this(TextAlignment.CENTER);
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      boolean hovered = animatable.isInside((double)mouseX, (double)mouseY) && guiGraphics.containsPointInScissor(mouseX, mouseY);
      if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
         boolean wasHovered = accessor.getIsHovered();
         accessor.setIsHovered(hovered);
         if (!wasHovered && hovered) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_OFF, Math.random() * 0.2F + 0.9F));
         }
      }

      AbstractSliderButton slider = (AbstractSliderButton)animatable;
      MixinAbstractSliderButtonAccessor sliderAccessor = (MixinAbstractSliderButtonAccessor)animatable;
      int color = BaseColors.BACKGROUND_COLOR;
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth();
      float height = animatable.getAnimHeight();
      float depth = animatable.getAnimDepth();
      drawRect(guiGraphics, x, y, width, height, depth, color);
      double value = sliderAccessor.getValue();
      int barSize = 8;
      float sliderOffset = (float)value * (width - (float)barSize);
      drawRect(guiGraphics, x + sliderOffset, y, (float)barSize, height, depth, ARGB.color(100, 167, 167, 167));
      drawLine(guiGraphics, x + sliderOffset, y + 1.0F, x + sliderOffset + (float)barSize, y + 1.0F, depth, ARGB.color(255, 40, 40, 40));
      drawLine(guiGraphics, x + sliderOffset + 1.0F, y, x + sliderOffset + 1.0F, y + height - 1.0F, depth, ARGB.color(255, 40, 40, 40));
      color = BaseColors.BAR_COLOR;
      if (slider.isHoveredOrFocused()) {
         color = BaseColors.HIGHLIGHT_COLOR;
      }

      if (!slider.active) {
         color = BaseColors.DISABLED_COLOR;
      }

      drawLine(guiGraphics, x + sliderOffset, y, x + sliderOffset + (float)barSize, y, depth, color);
      drawLine(guiGraphics, x + sliderOffset + (float)barSize, y, x + sliderOffset + (float)barSize, y + height - 1.0F, depth, color);
      drawLine(guiGraphics, x + sliderOffset, y + height - 1.0F, x + sliderOffset + (float)barSize, y + height - 1.0F, depth, color);
      drawLine(guiGraphics, x + sliderOffset, y, x + sliderOffset, y + height - 1.0F, depth, color);
      boolean buttonActiveBefore = slider.active;
      slider.active = true;
      FormattedCharSequence formattedCharSequence = (this.chatFormatting == null
            ? slider.getMessage()
            : slider.getMessage().copy().withStyle(this.chatFormatting))
         .getVisualOrderText();
      slider.active = buttonActiveBefore;
      Font font = Minecraft.getInstance().font;
      float xText = x + 7.0F;
      if (this.alignment == TextAlignment.CENTER) {
         xText = x + width * 0.5F - (float)font.width(formattedCharSequence) * 0.5F;
      } else if (this.alignment == TextAlignment.RIGHT) {
         xText = x + width - (float)font.width(formattedCharSequence) - 7.0F;
      }

      drawText(guiGraphics, font, formattedCharSequence, (float)Math.fastRound(xText), (float)Math.fastRound(y + (height - 8.0F) / 2.0F));
      if (this.renderTooltips
         && animatable instanceof MixinAbstractWidgetAccessor invoker
         && animatable instanceof AbstractWidget widget
         && widget.isHoveredOrFocused()) {
         invoker.getTooltipHolder().refreshTooltipForNextRenderPass(guiGraphics, mouseX, mouseY, widget.isHovered(), widget.isFocused(), widget.getRectangle());
      }

      return true;
   }

   public void setRenderTooltips(boolean renderTooltips) {
      this.renderTooltips = renderTooltips;
   }

   public boolean isRenderingTooltips() {
      return this.renderTooltips;
   }
}
