package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanCommandEncoder;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuBuffer;
import com.mojang.blaze3d.vulkan.VulkanGpuSampler;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import com.mojang.blaze3d.vulkan.VulkanGpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.nio.charset.StandardCharsets;
import java.util.OptionalDouble;
import net.diebuddies.mixins.VulkanCommandEncoderAccessor;
import net.diebuddies.physics.smoke.SmokeColorConfig;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkBufferMemoryBarrier;
import org.lwjgl.vulkan.VkClearColorValue;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkComputePipelineCreateInfo;
import org.lwjgl.vulkan.VkDescriptorBufferInfo;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkImageSubresourceRange;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding.Buffer;

final class VulkanSmokeVolumeBackend extends AbstractSmokeVolumeBackend {
   private static final long MIN_PARTICLE_BYTES = 8192L;
   private static final int BRICK_SHIFT = 4;
   private static final int NOISE_SHIFT = 2;
   private static final float NOISE_MIN_MUL = 0.2F;
   private static final float NOISE_MAX_MUL = 0.8F;
   private static final int PUSH_CONSTANT_BYTES = 128;
   private final VulkanDevice vkDevice;
   private final VulkanGpuSampler lightmapSampler;
   private long descriptorPool;
   private final long descriptorSetLayout;
   private long[] descriptorSets;
   private int descriptorSetCapacity;
   private final long pipelineLayout;
   private final long clearShaderModule;
   private final long splatShaderModule;
   private final long resolveShaderModule;
   private final long clearPipeline;
   private final long splatPipeline;
   private final long resolvePipeline;
   private PhysicsVulkanStorageBuffer particleSsbo;
   private long particleCapacity;
   private PhysicsVulkanStorageBuffer metaSsbo;
   private long metaCapacity;

