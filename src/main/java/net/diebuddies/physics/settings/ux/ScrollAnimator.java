package net.diebuddies.physics.settings.ux;

import it.unimi.dsi.fastutil.objects.Object2FloatLinkedOpenHashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import org.joml.Math;

public class ScrollAnimator extends Animator {
   private Object2FloatLinkedOpenHashMap<Animatable> baseOffsets;
   private float scrollOffset;
   private float oldScrollOffset;
   private float currentScrollOffset;
   private float minOffset;
   private float maxOffset;
   private float scrollSpeed;
   private float animationSpeed;
   private float screenHeight;
   private float arrowXPos;
   private float time;
   private int hoveredColor = BaseColors.HIGHLIGHT_COLOR;

   public ScrollAnimator(float minOffset, float maxOffset, float arrowXPos, float screenHeight) {
      this.animationSpeed = 0.5F;
      this.scrollSpeed = 40.0F;
      this.scrollOffset = 0.0F;
      this.minOffset = minOffset;
      this.maxOffset = maxOffset;
      this.screenHeight = screenHeight;
      this.arrowXPos = arrowXPos;
      this.baseOffsets = new Object2FloatLinkedOpenHashMap();
   }

   @Override
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      this.baseOffsets.putIfAbsent(animatable, animatable.getAnimY());
      float baseY = this.baseOffsets.getFloat(animatable);
      animatable.setAnimY(baseY + Math.lerp(this.oldScrollOffset, this.currentScrollOffset, renderPercent));
      if (animatable == this.baseOffsets.lastKey()) {
         int shadowOffset = 1;
         this.time += delta;
         float width = 12.0F;
         float height = 12.0F;
         float x = this.arrowXPos - width * 0.5F + (float)shadowOffset;
         float y = this.screenHeight * 0.95F - height * 0.5F + (float)java.lang.Math.sin((double)this.time * 6.0) + (float)shadowOffset;
         float depth = 130.0F;
         if (this.scrollOffset - 0.01F >= this.minOffset) {
            int color = ARGB.color(255, 0, 0, 0);
            Animator.drawRect(guiGraphics, GUIResources.ARROW, x, y, width, height, depth, 0.0F, 1.0F, 0.0F, 1.0F, color);
            color = ARGB.color(255, 255, 255, 255);
            if (this.isOverBottomArrow((double)mouseX, (double)mouseY)) {
               color = this.hoveredColor;
            }

            x -= (float)shadowOffset;
            y -= (float)shadowOffset;
            Animator.drawRect(guiGraphics, GUIResources.ARROW, x, y, width, height, depth, 0.0F, 1.0F, 0.0F, 1.0F, color);
         }

         if (this.scrollOffset + 0.01F <= this.maxOffset) {
            int color = ARGB.color(255, 0, 0, 0);
            x = this.arrowXPos - width * 0.5F + (float)shadowOffset;
            y = this.screenHeight * 0.05F - height / 2.0F + (float)java.lang.Math.cos((double)this.time * 6.0) + (float)shadowOffset;
            Animator.drawRect(guiGraphics, GUIResources.ARROW, x, y, width, height, depth, 0.0F, 1.0F, 1.0F, 0.0F, color);
            color = ARGB.color(255, 255, 255, 255);
            if (this.isOverTopArrow((double)mouseX, (double)mouseY)) {
               color = this.hoveredColor;
            }

            x -= (float)shadowOffset;
            y -= (float)shadowOffset;
            Animator.drawRect(guiGraphics, GUIResources.ARROW, x, y, width, height, depth, 0.0F, 1.0F, 1.0F, 0.0F, color);
         }
      }

      return false;
   }

   public boolean mouseClicked(double mouseX, double mouseY, int mouseKey) {
      if (mouseKey == 0) {
         if (this.isOverBottomArrow(mouseX, mouseY)) {
            this.scroll(-2.0F);
            return true;
         }

         if (this.isOverTopArrow(mouseX, mouseY)) {
            this.scroll(2.0F);
            return true;
         }
      }

      return false;
   }

   private boolean isOverBottomArrow(double mouseX, double mouseY) {
      float width = 24.0F;
      float height = 16.0F;
      float x = this.arrowXPos - width * 0.5F;
      float y = this.screenHeight * 0.95F - height * 0.5F + (float)java.lang.Math.sin((double)this.time * 6.0);
      return mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)(x + width) && mouseY < (double)(y + height);
   }

   private boolean isOverTopArrow(double mouseX, double mouseY) {
      float width = 24.0F;
      float height = 16.0F;
      float x = this.arrowXPos - width * 0.5F;
      float y = this.screenHeight * 0.05F - height * 0.5F + (float)java.lang.Math.sin((double)this.time * 6.0);
      return mouseX >= (double)x && mouseY >= (double)y && mouseX < (double)(x + width) && mouseY < (double)(y + height);
   }

   @Override
   public void tick(Animatable animatable) {
      if (this.baseOffsets.size() == 0 || animatable == this.baseOffsets.keySet().iterator().next()) {
         this.oldScrollOffset = this.currentScrollOffset;
         this.currentScrollOffset = Math.lerp(this.currentScrollOffset, this.scrollOffset, this.animationSpeed);
      }
   }

   public void scroll(float scroll) {
      this.scrollOffset = net.diebuddies.math.Math.clamp(this.scrollOffset + scroll * this.scrollSpeed, this.minOffset, this.maxOffset);
   }

   public float getScrollSpeed() {
      return this.scrollSpeed;
   }

   public void setScrollSpeed(float scrollSpeed) {
      this.scrollSpeed = scrollSpeed;
   }

   public void setMinOffset(float minOffset) {
      this.minOffset = minOffset;
   }

   public void setMaxOffset(float maxOffset) {
      this.maxOffset = maxOffset;
   }

   public float getMinOffset() {
      return this.minOffset;
   }

   public float getMaxOffset() {
      return this.maxOffset;
   }

   public void setAnimationSpeed(float animationSpeed) {
      this.animationSpeed = animationSpeed;
   }

   public float getAnimationSpeed() {
      return this.animationSpeed;
   }
}
