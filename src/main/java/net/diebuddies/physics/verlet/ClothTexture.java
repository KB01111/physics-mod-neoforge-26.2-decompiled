package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.renderer.texture.AbstractTexture;

public class ClothTexture extends AbstractTexture {
   public ClothTexture(NativeImage img, String label) {
      GpuDevice device = RenderSystem.getDevice();
      this.texture = device.createTexture(label, 5, GpuFormat.RGBA8_UNORM, img.getWidth(), img.getHeight(), 1, 1);
      device.createCommandEncoder().writeToTexture(this.texture, img);
      this.textureView = device.createTextureView(this.texture);
      this.sampler = RenderSystem.getSamplerCache().getSampler(AddressMode.REPEAT, AddressMode.REPEAT, FilterMode.NEAREST, FilterMode.NEAREST, false);
   }
}
