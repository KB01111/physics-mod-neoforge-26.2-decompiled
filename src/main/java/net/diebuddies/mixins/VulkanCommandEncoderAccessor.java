package net.diebuddies.mixins;

import com.mojang.blaze3d.vulkan.VulkanCommandEncoder;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({VulkanCommandEncoder.class})
public interface VulkanCommandEncoderAccessor {
   @Invoker("textureInitCommandBuffer")
   VkCommandBuffer physicsmod$getTextureInitCommandBuffer();
}
