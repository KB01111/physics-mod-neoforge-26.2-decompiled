package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.diebuddies.opengl.LoadableTexture;
import net.diebuddies.opengl.ResourceManager;
import net.diebuddies.physics.liquid.SimpleTextureDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class ImageElement extends AbstractWidget {
   private ResourceManager resourceManager;
   private float textureWidth;
   private float textureHeight;
   private float textureAspectRatio;
   private LoadableTexture textureLoadable;
   private Identifier imageLocation;
   private boolean keepAspectRatio;
   private boolean slide;
   private float time;

   public ImageElement(Identifier imageLocation, float x, float y, float width, float height, boolean keepAspectRatio) {
      super((int)x, (int)y, (int)width, (int)height, null);
      this.imageLocation = imageLocation;
      this.keepAspectRatio = keepAspectRatio;
      TextureManager textureManager = Minecraft.getInstance().getTextureManager();
      if (textureManager.getTexture(imageLocation) instanceof SimpleTextureDimension textureDimension) {
         this.textureWidth = (float)textureDimension.getWidth();
         this.textureHeight = (float)textureDimension.getHeight();
      } else {
         this.textureWidth = width;
         this.textureHeight = height;
      }

      this.textureAspectRatio = this.textureWidth / this.textureHeight;
   }

   public ImageElement(ResourceManager resourceManager, Identifier imageLocation, float x, float y, float width, float height, boolean keepAspectRatio) {
      super((int)x, (int)y, (int)width, (int)height, null);
      this.resourceManager = resourceManager;
      this.imageLocation = imageLocation;
      this.keepAspectRatio = keepAspectRatio;
   }

   public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.time += delta / 20.0F;
      Animatable ths = (Animatable)this;
      float umin = 0.0F;
      float umax = 1.0F;
      float vmin = 0.0F;
      float vmax = 1.0F;
      if (this.keepAspectRatio) {
         float panelAspectRatio = ths.getAnimWidth() / ths.getAnimHeight();
         if (this.textureAspectRatio < panelAspectRatio) {
            float multiplier = panelAspectRatio / this.textureAspectRatio;
            vmax /= multiplier;
            float half = (1.0F - vmax) * 0.5F;
            vmax += half;
            vmin += half;
         } else {
            float multiplier = panelAspectRatio / this.textureAspectRatio;
            umax *= multiplier;
            float half = (1.0F - umax) * 0.5F;
            umax += half;
            umin += half;
         }
      }

      float x = ths.getAnimX();
      float y = ths.getAnimY();
      float width = ths.getAnimWidth();
      float height = ths.getAnimHeight();
      float depth = ths.getAnimDepth();
      AbstractTexture abstractTexture;
      if (this.resourceManager != null) {
         abstractTexture = this.textureLoadable;
      } else {
         TextureManager textureManager = Minecraft.getInstance().getTextureManager();
         abstractTexture = textureManager.getTexture(this.getImageLocation());
      }

      GpuTextureView textureView = abstractTexture.getTextureView();
      GpuSampler sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR, false);
      int color = ARGB.colorFromFloat(ths.getAnimAlpha(), ths.getAnimRed(), ths.getAnimGreen(), ths.getAnimBlue());
      if (this.slide) {
         double curveFunction = Math.pow(Math.cos(Math.pow(Math.sin((double)this.time * 0.7), 3.0)), 10.0);
         double perc = (curveFunction - 0.0021202) / 0.9978798;
         Animator.drawRect(guiGraphics, textureView, sampler, x, y, width * (float)perc, height, depth, umin, umax * (float)perc, vmin, vmax, color);
      } else {
         Animator.drawRect(guiGraphics, textureView, sampler, x, y, width, height, depth, umin, umax, vmin, vmax, color);
      }
   }

   public Identifier getImageLocation() {
      return this.imageLocation;
   }

   public void setImageLocation(Identifier imageLocation) {
      this.imageLocation = imageLocation;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
   }

   public void destroyLoadedTexture() {
      if (this.resourceManager != null) {
         this.resourceManager.destroyTexture(this.imageLocation);
         this.textureLoadable = null;
      }
   }

   public boolean loadImage() {
      if (this.resourceManager == null) {
         return true;
      } else if (this.textureLoadable == null) {
         this.textureLoadable = this.resourceManager.getTexture(this.imageLocation);
         return false;
      } else {
         this.resourceManager.update();
         if (!this.textureLoadable.isLoaded()) {
            return false;
         } else {
            this.textureWidth = (float)this.textureLoadable.getTexture().getWidth(0);
            this.textureHeight = (float)this.textureLoadable.getTexture().getHeight(0);
            this.textureAspectRatio = this.textureWidth / this.textureHeight;
            return true;
         }
      }
   }

   public ImageElement enableSlide() {
      this.slide = true;
      return this;
   }
}
