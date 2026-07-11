package net.diebuddies.render;

import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.mixins.GpuDeviceBackendAccessor;
import net.diebuddies.physics.smoke.volumetric.GLCompat;
import net.diebuddies.physics.smoke.volumetric.RayMarcherEffect;
import net.minecraft.client.multiplayer.ClientLevel;
import org.joml.Matrix4f;

public class SmokeVolumeRenderer {
   private static final int CASCADES = 3;
   private static final int CASCADES_WITH_SHADER_MODS = 4;
   private RayMarcherEffect smokeVolume;
   private final MainRenderer mainRenderer;
   private final boolean supported;

   public SmokeVolumeRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.supported = supportsVolumetricSmoke();
   }

   public void render(ClientLevel level, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
   }

   private int getDownsampleFactor() {
      return switch (ConfigClient.smokeVolumeQuality) {
         case 0 -> 4;
         case 1 -> 2;
         case 2 -> 1;
         default -> 1;
      };
   }

   public void destroy() {
      if (this.smokeVolume != null) {
         this.smokeVolume.close();
         this.smokeVolume = null;
      }
   }

   public static boolean supportsVolumetricSmoke() {
      GpuDeviceBackend backend = ((GpuDeviceBackendAccessor)RenderSystem.getDevice()).physicsmod$getBackend();
      return backend instanceof VulkanDevice || GLCompat.features().supportsSmokeComputePipeline;
   }
}
