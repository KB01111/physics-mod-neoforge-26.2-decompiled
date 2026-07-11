package net.diebuddies.render.util;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import java.util.OptionalDouble;

public final class Vulkan3DSamplerState {
   private static final ThreadLocal<AddressMode> WRAP_W = new ThreadLocal<>();

   private Vulkan3DSamplerState() {
   }

   public static GpuSampler createSampler3D(
      GpuDevice device,
      AddressMode addressModeU,
      AddressMode addressModeV,
      AddressMode addressModeW,
      FilterMode minFilter,
      FilterMode magFilter,
      int maxAnisotropy,
      OptionalDouble maxLod
   ) {
      WRAP_W.set(addressModeW);

      GpuSampler var8;
      try {
         var8 = device.createSampler(addressModeU, addressModeV, minFilter, magFilter, maxAnisotropy, maxLod);
      } finally {
         WRAP_W.remove();
      }

      return var8;
   }

   public static AddressMode getWrapW() {
      return WRAP_W.get();
   }
}
