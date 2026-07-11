package net.diebuddies.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vulkan.VulkanCommandEncoder;
import com.mojang.blaze3d.vulkan.VulkanConst;
import com.mojang.blaze3d.vulkan.VulkanGpuBuffer;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import com.mojang.blaze3d.vulkan.VulkanTransientMemory;
import java.nio.ByteBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferImageCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkImageSubresourceLayers;
import org.lwjgl.vulkan.VkBufferImageCopy.Buffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({VulkanCommandEncoder.class})
public abstract class MixinVulkanCommandEncoder {
   @Shadow
   @Final
   private VulkanTransientMemory transientMemory;

   @Shadow
   public abstract VkCommandBuffer commandBuffer();

   @Shadow
   public abstract void memoryBarrier(MemoryStack var1);

   @Inject(
      method = {"writeToTexture"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void physics$write3DTexture(
      GpuTexture destination, ByteBuffer source, int mipLevel, int depthOrLayer, int destX, int destY, int width, int height, CallbackInfo ci
   ) {
      if ((destination.usage() & 128) != 0) {
         GpuBufferSlice stagingBuffer = this.transientMemory.uploadStaging(source, 1L, 16);
         MemoryStack stack = MemoryStack.stackPush();

         try {
            Buffer region = VkBufferImageCopy.calloc(1, stack);
            region.bufferOffset(stagingBuffer.offset());
            region.bufferRowLength(width);
            region.bufferImageHeight(height);
            VkImageSubresourceLayers subresource = region.imageSubresource();
            subresource.aspectMask(VulkanConst.formatAspectMask(destination.getFormat()));
            subresource.mipLevel(mipLevel);
            subresource.baseArrayLayer(0);
            subresource.layerCount(1);
            region.imageOffset().set(destX, destY, depthOrLayer);
            region.imageExtent().set(width, height, 1);
            VK12.vkCmdCopyBufferToImage(
               this.commandBuffer(), ((VulkanGpuBuffer)stagingBuffer.buffer()).vkBuffer(), ((VulkanGpuTexture)destination).vkImage(), 1, region
            );
            this.memoryBarrier(stack);
         } catch (Throwable var15) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var14) {
                  var15.addSuppressed(var14);
               }
            }

            throw var15;
         }

         if (stack != null) {
            stack.close();
         }

         ci.cancel();
      }
   }
}
