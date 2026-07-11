package net.diebuddies.physics.smoke.volumetric;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.nio.ByteBuffer;
import org.joml.Vector3f;

public interface SmokeVolumeBackend extends AutoCloseable {
   void buildVolumes(
      ByteBuffer var1,
      int var2,
      SmokeVolumeTargets var3,
      GpuTexture var4,
      GpuTextureView var5,
      int var6,
      Vector3f[] var7,
      Vector3f[] var8,
      int var9,
      int var10,
      int var11
   );

   default boolean isSupported() {
      return true;
   }

   default void resetFrameState() {
   }

   @Override
   void close();
}
