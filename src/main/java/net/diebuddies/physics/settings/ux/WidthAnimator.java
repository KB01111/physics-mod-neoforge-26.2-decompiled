package net.diebuddies.physics.settings.ux;

import net.diebuddies.mixins.guiphysics.MixinAbstractWidgetAccessor;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.joml.Math;

public class WidthAnimator extends Animator {
   private float baseWidth;
   private float baseHeight;
   private float baseX;
   private float currentWidth;
   private float oldWidth;
   private boolean hovered = false;
   private boolean offsetPos;
   private float widthMultiplier;
   private float speed;
   private float offset;

   public WidthAnimator(float widthMultiplier, float speed, boolean offsetPos) {
      this.offsetPos = offsetPos;
      this.widthMultiplier = widthMultiplier;
      this.speed = speed;
   }

   public WidthAnimator(float widthMultiplier, float speed) {
      this(widthMultiplier, speed, false);
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      if (animatable instanceof MixinAbstractWidgetAccessor widget) {
         this.hovered = widget.getIsHovered();
      }

      float width = Math.lerp(this.oldWidth, this.currentWidth, renderPercent);
      if (this.offsetPos) {
         this.offset = width - this.baseWidth;
         animatable.setAnimX(this.baseX - this.offset);
      }

      animatable.setAnimWidth(width);
      return false;
   }

   @Override
   public void tick(Animatable animatable) {
      this.updatePositions();
      if (this.hovered) {
         this.currentWidth = Math.lerp(animatable.getAnimWidth(), this.baseWidth + this.baseHeight * this.widthMultiplier, this.speed);
      } else {
         this.currentWidth = Math.lerp(animatable.getAnimWidth(), this.baseWidth, this.speed);
      }
   }

   @Override
   public void init(Animatable animatable) {
      this.baseX = animatable.getAnimX();
      this.baseWidth = animatable.getAnimWidth();
      this.baseHeight = animatable.getAnimHeight();
      this.currentWidth = this.baseWidth;
      this.oldWidth = this.baseWidth;
      this.updatePositions();
   }

   private void updatePositions() {
      this.oldWidth = this.currentWidth;
   }

   public void setBaseX(float baseX) {
      this.baseX = baseX;
   }

   public float getBaseX() {
      return this.baseX;
   }

   public float getOffset() {
      return this.offset;
   }
}
