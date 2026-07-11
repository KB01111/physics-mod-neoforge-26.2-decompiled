package net.diebuddies.mixins;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vulkan.VulkanConst;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuSampler;
import java.util.OptionalDouble;
import net.diebuddies.render.util.Vulkan3DSamplerState;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({VulkanGpuSampler.class})
public abstract class MixinVulkanGpuSampler {
   @Redirect(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/vulkan/VkSamplerCreateInfo;addressModeV(I)Lorg/lwjgl/vulkan/VkSamplerCreateInfo;"
      )
   )
   private VkSamplerCreateInfo physics$setAddressModeVAndW(
      VkSamplerCreateInfo createInfo,
      int addressModeV,
      VulkanDevice device,
      AddressMode addressModeU,
      AddressMode ctorAddressModeV,
      FilterMode minFilter,
      FilterMode magFilter,
      int maxAnisotropy,
      OptionalDouble maxLod
   ) {
      createInfo.addressModeV(addressModeV);
      AddressMode wrapW = Vulkan3DSamplerState.getWrapW();
      if (wrapW != null) {
         createInfo.addressModeW(VulkanConst.toVk(wrapW));
      } else {
         createInfo.addressModeW(addressModeV);
      }

      return createInfo;
   }
}
