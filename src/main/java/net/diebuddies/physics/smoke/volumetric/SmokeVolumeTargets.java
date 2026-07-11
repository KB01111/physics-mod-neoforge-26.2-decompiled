package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

public final class SmokeVolumeTargets {
   public static final int META_UINTS_PER_CASCADE = 8;
   public final GpuBuffer cascadeMetaBuffer;
   public final GpuTexture[] densityTextures;
   public final GpuTextureView[] densityViews;
   public final GpuTexture[] densityAccTextures;
   public final GpuTextureView[] densityAccViews;
   public final GpuTexture[] occupancyTextures;
   public final GpuTextureView[] occupancyViews;
   public final GpuTexture[] lightTextures;
   public final GpuTextureView[] lightViews;
   public final GpuTexture[] lightAccTextures;
   public final GpuTextureView[] lightAccViews;

   public SmokeVolumeTargets(
      GpuBuffer cascadeMetaBuffer,
      GpuTexture[] densityTextures,
      GpuTextureView[] densityViews,
      GpuTexture[] densityAccTextures,
      GpuTextureView[] densityAccViews,
      GpuTexture[] occupancyTextures,
      GpuTextureView[] occupancyViews,
      GpuTexture[] lightTextures,
      GpuTextureView[] lightViews,
      GpuTexture[] lightAccTextures,
      GpuTextureView[] lightAccViews
   ) {
      this.cascadeMetaBuffer = cascadeMetaBuffer;
      this.densityTextures = densityTextures;
      this.densityViews = densityViews;
      this.densityAccTextures = densityAccTextures;
      this.densityAccViews = densityAccViews;
      this.occupancyTextures = occupancyTextures;
      this.occupancyViews = occupancyViews;
      this.lightTextures = lightTextures;
      this.lightViews = lightViews;
      this.lightAccTextures = lightAccTextures;
      this.lightAccViews = lightAccViews;
   }
}
