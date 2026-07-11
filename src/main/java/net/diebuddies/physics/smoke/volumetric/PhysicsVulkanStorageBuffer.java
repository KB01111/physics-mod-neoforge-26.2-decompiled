package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBuffer.Usage;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vulkan.Destroyable;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanUtils;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;

final class PhysicsVulkanStorageBuffer extends GpuBuffer implements Destroyable {
   private final VulkanDevice device;
   private final long vkBuffer;
   private final long vmaAllocation;
   private final boolean hostVisible;
   private boolean closed;

   PhysicsVulkanStorageBuffer(VulkanDevice device, @Nullable Supplier<String> label, @Usage int usage, long size, int vkUsageFlags, boolean hostVisible) {
      super(usage, size);
      this.device = device;
      this.hostVisible = hostVisible;
      this.closed = false;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkBufferCreateInfo bufferCreateInfo = VkBufferCreateInfo.calloc(stack).sType$Default();
         bufferCreateInfo.size(size);
         bufferCreateInfo.usage(vkUsageFlags);
         bufferCreateInfo.sharingMode(0);
         bufferCreateInfo.pQueueFamilyIndices(null);
         VmaAllocationCreateInfo allocCreateInfo = VmaAllocationCreateInfo.calloc(stack);
         allocCreateInfo.usage(hostVisible ? 9 : 7);
         if (hostVisible) {
            allocCreateInfo.requiredFlags(6);
            allocCreateInfo.flags(1024);
         }

         LongBuffer bufferPtr = stack.callocLong(1);
         PointerBuffer allocPtr = stack.callocPointer(1);
         int result = Vma.vmaCreateBuffer(device.vma(), bufferCreateInfo, allocCreateInfo, bufferPtr, allocPtr, null);
         VulkanUtils.crashIfFailure((VulkanDevice)RenderSystem.getDevice().backend, result, "Failed to allocate smoke compute VkBuffer");
         this.vkBuffer = bufferPtr.get(0);
         this.vmaAllocation = allocPtr.get(0);
         if (label != null) {
            device.instance().debug().setObjectName(device.vkDevice(), 9, this.vkBuffer, label);
         }
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
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.createCommandEncoder().queueForDestroy(this);
      }
   }

   public void destroy() {
      Vma.vmaDestroyBuffer(this.device.vma(), this.vkBuffer, this.vmaAllocation);
   }

   public long vkBuffer() {
      return this.vkBuffer;
   }

   public void upload(ByteBuffer src) {
      if (!this.hostVisible) {
         throw new IllegalStateException("Tried to upload into a non-host-visible smoke compute buffer");
      } else if ((long)src.remaining() > this.size()) {
         throw new IllegalArgumentException("Smoke particle upload exceeds buffer size");
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         try {
            PointerBuffer pointer = stack.callocPointer(1);
            VulkanUtils.crashIfFailure(
               (VulkanDevice)RenderSystem.getDevice().backend,
               Vma.vmaMapMemory(this.device.vma(), this.vmaAllocation, pointer),
               "Failed to map smoke compute buffer"
            );

            try {
               ByteBuffer dst = MemoryUtil.memByteBuffer(pointer.get(0), (int)this.size());
               dst.position(0);
               ByteBuffer copy = src.duplicate();
               copy.position(0);
               dst.put(copy);
            } finally {
               Vma.vmaUnmapMemory(this.device.vma(), this.vmaAllocation);
            }
         } catch (Throwable var12) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var10) {
                  var12.addSuppressed(var10);
               }
            }

            throw var12;
         }

         if (stack != null) {
            stack.close();
         }
      }
   }

   public MappedView map(long offset, long length, boolean read, boolean write) {
      throw new RuntimeException("Mapping physics vulkan buffer not supported");
   }
}
