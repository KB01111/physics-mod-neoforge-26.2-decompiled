package net.diebuddies.render;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.jspecify.annotations.Nullable;

public final class SimpleColorDepthRenderTarget {
   private final String label;
   private final GpuFormat colorFormat;
   @Nullable
   private GpuTexture colorTexture;
   @Nullable
   private GpuTextureView colorView;
   @Nullable
   private GpuTexture depthTexture;
   @Nullable
   private GpuTextureView depthView;
   private int width;
   private int height;

   public SimpleColorDepthRenderTarget(String label, GpuFormat colorFormat) {
      this.label = label;
      this.colorFormat = colorFormat;
   }

   public void ensureSize(int width, int height, GpuFormat depthFormat) {
      if (this.colorTexture == null || this.depthTexture == null || this.width != width || this.height != height) {
         this.destroy();
         this.width = width;
         this.height = height;
         this.colorTexture = RenderSystem.getDevice().createTexture(() -> this.label + " Color", 12, this.colorFormat, width, height, 1, 1);
         this.colorView = RenderSystem.getDevice().createTextureView(this.colorTexture);
         this.depthTexture = RenderSystem.getDevice().createTexture(() -> this.label + " Depth", 13, depthFormat, width, height, 1, 1);
         this.depthView = RenderSystem.getDevice().createTextureView(this.depthTexture);
      }
   }

   public GpuTexture colorTexture() {
      if (this.colorTexture == null) {
         throw new IllegalStateException("Color texture has not been created yet");
      } else {
         return this.colorTexture;
      }
   }

   public GpuTextureView colorView() {
      if (this.colorView == null) {
         throw new IllegalStateException("Color view has not been created yet");
      } else {
         return this.colorView;
      }
   }

   public GpuTexture depthTexture() {
      if (this.depthTexture == null) {
         throw new IllegalStateException("Depth texture has not been created yet");
      } else {
         return this.depthTexture;
      }
   }

   public GpuTextureView depthView() {
      if (this.depthView == null) {
         throw new IllegalStateException("Depth view has not been created yet");
      } else {
         return this.depthView;
      }
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void destroy() {
      if (this.colorView != null) {
         this.colorView.close();
         this.colorView = null;
      }

      if (this.depthView != null) {
         this.depthView.close();
         this.depthView = null;
      }

      if (this.colorTexture != null) {
         this.colorTexture.close();
         this.colorTexture = null;
      }

      if (this.depthTexture != null) {
         this.depthTexture.close();
         this.depthTexture = null;
      }

      this.width = 0;
      this.height = 0;
   }
}
