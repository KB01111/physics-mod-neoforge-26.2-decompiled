package net.diebuddies.physics.settings.ux;

import net.diebuddies.math.Math;
import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;

public class ButtonRenderer extends Animator {
   private TextAlignment alignment;
   private boolean renderTooltips = true;
   private ChatFormatting chatFormatting;
   private Identifier image;

   public ButtonRenderer(TextAlignment alignment, ChatFormatting chatFormatting) {
      this.chatFormatting = chatFormatting;
      this.alignment = alignment;
   }

   public ButtonRenderer(TextAlignment alignment) {
      this(alignment, null);
   }

   public ButtonRenderer() {
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

      int color = BaseColors.BACKGROUND_COLOR;
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth();
      float height = animatable.getAnimHeight();
      float depth = animatable.getAnimDepth();
      AbstractButton button = (AbstractButton)animatable;
      if (!button.active) {
         color = BaseColors.INACTIVE_COLOR;
      }

      drawRect(guiGraphics, x, y, width, height, depth, color);
      if (this.image != null) {
         drawRect(
            guiGraphics,
            this.image,
            (float)((int)x),
            (float)((int)y),
            (float)((int)width),
            (float)((int)height),
            0.0F,
            0.0F,
            1.0F,
            0.0F,
            1.0F,
            ARGB.colorFromFloat(animatable.getAnimAlpha(), animatable.getAnimRed(), animatable.getAnimGreen(), animatable.getAnimBlue())
         );
      }

      boolean buttonActiveBefore = button.active;
      button.active = true;
      FormattedCharSequence formattedCharSequence = (this.chatFormatting == null
            ? button.getMessage()
            : button.getMessage().copy().withStyle(this.chatFormatting))
         .getVisualOrderText();
      button.active = buttonActiveBefore;
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

   public ButtonRenderer setRenderTooltips(boolean renderTooltips) {
      this.renderTooltips = renderTooltips;
      return this;
   }

   public boolean isRenderingTooltips() {
      return this.renderTooltips;
   }

   public Identifier getImage() {
      return this.image;
   }

   public ButtonRenderer setImage(Identifier image) {
      this.image = image;
      return this;
   }
}
