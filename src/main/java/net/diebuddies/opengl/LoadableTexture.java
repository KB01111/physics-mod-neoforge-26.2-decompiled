package net.diebuddies.opengl;

import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class LoadableTexture extends AbstractTexture {
   private boolean loaded = false;

   public boolean isLoaded() {
      return this.loaded;
   }

   public void setTexture(GpuTexture texture) {
      this.texture = texture;
      this.loaded = true;
   }

   public void setTextureView(GpuTextureView textureView) {
      this.textureView = textureView;
   }

   public void setSampler(GpuSampler sampler) {
      this.sampler = sampler;
   }
}
