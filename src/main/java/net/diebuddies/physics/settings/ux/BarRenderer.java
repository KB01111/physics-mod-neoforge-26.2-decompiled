package net.diebuddies.physics.settings.ux;

import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.util.ARGB;
import org.joml.Math;

public class BarRenderer extends Animator {
   private float oldHoveredPercent;
   private float currentHoveredPercent;
   private float animationSpeed = 0.6F;
   private boolean highlightBarAnimation = false;
   private BarRenderer.BarAlignment barAlignment;
   private int hoveredColor = BaseColors.HIGHLIGHT_COLOR;
   private int disabledColor = BaseColors.DISABLED_COLOR;
   private int darkenColor = ARGB.color(255, 200, 200, 200);
   private int barColor = BaseColors.BAR_COLOR;
   private boolean active;
   private float barSize;

   public BarRenderer(BarRenderer.BarAlignment barAlignment, boolean highlightBarAnimation, float barSize) {
      this.highlightBarAnimation = highlightBarAnimation;
      this.barAlignment = barAlignment;
      this.barSize = barSize;
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      boolean hovered = false;
      if (animatable instanceof MixinAbstractWidgetAccessor accessor) {
         hovered = accessor.getIsHovered();
      }

      this.active = true;
      boolean focused = false;
      if (animatable instanceof AbstractWidget widget) {
         this.active = widget.isActive();
         focused = widget.isFocused();
      }

      int color = BaseColors.BACKGROUND_COLOR;
      float x = animatable.getAnimX();
      float y = animatable.getAnimY();
      float width = animatable.getAnimWidth();
      float height = animatable.getAnimHeight();
      float depth = animatable.getAnimDepth();
      color = this.active ? this.barColor : this.disabledColor;
      if (!hovered && focused) {
         color = ARGB.multiply(color, this.darkenColor);
      }

      if (this.barAlignment == BarRenderer.BarAlignment.BOTTOM) {
         drawRect(guiGraphics, x, y + height - this.barSize, width, this.barSize, depth, color);
      } else if (this.barAlignment == BarRenderer.BarAlignment.RIGHT) {
         drawRect(guiGraphics, x + width - this.barSize, y, this.barSize, height, depth, color);
      } else if (this.barAlignment == BarRenderer.BarAlignment.TOP) {
         drawRect(guiGraphics, x, y + height, width, this.barSize, depth, color);
      } else if (this.barAlignment == BarRenderer.BarAlignment.LEFT) {
         drawRect(guiGraphics, x, y, this.barSize, height, depth, color);
      }

      if (animatable instanceof AbstractWidget widget && widget.isHoveredOrFocused()) {
         color = this.active ? this.hoveredColor : this.disabledColor;
         if (!hovered && focused) {
            color = ARGB.multiply(color, this.darkenColor);
         }

         float hoverPercent = Math.lerp(this.oldHoveredPercent, this.currentHoveredPercent, renderPercent) * 0.5F;
         if (!this.isHighlightBarAnimation()) {
            hoverPercent = 0.5F;
         }

         if (this.barAlignment == BarRenderer.BarAlignment.BOTTOM) {
            drawRect(guiGraphics, x + width * 0.5F - width * hoverPercent, y + height - this.barSize, width * hoverPercent * 2.0F, this.barSize, depth, color);
         } else if (this.barAlignment == BarRenderer.BarAlignment.RIGHT) {
            drawRect(guiGraphics, x + width - this.barSize, y + height * 0.5F - height * hoverPercent, this.barSize, height * hoverPercent * 2.0F, depth, color);
         } else if (this.barAlignment == BarRenderer.BarAlignment.TOP) {
            drawRect(guiGraphics, x + width * 0.5F - width * hoverPercent, y, width * hoverPercent * 2.0F, this.barSize, depth, color);
         } else if (this.barAlignment == BarRenderer.BarAlignment.LEFT) {
            drawRect(guiGraphics, x, y + height * 0.5F - height * hoverPercent, this.barSize, height * hoverPercent * 2.0F, depth, color);
         }
      }

      return false;
   }

   public static void renderHighlightBar(
      GuiGraphicsExtractor guiGraphics,
      BarRenderer.BarAlignment barAlignment,
      float x,
      float y,
      float width,
      float height,
      float depth,
      float barSize,
      int color
   ) {
      if (barAlignment == BarRenderer.BarAlignment.BOTTOM) {
         drawRect(guiGraphics, x, y + height - barSize, width, barSize, depth, color);
      } else if (barAlignment == BarRenderer.BarAlignment.RIGHT) {
         drawRect(guiGraphics, x + width - barSize, y, barSize, height, depth, color);
      } else if (barAlignment == BarRenderer.BarAlignment.TOP) {
         drawRect(guiGraphics, x, y + height, width, barSize, depth, color);
      } else if (barAlignment == BarRenderer.BarAlignment.LEFT) {
         drawRect(guiGraphics, x, y, barSize, height, depth, color);
      }
   }

   @Override
   public void tick(Animatable animatable) {
      super.tick(animatable);
      this.oldHoveredPercent = this.currentHoveredPercent;
      float hovered = 0.0F;
      if (animatable instanceof MixinAbstractWidgetAccessor accessor && accessor.getIsHovered()) {
         hovered = 1.0F;
      }

      this.currentHoveredPercent = Math.lerp(this.currentHoveredPercent, hovered, this.animationSpeed);
   }

   public int getActiveColor() {
      return this.active ? this.hoveredColor : this.disabledColor;
   }

   public BarRenderer setAnimationSpeed(float animationSpeed) {
      this.animationSpeed = animationSpeed;
      return this;
   }

   public float getAnimationSpeed() {
      return this.animationSpeed;
   }

   public BarRenderer setBarAlignment(BarRenderer.BarAlignment barAlignment) {
      this.barAlignment = barAlignment;
      return this;
   }

   public BarRenderer.BarAlignment getBarAlignment() {
      return this.barAlignment;
   }

   public BarRenderer setHighlightBarAnimation(boolean highlightBarAnimation) {
      this.highlightBarAnimation = highlightBarAnimation;
      return this;
   }

   public boolean isHighlightBarAnimation() {
      return this.highlightBarAnimation;
   }

   public BarRenderer setHoveredColor(int hoveredColor) {
      this.hoveredColor = hoveredColor;
      return this;
   }

   public BarRenderer setDisabledColor(int disabledColor) {
      this.disabledColor = disabledColor;
      return this;
   }

   public BarRenderer setBarColor(int barColor) {
      this.barColor = barColor;
      return this;
   }

   public BarRenderer setDarkenColor(int darkenColor) {
      this.darkenColor = darkenColor;
      return this;
   }

   public static enum BarAlignment {
      LEFT,
      RIGHT,
      TOP,
      BOTTOM;
   }
}
