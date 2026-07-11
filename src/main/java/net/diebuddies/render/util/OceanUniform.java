package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;

public record OceanUniform(
   float oceanTime,
   float globalTime,
   int iterationsNormal,
   float oceanHeight,
   float oceanWaveHorizontalScale,
   float rippleRange,
   float foamAmount,
   float foamOpacity
) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putFloat(this.oceanTime)
         .putFloat(this.globalTime)
         .putInt(this.iterationsNormal)
         .putFloat(this.oceanHeight)
         .putFloat(this.oceanWaveHorizontalScale)
         .putFloat(this.rippleRange)
         .putFloat(this.foamAmount)
         .putFloat(this.foamOpacity);
   }
}
