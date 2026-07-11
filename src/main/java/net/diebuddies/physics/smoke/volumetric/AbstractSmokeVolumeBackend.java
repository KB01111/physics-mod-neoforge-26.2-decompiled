package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.systems.GpuDevice;

abstract class AbstractSmokeVolumeBackend implements SmokeVolumeBackend {
   protected final GpuDevice device;
   protected boolean volumeDataInitialized;

   protected AbstractSmokeVolumeBackend(GpuDevice device) {
      this.device = device;
      this.volumeDataInitialized = false;
   }

   @Override
   public void resetFrameState() {
      this.volumeDataInitialized = false;
   }
}