   VulkanSmokeVolumeBackend(GpuDevice device, VulkanDevice vkDevice) {
      super(device);
      this.vkDevice = vkDevice;
      this.lightmapSampler = (VulkanGpuSampler)device.createSampler(
         AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, FilterMode.LINEAR, FilterMode.LINEAR, 1, OptionalDouble.empty()
      );
      this.particleSsbo = null;
      this.particleCapacity = 0L;
      this.metaSsbo = null;
      this.metaCapacity = 0L;
      MemoryStack stack = MemoryStack.stackPush();

      try {
         Buffer bindings = VkDescriptorSetLayoutBinding.calloc(8, stack);
         ((VkDescriptorSetLayoutBinding)bindings.get(0)).binding(0).descriptorType(7).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(1)).binding(1).descriptorType(7).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(2)).binding(2).descriptorType(3).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(3)).binding(3).descriptorType(3).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(4)).binding(4).descriptorType(3).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(5)).binding(5).descriptorType(3).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(6)).binding(6).descriptorType(3).descriptorCount(1).stageFlags(32);
         ((VkDescriptorSetLayoutBinding)bindings.get(7)).binding(7).descriptorType(1).descriptorCount(1).stageFlags(32);
         VkDescriptorSetLayoutCreateInfo layoutInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack).sType$Default().pBindings(bindings);
         LongBuffer ptr = stack.callocLong(1);
         VulkanUtils.crashIfFailure(
            (VulkanDevice)RenderSystem.getDevice().backend,
            VK12.vkCreateDescriptorSetLayout(vkDevice.vkDevice(), layoutInfo, null, ptr),
            "Failed to create smoke descriptor set layout"
         );
         this.descriptorSetLayout = ptr.get(0);
         this.descriptorPool = 0L;
         this.descriptorSets = new long[0];
         this.descriptorSetCapacity = 0;
         org.lwjgl.vulkan.VkPushConstantRange.Buffer pushRange = VkPushConstantRange.calloc(1, stack);
         ((VkPushConstantRange)pushRange.get(0)).stageFlags(32).offset(0).size(128);
         VkPipelineLayoutCreateInfo pipelineLayoutInfo = VkPipelineLayoutCreateInfo.calloc(stack)
            .sType$Default()
            .pSetLayouts(stack.longs(this.descriptorSetLayout))
            .pPushConstantRanges(pushRange);
         VulkanUtils.crashIfFailure(
            (VulkanDevice)RenderSystem.getDevice().backend,
            VK12.vkCreatePipelineLayout(vkDevice.vkDevice(), pipelineLayoutInfo, null, ptr),
            "Failed to create smoke pipeline layout"
         );
         this.pipelineLayout = ptr.get(0);
         this.clearShaderModule = this.createShaderModule(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_clear_dirty_vk.comp"), "smoke_volume_clear_dirty_vk.comp"
         );
         this.splatShaderModule = this.createShaderModule(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_splat_vk.comp"), "smoke_volume_splat_vk.comp"
         );
         this.resolveShaderModule = this.createShaderModule(
            load("/assets/physicsmod/shaders/core/raymarch/smoke_volume_resolve_vk.comp"), "smoke_volume_resolve_vk.comp"
         );
         this.clearPipeline = this.createPipeline(stack, this.clearShaderModule);
         this.splatPipeline = this.createPipeline(stack, this.splatShaderModule);
         this.resolvePipeline = this.createPipeline(stack, this.resolveShaderModule);
      } catch (Throwable var10) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var9) {
               var10.addSuppressed(var9);
            }
         }

         throw var10;
      }

      if (stack != null) {
         stack.close();
      }
   }

   @Override
   public void buildVolumes(
      ByteBuffer particleData,
      int particleCount,
      SmokeVolumeTargets targets,
      GpuTexture lightmapTexture,
      GpuTextureView lightmapView,
      int cascadeCount,
      Vector3f[] boundsMin,
      Vector3f[] volumeSize,
      int gridX,
      int gridY,
      int gridZ
   ) {
      if (particleCount > 0 && particleData != null) {
         this.ensureParticleBuffer(particleData.remaining());
         this.ensureMetaBuffer(targets.cascadeMetaBuffer.size());
         this.ensureDescriptorSets(cascadeCount);
         this.particleSsbo.upload(particleData);
         boolean hasLightMap = true;
         VulkanGpuTextureView vkLightmapView = (VulkanGpuTextureView)lightmapView;

         for (int c = 0; c < cascadeCount; c++) {
            this.updateDescriptorSet(
               this.descriptorSets[c],
               this.particleSsbo,
               this.metaSsbo,
               (VulkanGpuTextureView)targets.densityAccViews[c],
               (VulkanGpuTextureView)targets.occupancyViews[c],
               (VulkanGpuTextureView)targets.lightAccViews[c],
               (VulkanGpuTextureView)targets.densityViews[c],
               (VulkanGpuTextureView)targets.lightViews[c],
               vkLightmapView
            );
         }

         VulkanCommandEncoder encoder = this.commandEncoder();
         VkCommandBuffer cmd = commandBuffer(encoder);
         this.bufferBarrier(cmd, 16384, 2048, 16384, 32, this.particleSsbo);
         if (!this.volumeDataInitialized) {
            this.clearAllVolumes(cmd, targets, cascadeCount);
            this.clearMeta(cmd, this.metaSsbo);
            this.barrierImages(cmd, targets, cascadeCount, 2048, 96);
            this.bufferBarrier(cmd, 4096, 2048, 4096, 96, this.metaSsbo);
            this.volumeDataInitialized = true;
         } else {
            this.clearOccupancy(cmd, targets, cascadeCount);
            this.barrierImages(cmd, targets, cascadeCount, 2048, 96);

            for (int c = 0; c < cascadeCount; c++) {
               VK12.vkCmdBindPipeline(cmd, 1, this.clearPipeline);
               this.bindDescriptorSet(cmd, this.descriptorSets[c]);
               this.pushParams(cmd, c, 0, boundsMin[c], volumeSize[c], gridX, gridY, gridZ, false);
               VK12.vkCmdDispatch(cmd, (gridX + 7) / 8, (gridY + 7) / 8, (gridZ + 3) / 4);
            }

            this.barrierImages(cmd, targets, cascadeCount, 2048, 96);
            this.clearMeta(cmd, this.metaSsbo);
            this.bufferBarrier(cmd, 4096, 2048, 4096, 96, this.metaSsbo);
         }

         int groups = Math.max(1, (particleCount + 63) / 64);

         for (int c = 0; c < cascadeCount; c++) {
            VK12.vkCmdBindPipeline(cmd, 1, this.splatPipeline);
            this.bindDescriptorSet(cmd, this.descriptorSets[c]);
            this.pushParams(cmd, c, particleCount, boundsMin[c], volumeSize[c], gridX, gridY, gridZ, hasLightMap);
            VK12.vkCmdDispatch(cmd, groups, 1, 1);
         }

         this.barrierImages(cmd, targets, cascadeCount, 2048, 96);
         this.bufferBarrier(cmd, 2048, 2048, 96, 96, this.metaSsbo);

         for (int c = 0; c < cascadeCount; c++) {
            VK12.vkCmdBindPipeline(cmd, 1, this.resolvePipeline);
            this.bindDescriptorSet(cmd, this.descriptorSets[c]);
            this.pushParams(cmd, c, 0, boundsMin[c], volumeSize[c], gridX, gridY, gridZ, false);
            VK12.vkCmdDispatch(cmd, (gridX + 7) / 8, (gridY + 7) / 8, (gridZ + 3) / 4);
         }

         this.barrierImages(cmd, targets, cascadeCount, 128, 32);
         this.copyMetaToPublic(cmd, this.metaSsbo, (VulkanGpuBuffer)targets.cascadeMetaBuffer);
         encoder.submit();
      } else {
         this.volumeDataInitialized = false;
      }
   }

   private void ensureParticleBuffer(int requiredBytes) {
      long required = Math.max(8192L, (long)requiredBytes);
      if (required > this.particleCapacity || this.particleSsbo == null) {
         if (this.particleSsbo != null) {
            this.particleSsbo.close();
         }

         this.particleCapacity = Math.max(required, Math.max(8192L, this.particleCapacity * 2L));
         this.particleSsbo = new PhysicsVulkanStorageBuffer(this.vkDevice, () -> "Physics Smoke Particle SSBO", 10, this.particleCapacity, 32, true);
      }
   }

   private void ensureMetaBuffer(long requiredBytes) {
      if (requiredBytes > this.metaCapacity || this.metaSsbo == null) {
         if (this.metaSsbo != null) {
            this.metaSsbo.close();
         }

         this.metaCapacity = requiredBytes;
         this.metaSsbo = new PhysicsVulkanStorageBuffer(this.vkDevice, () -> "Physics Smoke Meta SSBO", 24, this.metaCapacity, 35, false);
         this.volumeDataInitialized = false;
      }
   }

   private void ensureDescriptorSets(int requiredSets) {
      if (requiredSets > 0) {
         if (this.descriptorSetCapacity < requiredSets || this.descriptorSets.length < requiredSets || this.descriptorPool == 0L) {
            if (this.descriptorPool != 0L) {
               VK12.vkDestroyDescriptorPool(this.vkDevice.vkDevice(), this.descriptorPool, null);
               this.descriptorPool = 0L;
            }

            MemoryStack stack = MemoryStack.stackPush();

            try {
               org.lwjgl.vulkan.VkDescriptorPoolSize.Buffer poolSizes = VkDescriptorPoolSize.calloc(3, stack);
               ((VkDescriptorPoolSize)poolSizes.get(0)).type(7).descriptorCount(2 * requiredSets);
               ((VkDescriptorPoolSize)poolSizes.get(1)).type(3).descriptorCount(5 * requiredSets);
               ((VkDescriptorPoolSize)poolSizes.get(2)).type(1).descriptorCount(requiredSets);
               VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.calloc(stack).sType$Default().maxSets(requiredSets).pPoolSizes(poolSizes);
               LongBuffer poolPtr = stack.callocLong(1);
               VulkanUtils.crashIfFailure(
                  (VulkanDevice)RenderSystem.getDevice().backend,
                  VK12.vkCreateDescriptorPool(this.vkDevice.vkDevice(), poolInfo, null, poolPtr),
                  "Failed to create smoke descriptor pool"
               );
               this.descriptorPool = poolPtr.get(0);
               LongBuffer setLayouts = stack.mallocLong(requiredSets);

               for (int i = 0; i < requiredSets; i++) {
                  setLayouts.put(i, this.descriptorSetLayout);
               }

               VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack)
                  .sType$Default()
                  .descriptorPool(this.descriptorPool)
                  .pSetLayouts(setLayouts);
               LongBuffer setPtr = stack.mallocLong(requiredSets);
               VulkanUtils.crashIfFailure(
                  (VulkanDevice)RenderSystem.getDevice().backend,
                  VK12.vkAllocateDescriptorSets(this.vkDevice.vkDevice(), allocInfo, setPtr),
                  "Failed to allocate smoke descriptor sets"
               );
               this.descriptorSets = new long[requiredSets];

               for (int i = 0; i < requiredSets; i++) {
                  this.descriptorSets[i] = setPtr.get(i);
               }

               this.descriptorSetCapacity = requiredSets;
            } catch (Throwable var11) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var10) {
                     var11.addSuppressed(var10);
                  }
               }

               throw var11;
            }

            if (stack != null) {
               stack.close();
            }
         }
      }
   }

   private VulkanCommandEncoder commandEncoder() {
      return this.vkDevice.createCommandEncoder();
   }

   private static VkCommandBuffer commandBuffer(VulkanCommandEncoder encoder) {
      return ((VulkanCommandEncoderAccessor)encoder).physicsmod$getTextureInitCommandBuffer();
   }

   private void bindDescriptorSet(VkCommandBuffer cmd, long descriptorSet) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VK12.vkCmdBindDescriptorSets(cmd, 1, this.pipelineLayout, 0, stack.longs(descriptorSet), null);
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void pushParams(
      VkCommandBuffer cmd, int cascadeIndex, int particleCount, Vector3f boundsMin, Vector3f boundsSize, int gridX, int gridY, int gridZ, boolean hasLightMap
   ) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         ByteBuffer bytes = stack.malloc(128);
         float cellX = boundsSize.x / (float)gridX;
         float cellY = boundsSize.y / (float)gridY;
         float cellZ = boundsSize.z / (float)gridZ;
         bytes.putInt(0, cascadeIndex);
         bytes.putInt(4, particleCount);
         bytes.putInt(8, 4);
         bytes.putInt(12, 2);
         bytes.putFloat(16, boundsMin.x);
         bytes.putFloat(20, boundsMin.y);
         bytes.putFloat(24, boundsMin.z);
         bytes.putFloat(28, 0.0F);
         bytes.putFloat(32, boundsSize.x);
         bytes.putFloat(36, boundsSize.y);
         bytes.putFloat(40, boundsSize.z);
         bytes.putFloat(44, 0.0F);
         bytes.putInt(48, gridX);
         bytes.putInt(52, gridY);
         bytes.putInt(56, gridZ);
         bytes.putInt(60, 0);
         bytes.putFloat(64, cellX);
         bytes.putFloat(68, cellY);
         bytes.putFloat(72, cellZ);
         bytes.putFloat(76, 0.0F);
         bytes.putFloat(80, 0.2F);
         bytes.putFloat(84, 0.8F);
         bytes.putFloat(88, hasLightMap ? 1.0F : 0.0F);
         bytes.putFloat(92, 0.0F);
         bytes.putFloat(96, SmokeColorConfig.fireVolumeColorRed());
         bytes.putFloat(100, SmokeColorConfig.fireVolumeColorGreen());
         bytes.putFloat(104, SmokeColorConfig.fireVolumeColorBlue());
         bytes.putFloat(108, 1.0F);
         bytes.putFloat(112, SmokeColorConfig.steamVolumeColorRed());
         bytes.putFloat(116, SmokeColorConfig.steamVolumeColorGreen());
         bytes.putFloat(120, SmokeColorConfig.steamVolumeColorBlue());
         bytes.putFloat(124, 1.0F);
         VK12.vkCmdPushConstants(cmd, this.pipelineLayout, 32, 0, bytes);
      } catch (Throwable var16) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var15) {
               var16.addSuppressed(var15);
            }
         }

         throw var16;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void updateDescriptorSet(
      long descriptorSet,
      PhysicsVulkanStorageBuffer particleBuffer,
      PhysicsVulkanStorageBuffer metaBuffer,
      VulkanGpuTextureView densityAccView,
      VulkanGpuTextureView occupancyView,
      VulkanGpuTextureView lightAccView,
      VulkanGpuTextureView densityView,
      VulkanGpuTextureView lightView,
      VulkanGpuTextureView lightmapView
   ) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         org.lwjgl.vulkan.VkDescriptorBufferInfo.Buffer particleInfo = VkDescriptorBufferInfo.calloc(1, stack)
            .buffer(particleBuffer.vkBuffer())
            .offset(0L)
            .range(particleBuffer.size());
         org.lwjgl.vulkan.VkDescriptorBufferInfo.Buffer metaInfo = VkDescriptorBufferInfo.calloc(1, stack)
            .buffer(metaBuffer.vkBuffer())
            .offset(0L)
            .range(metaBuffer.size());
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer densityAccInfo = VkDescriptorImageInfo.calloc(1, stack)
            .imageView(densityAccView.vkImageView())
            .imageLayout(1);
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer occupancyInfo = VkDescriptorImageInfo.calloc(1, stack)
            .imageView(occupancyView.vkImageView())
            .imageLayout(1);
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer lightAccInfo = VkDescriptorImageInfo.calloc(1, stack)
            .imageView(lightAccView.vkImageView())
            .imageLayout(1);
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer densityInfo = VkDescriptorImageInfo.calloc(1, stack).imageView(densityView.vkImageView()).imageLayout(1);
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer lightInfo = VkDescriptorImageInfo.calloc(1, stack).imageView(lightView.vkImageView()).imageLayout(1);
         org.lwjgl.vulkan.VkDescriptorImageInfo.Buffer lightmapInfo = VkDescriptorImageInfo.calloc(1, stack)
            .sampler(this.lightmapSampler.vkSampler())
            .imageView(lightmapView.vkImageView())
            .imageLayout(1);
         org.lwjgl.vulkan.VkWriteDescriptorSet.Buffer writes = VkWriteDescriptorSet.calloc(8, stack);
         ((VkWriteDescriptorSet)writes.get(0))
            .sType$Default()
            .dstSet(descriptorSet)
            .dstBinding(0)
            .descriptorType(7)
            .descriptorCount(1)
            .pBufferInfo(particleInfo);
         ((VkWriteDescriptorSet)writes.get(1)).sType$Default().dstSet(descriptorSet).dstBinding(1).descriptorType(7).descriptorCount(1).pBufferInfo(metaInfo);
         ((VkWriteDescriptorSet)writes.get(2))
            .sType$Default()
            .dstSet(descriptorSet)
            .dstBinding(2)
            .descriptorType(3)
            .descriptorCount(1)
            .pImageInfo(densityAccInfo);
         ((VkWriteDescriptorSet)writes.get(3))
            .sType$Default()
            .dstSet(descriptorSet)
            .dstBinding(3)
            .descriptorType(3)
            .descriptorCount(1)
            .pImageInfo(occupancyInfo);
         ((VkWriteDescriptorSet)writes.get(4))
            .sType$Default()
            .dstSet(descriptorSet)
            .dstBinding(4)
            .descriptorType(3)
            .descriptorCount(1)
            .pImageInfo(lightAccInfo);
         ((VkWriteDescriptorSet)writes.get(5)).sType$Default().dstSet(descriptorSet).dstBinding(5).descriptorType(3).descriptorCount(1).pImageInfo(densityInfo);
         ((VkWriteDescriptorSet)writes.get(6)).sType$Default().dstSet(descriptorSet).dstBinding(6).descriptorType(3).descriptorCount(1).pImageInfo(lightInfo);
         ((VkWriteDescriptorSet)writes.get(7))
            .sType$Default()
            .dstSet(descriptorSet)
            .dstBinding(7)
            .descriptorType(1)
            .descriptorCount(1)
            .pImageInfo(lightmapInfo);
         VK12.vkUpdateDescriptorSets(this.vkDevice.vkDevice(), writes, null);
      } catch (Throwable var22) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var21) {
               var22.addSuppressed(var21);
            }
         }

         throw var22;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private long createPipeline(MemoryStack stack, long shaderModule) {
      LongBuffer ptr = stack.callocLong(1);
      VkPipelineShaderStageCreateInfo stage = VkPipelineShaderStageCreateInfo.calloc(stack).sType$Default();
      stage.stage(32).module(shaderModule).pName(stack.UTF8("main"));
      org.lwjgl.vulkan.VkComputePipelineCreateInfo.Buffer pipelineInfo = VkComputePipelineCreateInfo.calloc(1, stack);
      ((VkComputePipelineCreateInfo)pipelineInfo.get(0)).sType$Default().stage(stage).layout(this.pipelineLayout);
      VulkanUtils.crashIfFailure(
         (VulkanDevice)RenderSystem.getDevice().backend,
         VK12.vkCreateComputePipelines(this.vkDevice.vkDevice(), 0L, pipelineInfo, null, ptr),
         "Failed to create smoke compute pipeline"
      );
      return ptr.get(0);
   }

   private long createShaderModule(String source, String debugName) {
      long compiler = Shaderc.shaderc_compiler_initialize();
      long options = Shaderc.shaderc_compile_options_initialize();
      Shaderc.shaderc_compile_options_set_target_env(options, 0, 4202496);
      long result = Shaderc.shaderc_compile_into_spv(compiler, source, 2, debugName, "main", options);
      if (Shaderc.shaderc_result_get_compilation_status(result) != 0) {
         throw new IllegalStateException(Shaderc.shaderc_result_get_error_message(result));
      } else {
         ByteBuffer spirv = Shaderc.shaderc_result_get_bytes(result);

         long var13;
         try {
            MemoryStack stack = MemoryStack.stackPush();

            try {
               VkShaderModuleCreateInfo info = VkShaderModuleCreateInfo.calloc(stack).sType$Default().pCode(spirv);
               LongBuffer ptr = stack.callocLong(1);
               VulkanUtils.crashIfFailure(
                  (VulkanDevice)RenderSystem.getDevice().backend,
                  VK12.vkCreateShaderModule(this.vkDevice.vkDevice(), info, null, ptr),
                  "Failed to create shader module: " + debugName
               );
               var13 = ptr.get(0);
            } catch (Throwable var20) {
               if (stack != null) {
                  try {
                     stack.close();
                  } catch (Throwable var19) {
                     var20.addSuppressed(var19);
                  }
               }

               throw var20;
            }

            if (stack != null) {
               stack.close();
            }
         } finally {
            Shaderc.shaderc_result_release(result);
            Shaderc.shaderc_compile_options_release(options);
            Shaderc.shaderc_compiler_release(compiler);
         }

         return var13;
      }
   }

   private void clearMeta(VkCommandBuffer cmd, PhysicsVulkanStorageBuffer metaBuffer) {
      VK12.vkCmdFillBuffer(cmd, metaBuffer.vkBuffer(), 0L, metaBuffer.size(), 0);
   }

   private void clearAllVolumes(VkCommandBuffer cmd, SmokeVolumeTargets targets, int cascadeCount) {
      for (int c = 0; c < cascadeCount; c++) {
         this.clearImage(cmd, (VulkanGpuTexture)targets.densityAccTextures[c]);
         this.clearImage(cmd, (VulkanGpuTexture)targets.lightAccTextures[c]);
         this.clearImage(cmd, (VulkanGpuTexture)targets.occupancyTextures[c]);
      }
   }

   private void clearOccupancy(VkCommandBuffer cmd, SmokeVolumeTargets targets, int cascadeCount) {
      for (int c = 0; c < cascadeCount; c++) {
         this.clearImage(cmd, (VulkanGpuTexture)targets.occupancyTextures[c]);
      }
   }

   private void clearImage(VkCommandBuffer cmd, VulkanGpuTexture texture) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkImageSubresourceRange range = VkImageSubresourceRange.calloc(stack).aspectMask(1).baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1);
         VkClearColorValue color = VkClearColorValue.calloc(stack);
         color.int32(0, 0).int32(1, 0).int32(2, 0).int32(3, 0);
         VK12.vkCmdClearColorImage(cmd, texture.vkImage(), 1, color, range);
      } catch (Throwable var7) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var6) {
               var7.addSuppressed(var6);
            }
         }

         throw var7;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void barrierImages(VkCommandBuffer cmd, SmokeVolumeTargets targets, int cascadeCount, int dstStage, int dstAccess) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         org.lwjgl.vulkan.VkImageMemoryBarrier.Buffer barriers = VkImageMemoryBarrier.calloc(cascadeCount * 5, stack);
         int idx = 0;

         for (int c = 0; c < cascadeCount; c++) {
            idx = this.imageBarrier(barriers, idx, (VulkanGpuTexture)targets.densityAccTextures[c], dstAccess);
            idx = this.imageBarrier(barriers, idx, (VulkanGpuTexture)targets.occupancyTextures[c], dstAccess);
            idx = this.imageBarrier(barriers, idx, (VulkanGpuTexture)targets.lightAccTextures[c], dstAccess);
            idx = this.imageBarrier(barriers, idx, (VulkanGpuTexture)targets.densityTextures[c], dstAccess);
            idx = this.imageBarrier(barriers, idx, (VulkanGpuTexture)targets.lightTextures[c], dstAccess);
         }

         barriers.limit(idx);
         VK12.vkCmdPipelineBarrier(cmd, 65536, dstStage, 0, null, null, barriers);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private int imageBarrier(org.lwjgl.vulkan.VkImageMemoryBarrier.Buffer barriers, int idx, VulkanGpuTexture texture, int dstAccess) {
      ((VkImageMemoryBarrier)barriers.get(idx))
         .sType$Default()
         .srcAccessMask(4192)
         .dstAccessMask(dstAccess)
         .oldLayout(1)
         .newLayout(1)
         .srcQueueFamilyIndex(-1)
         .dstQueueFamilyIndex(-1)
         .image(texture.vkImage())
         .subresourceRange(r -> r.aspectMask(1).baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1));
      return idx + 1;
   }

   private void bufferBarrier(VkCommandBuffer cmd, int srcStage, int dstStage, int srcAccess, int dstAccess, PhysicsVulkanStorageBuffer buffer) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         org.lwjgl.vulkan.VkBufferMemoryBarrier.Buffer barrier = VkBufferMemoryBarrier.calloc(1, stack);
         ((VkBufferMemoryBarrier)barrier.get(0))
            .sType$Default()
            .srcAccessMask(srcAccess)
            .dstAccessMask(dstAccess)
            .srcQueueFamilyIndex(-1)
            .dstQueueFamilyIndex(-1)
            .buffer(buffer.vkBuffer())
            .offset(0L)
            .size(buffer.size());
         VK12.vkCmdPipelineBarrier(cmd, srcStage, dstStage, 0, null, barrier, null);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }
   }

   private void copyMetaToPublic(VkCommandBuffer cmd, PhysicsVulkanStorageBuffer src, VulkanGpuBuffer dst) {
      MemoryStack stack = MemoryStack.stackPush();

      try {
         this.bufferBarrier(cmd, 2048, 4096, 96, 2048, src);
         org.lwjgl.vulkan.VkBufferCopy.Buffer copy = VkBufferCopy.calloc(1, stack);
         ((VkBufferCopy)copy.get(0)).srcOffset(0L).dstOffset(0L).size(dst.size());
         VK12.vkCmdCopyBuffer(cmd, src.vkBuffer(), dst.vkBuffer(), copy);
         org.lwjgl.vulkan.VkBufferMemoryBarrier.Buffer barrier = VkBufferMemoryBarrier.calloc(1, stack);
         ((VkBufferMemoryBarrier)barrier.get(0))
            .sType$Default()
            .srcAccessMask(4096)
            .dstAccessMask(32)
            .srcQueueFamilyIndex(-1)
            .dstQueueFamilyIndex(-1)
            .buffer(dst.vkBuffer())
            .offset(0L)
            .size(dst.size());
         VK12.vkCmdPipelineBarrier(cmd, 4096, 128, 0, null, barrier, null);
      } catch (Throwable var8) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (stack != null) {
         stack.close();
      }
   }

   @Override
   public boolean isSupported() {
      return true;
   }

   @Override
   public void close() {
      if (this.particleSsbo != null) {
         this.particleSsbo.close();
         this.particleSsbo = null;
      }

      if (this.metaSsbo != null) {
         this.metaSsbo.close();
         this.metaSsbo = null;
      }

      this.lightmapSampler.close();
      VK12.vkDestroyPipeline(this.vkDevice.vkDevice(), this.clearPipeline, null);
      VK12.vkDestroyPipeline(this.vkDevice.vkDevice(), this.splatPipeline, null);
      VK12.vkDestroyPipeline(this.vkDevice.vkDevice(), this.resolvePipeline, null);
      VK12.vkDestroyShaderModule(this.vkDevice.vkDevice(), this.clearShaderModule, null);
      VK12.vkDestroyShaderModule(this.vkDevice.vkDevice(), this.splatShaderModule, null);
      VK12.vkDestroyShaderModule(this.vkDevice.vkDevice(), this.resolveShaderModule, null);
      VK12.vkDestroyPipelineLayout(this.vkDevice.vkDevice(), this.pipelineLayout, null);
      if (this.descriptorPool != 0L) {
         VK12.vkDestroyDescriptorPool(this.vkDevice.vkDevice(), this.descriptorPool, null);
         this.descriptorPool = 0L;
      }

      this.descriptorSets = new long[0];
      this.descriptorSetCapacity = 0;
      VK12.vkDestroyDescriptorSetLayout(this.vkDevice.vkDevice(), this.descriptorSetLayout, null);
   }

   private static String load(String path) {
      try {
         return new String(VulkanSmokeVolumeBackend.class.getResourceAsStream(path).readAllBytes(), StandardCharsets.UTF_8);
      } catch (IOException var2) {
         throw new RuntimeException("Failed to load shader: " + path, var2);
      }
   }
}
