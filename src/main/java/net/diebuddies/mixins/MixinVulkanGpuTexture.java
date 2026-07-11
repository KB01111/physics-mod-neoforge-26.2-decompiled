package net.diebuddies.mixins;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkImageCreateInfo;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({VulkanGpuTexture.class})
public abstract class MixinVulkanGpuTexture {
   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageCreateInfo;imageType(I)Lorg/lwjgl/vulkan/VkImageCreateInfo;"
      )
   )
   private VkImageCreateInfo physics$setImageType(
      VkImageCreateInfo createInfo,
      int originalType,
      VulkanDevice device,
      int usage,
      String label,
      GpuFormat format,
      int width,
      int height,
      int depthOrLayers,
      int mipLevels
   ) {
      return createInfo.imageType(is3D(usage) ? 2 : originalType);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkExtent3D;set(III)Lorg/lwjgl/vulkan/VkExtent3D;"
      )
   )
   private VkExtent3D physics$setExtent(
      VkExtent3D extent,
      int originalWidth,
      int originalHeight,
      int originalDepth,
      VulkanDevice device,
      int usage,
      String label,
      GpuFormat format,
      int width,
      int height,
      int depthOrLayers,
      int mipLevels
   ) {
      return is3D(usage) ? extent.set(width, height, depthOrLayers) : extent.set(originalWidth, originalHeight, originalDepth);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageCreateInfo;arrayLayers(I)Lorg/lwjgl/vulkan/VkImageCreateInfo;"
      )
   )
   private VkImageCreateInfo physics$setArrayLayers(
      VkImageCreateInfo createInfo,
      int originalLayers,
      VulkanDevice device,
      int usage,
      String label,
      GpuFormat format,
      int width,
      int height,
      int depthOrLayers,
      int mipLevels
   ) {
      return createInfo.arrayLayers(is3D(usage) ? 1 : originalLayers);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageCreateInfo;usage(I)Lorg/lwjgl/vulkan/VkImageCreateInfo;"
      )
   )
   private VkImageCreateInfo physics$setUsage(
      VkImageCreateInfo createInfo,
      int originalUsage,
      VulkanDevice device,
      int usage,
      String label,
      GpuFormat format,
      int width,
      int height,
      int depthOrLayers,
      int mipLevels
   ) {
      return (usage & 256) != 0 ? createInfo.usage(originalUsage | 8) : createInfo.usage(originalUsage);
   }

   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkImageSubresourceRange;layerCount(I)Lorg/lwjgl/vulkan/VkImageSubresourceRange;"
      )
   )
   private VkImageSubresourceRange physics$setInitBarrierLayerCount(
      VkImageSubresourceRange range,
      int originalLayerCount,
      VulkanDevice device,
      int usage,
      String label,
      GpuFormat format,
      int width,
      int height,
      int depthOrLayers,
      int mipLevels
   ) {
      return range.layerCount(is3D(usage) ? 1 : originalLayerCount);
   }

   private static boolean is3D(int usage) {
      return (usage & 128) != 0;
   }
}
