package net.diebuddies.physics.settings.ux;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2f;

public abstract class Animator {
   public boolean extraxtRenderState(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent, float delta) {
      return false;
   }

   public boolean extraxtRenderStateWithToolTip(Animatable animatable, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float renderPercent) {
      return false;
   }

   public void tick(Animatable animatable) {
   }

   public void init(Animatable animatable) {
   }

   public static final void drawRect(GuiGraphicsExtractor guiGraphics, float x, float y, float width, float height, float depth, int color) {
      guiGraphics.fill((int)x, (int)y, (int)(x + width), (int)(y + height), color);
   }

   public static final void drawLine(GuiGraphicsExtractor guiGraphics, float x1, float y1, float x2, float y2, float depth, int color) {
      guiGraphics.fill((int)x1, (int)y1, (int)x2 + 1, (int)y2 + 1, color);
   }

   public static final void drawRect(
      GuiGraphicsExtractor guiGraphics,
      Identifier image,
      float x,
      float y,
      float width,
      float height,
      float depth,
      float umin,
      float umax,
      float vmin,
      float vmax,
      int color
   ) {
      AbstractTexture texture = Minecraft.getInstance().getTextureManager().getTexture(image);
      GpuTextureView gpuTexture = texture.getTextureView();
      GpuSampler gpuSampler = texture.getSampler();
      drawRect(guiGraphics, gpuTexture, gpuSampler, x, y, width, height, depth, umin, umax, vmin, vmax, color);
   }

   public static final void drawRect(
      GuiGraphicsExtractor guiGraphics,
      GpuTextureView image,
      GpuSampler sampler,
      float x,
      float y,
      float width,
      float height,
      float depth,
      float umin,
      float umax,
      float vmin,
      float vmax,
      int color
   ) {
      submitBlit(guiGraphics, RenderPipelines.GUI_TEXTURED, image, sampler, x, y, x + width, y + height, umin, umax, vmin, vmax, color);
   }

   private static void submitBlit(
      GuiGraphicsExtractor guiGraphics,
      RenderPipeline renderPipeline,
      GpuTextureView gpuTexture,
      GpuSampler sampler,
      float i,
      float j,
      float k,
      float l,
      float f,
      float g,
      float h,
      float m,
      int n
   ) {
      guiGraphics.guiRenderState
         .addGuiElement(
            new BlitFloatRenderState(
               renderPipeline,
               TextureSetup.singleTexture(gpuTexture, sampler),
               new Matrix3x2f(guiGraphics.pose()),
               i,
               j,
               k,
               l,
               f,
               g,
               h,
               m,
               n,
               guiGraphics.scissorStack.peek()
            )
         );
   }

   public static final void drawText(GuiGraphicsExtractor guiGraphics, Font font, FormattedCharSequence formattedCharSequence, float x, float y) {
      guiGraphics.text(font, formattedCharSequence, (int)x + 1, (int)y + 1, ARGB.color(255, 0, 0, 0), false);
      guiGraphics.text(font, formattedCharSequence, (int)x, (int)y, ARGB.color(255, 255, 255, 255), false);
   }
}
