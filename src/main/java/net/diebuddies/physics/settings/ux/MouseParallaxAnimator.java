package net.diebuddies.physics.settings.ux;

import net.diebuddies.math.Math;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MouseParallaxAnimator extends Animator {
   protected float baseX;
   protected float baseY;
   protected float baseWidth;
   protected float baseHeight;
   protected float currentMouseX;
   protected float currentMouseY;
   protected float oldMouseX;
   protected float oldMouseY;
   protected float nextMouseX = Float.MAX_VALUE;
   protected float nextMouseY = Float.MAX_VALUE;
   protected boolean showBorder;
   protected float parallaxMultiplier;
   protected float mouseSmoothness;

   public MouseParallaxAnimator(float parallaxMultiplier, boolean showBorder) {
      this.showBorder = showBorder;
      this.parallaxMultiplier = parallaxMultiplier;
      this.mouseSmoothness = 0.15F;
   }

   public MouseParallaxAnimator() {
      this(0.1F, false);
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      if (this.nextMouseX == Float.MAX_VALUE) {
         this.currentMouseX = Math.clamp((float)mouseX, 0.0F, this.baseWidth);
         this.currentMouseY = Math.clamp((float)mouseY, 0.0F, this.baseHeight);
         this.oldMouseX = this.currentMouseX;
         this.oldMouseY = this.currentMouseY;
      }

      this.nextMouseX = Math.clamp((float)mouseX, 0.0F, this.baseWidth);
      this.nextMouseY = Math.clamp((float)mouseY, 0.0F, this.baseHeight);
      float lMouseX = org.joml.Math.lerp(this.oldMouseX, this.currentMouseX, renderPercent);
      float lMouseY = org.joml.Math.lerp(this.oldMouseY, this.currentMouseY, renderPercent);
      float offsetX = -Math.clamp(lMouseX / (this.baseWidth + this.baseX) * 2.0F - 1.0F, -1.0F, 1.0F);
      float offsetY = -Math.clamp(lMouseY / (this.baseHeight + this.baseY) * 2.0F - 1.0F, -1.0F, 1.0F);
      float offsetMultiplierX = this.parallaxMultiplier * this.baseWidth;
      float offsetMultiplierY = this.parallaxMultiplier * this.baseHeight;
      if (!this.showBorder) {
         float zoomX = this.baseX - offsetMultiplierX;
         float zoomY = this.baseY - offsetMultiplierY;
         float zoomWidth = this.baseWidth + offsetMultiplierX * 2.0F;
         float zoomHeight = this.baseHeight + offsetMultiplierY * 2.0F;
         animatable.setAnimX(zoomX + offsetX * offsetMultiplierX);
         animatable.setAnimY(zoomY + offsetY * offsetMultiplierY);
         animatable.setAnimWidth(zoomWidth);
         animatable.setAnimHeight(zoomHeight);
      } else {
         animatable.setAnimX(this.baseX + offsetX * offsetMultiplierX);
         animatable.setAnimY(this.baseY + offsetY * offsetMultiplierY);
      }

      return false;
   }

   @Override
   public void tick(Animatable animatable) {
      this.updatePositions();
      if (this.nextMouseX != Float.MAX_VALUE) {
         this.currentMouseX = org.joml.Math.lerp(this.currentMouseX, this.nextMouseX, this.mouseSmoothness);
         this.currentMouseY = org.joml.Math.lerp(this.currentMouseY, this.nextMouseY, this.mouseSmoothness);
      }
   }

   @Override
   public void init(Animatable animatable) {
      this.baseX = animatable.getAnimX();
      this.baseY = animatable.getAnimY();
      this.baseWidth = animatable.getAnimWidth();
      this.baseHeight = animatable.getAnimHeight();
      this.currentMouseX = this.baseWidth * 0.5F + this.baseX;
      this.currentMouseY = this.baseHeight * 0.5F + this.baseY;
      this.updatePositions();
   }

   private void updatePositions() {
      this.oldMouseX = this.currentMouseX;
      this.oldMouseY = this.currentMouseY;
   }

   public float getParallaxMultiplier() {
      return this.parallaxMultiplier;
   }

   public MouseParallaxAnimator setParallaxMultiplier(float parallaxMultiplier) {
      this.parallaxMultiplier = parallaxMultiplier;
      return this;
   }

   public float getMouseSmoothness() {
      return this.mouseSmoothness;
   }

   public MouseParallaxAnimator setMouseSmoothness(float mouseSmoothness) {
      this.mouseSmoothness = mouseSmoothness;
      return this;
   }
}
