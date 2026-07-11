package net.diebuddies.render.util;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlSampler;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.smoke.volumetric.GLCompat;
import net.diebuddies.render.GlStateManagerPhysics;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33C;
import org.lwjgl.system.MemoryUtil;

public class PhysicsRenderUtil {
   public static final int USAGE_STORAGE_IMAGE = 256;
   public static final int USAGE_TEXTURE_3D = 128;

   public static GpuTextureView load3DTexture(String filename, int width, int height, int depth) {
      byte[] rawData = readResourceOrFallback(filename, width, height, depth);
      byte[] rgbaData = expandRg8ToRgba8(rawData, width, height, depth);
      GpuDeviceBackend backend = RenderSystem.getDevice().backend;
      return backend instanceof VulkanDevice
         ? load3DTextureVulkan(filename, rgbaData, width, height, depth)
         : load3DTextureGl(filename, rgbaData, width, height, depth);
   }

   public static GpuTexture createTexture3D(String label, int usage, GpuFormat format, int width, int height, int depth, int mipLevels) {
      GpuDeviceBackend backend = RenderSystem.getDevice().backend;
      if (backend instanceof VulkanDevice vk) {
         return new VulkanGpuTexture(vk, usage | 128, label, format, width, height, depth, mipLevels);
      } else {
         int id = GlStateManager._genTexture();
         GlStateManagerPhysics._bindTexture(32879, id);

         int internalFormat = switch (format) {
            case RGBA8_UNORM -> '聘';
            case R32_UINT -> '舶';
            case R8_UNORM -> '舩';
            default -> throw new IllegalArgumentException("Unsupported 3D texture format for smoke volume: " + format);
         };
         GLCompat.texStorage3D(32879, mipLevels, internalFormat, width, height, depth);
         GL33C.glTexParameteri(32879, 33084, 0);
         GL33C.glTexParameteri(32879, 33085, Math.max(0, mipLevels - 1));
         GL33C.glTexParameteri(32879, 10241, 9728);
         GL33C.glTexParameteri(32879, 10240, 9728);
         GL33C.glTexParameteri(32879, 10242, 33071);
         GL33C.glTexParameteri(32879, 10243, 33071);
         GL33C.glTexParameteri(32879, 32882, 33071);
         GlStateManagerPhysics._bindTexture(32879, 0);
         return new GlTexture(usage | 128, label, format, width, height, depth, mipLevels, id, ((GlDevice)backend).frameBufferCache());
      }
   }

   public static GpuTextureView load3DTextureGl(String label, byte[] rgbaData, int width, int height, int depth) {
      int id = GlStateManager._genTexture();
      GlStateManagerPhysics._bindTexture(32879, id);
      GlStateManager._pixelStore(3314, 0);
      GlStateManager._pixelStore(3315, 0);
      GlStateManager._pixelStore(3316, 0);
      GlStateManager._pixelStore(3317, 4);
      ByteBuffer data = MemoryUtil.memAlloc(rgbaData.length);
      data.put(rgbaData);
      data.flip();
      GL32C.glTexImage3D(32879, 0, 32856, width, height, depth, 0, 6408, 5121, data);
      GlStateManager._bindTexture(0);
      MemoryUtil.memFree(data);
      GpuDeviceBackend backend = RenderSystem.getDevice().backend;
      GpuTexture texture = new GlTexture(132, label, GpuFormat.RGBA8_UNORM, width, height, depth, 1, id, ((GlDevice)backend).frameBufferCache());
      return RenderSystem.getDevice().createTextureView(texture);
   }

   private static GpuTextureView load3DTextureVulkan(String label, byte[] rgbaData, int width, int height, int depth) {
      GpuTexture texture = new VulkanGpuTexture((VulkanDevice)RenderSystem.getDevice().backend, 133, label, GpuFormat.RGBA8_UNORM, width, height, depth, 1);
      CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
      int sliceSize = width * height * 4;

      for (int z = 0; z < depth; z++) {
         ByteBuffer slice = MemoryUtil.memAlloc(sliceSize);
         slice.put(rgbaData, z * sliceSize, sliceSize);
         slice.flip();
         encoder.writeToTexture(texture, slice, 0, z, 0, 0, width, height);
         MemoryUtil.memFree(slice);
      }

      encoder.submit();
      return RenderSystem.getDevice().createTextureView(texture);
   }

   public static GpuSampler createSampler3D(
      AddressMode addressModeU,
      AddressMode addressModeV,
      AddressMode addressModeW,
      FilterMode minFilter,
      FilterMode magFilter,
      int maxAnisotropy,
      OptionalDouble maxLod
   ) {
      GpuDeviceBackend backend = RenderSystem.getDevice().backend;
      return (GpuSampler)(backend instanceof VulkanDevice
         ? Vulkan3DSamplerState.createSampler3D(RenderSystem.getDevice(), addressModeU, addressModeV, addressModeW, minFilter, magFilter, maxAnisotropy, maxLod)
         : new PhysicsRenderUtil.GlSampler3D(addressModeU, addressModeV, addressModeW, minFilter, magFilter, maxAnisotropy, maxLod));
   }

   private static byte[] readResourceOrFallback(String filename, int width, int height, int depth) {
      try {
         byte[] var5;
         try (InputStream src = PhysicsMod.class.getClassLoader().getResourceAsStream(filename)) {
            if (src == null) {
               throw new IOException("Missing resource: " + filename);
            }

            var5 = src.readAllBytes();
         }

         return var5;
      } catch (IOException var9) {
         var9.printStackTrace();
         return new byte[width * height * depth * 2];
      }
   }

   private static byte[] expandRg8ToRgba8(byte[] rawData, int width, int height, int depth) {
      int voxelCount = width * height * depth;
      byte[] rgba = new byte[voxelCount * 4];

      for (int i = 0; i < voxelCount; i++) {
         int src = i * 2;
         int dst = i * 4;
         byte r = src < rawData.length ? rawData[src] : 0;
         byte g = src + 1 < rawData.length ? rawData[src + 1] : 0;
         rgba[dst] = r;
         rgba[dst + 1] = g;
         rgba[dst + 2] = 0;
         rgba[dst + 3] = -1;
      }

      return rgba;
   }

   public static class GlSampler3D extends GlSampler {
      public GlSampler3D(
         AddressMode addressModeU,
         AddressMode addressModeV,
         AddressMode addressModeW,
         FilterMode minFilter,
         FilterMode magFilter,
         int maxAnisotropy,
         OptionalDouble maxLod
      ) {
         super(addressModeU, addressModeV, minFilter, magFilter, maxAnisotropy, maxLod);
         GL33C.glSamplerParameteri(this.getId(), 32882, GlConst.toGl(addressModeW));
      }
   }
}
