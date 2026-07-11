package net.diebuddies.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.jspecify.annotations.Nullable;

public final class SimpleColorRenderTarget {
   private final String label;
   private final GpuFormat format;
   private final int extraUsage;
   @Nullable
   private GpuTexture texture;
   @Nullable
   private GpuTextureView textureView;
   private int width;
   private int height;

   public SimpleColorRenderTarget(String label) {
      this(label, GpuFormat.RGBA8_UNORM);
   }

   public SimpleColorRenderTarget(String label, GpuFormat format) {
      this(label, format, 0);
   }

   public SimpleColorRenderTarget(String label, GpuFormat format, int extraUsage) {
      this.label = label;
      this.format = format;
      this.extraUsage = extraUsage;
   }

   public void ensureSize(int width, int height) {
      if (this.texture == null || this.width != width || this.height != height) {
         this.destroy();
         this.width = width;
         this.height = height;
         this.texture = RenderSystem.getDevice().createTexture(() -> this.label, 12 | this.extraUsage, this.format, width, height, 1, 1);
         this.textureView = RenderSystem.getDevice().createTextureView(this.texture);
      }
   }

   public GpuTexture texture() {
      if (this.texture == null) {
         throw new IllegalStateException("Render target has not been created yet");
      } else {
         return this.texture;
      }
   }

   public GpuTextureView textureView() {
      if (this.textureView == null) {
         throw new IllegalStateException("Render target view has not been created yet");
      } else {
         return this.textureView;
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public GpuFormat getFormat() {
      return this.format;
   }

   public void destroy() {
      if (this.textureView != null) {
         this.textureView.close();
         this.textureView = null;
      }

      if (this.texture != null) {
         this.texture.close();
         this.texture = null;
      }

      this.width = 0;
      this.height = 0;
   }
}
