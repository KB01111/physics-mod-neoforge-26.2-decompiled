package net.diebuddies.mixins;

import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import com.mojang.blaze3d.vulkan.VulkanGpuTextureView;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkImageViewCreateInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({VulkanGpuTextureView.class})
public abstract class MixinVulkanGpuTextureView {
   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageViewCreateInfo;viewType(I)Lorg/lwjgl/vulkan/VkImageViewCreateInfo;"
      )
   )
   private VkImageViewCreateInfo physics$setViewType(
      VkImageViewCreateInfo createInfo, int originalViewType, VulkanDevice device, VulkanGpuTexture texture, int baseMipLevel, int mipLevels
   ) {
      return is3D(texture) ? createInfo.viewType(2) : createInfo.viewType(originalViewType);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageSubresourceRange;layerCount(I)Lorg/lwjgl/vulkan/VkImageSubresourceRange;"
      )
   )
   private VkImageSubresourceRange physics$setViewLayerCount(
      VkImageSubresourceRange range, int originalLayerCount, VulkanDevice device, VulkanGpuTexture texture, int baseMipLevel, int mipLevels
   ) {
      return is3D(texture) ? range.layerCount(1) : range.layerCount(originalLayerCount);
   }

   private static boolean is3D(VulkanGpuTexture texture) {
      return (texture.usage() & 128) != 0;
   }
}
