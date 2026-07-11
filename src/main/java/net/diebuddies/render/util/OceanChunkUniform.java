package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;

public record OceanChunkUniform(float modelOffsetX, float modelOffsetY, float modelOffsetZ, float waveOffsetX, float waveOffsetZ) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putFloat(this.modelOffsetX)
         .putFloat(this.modelOffsetY)
         .putFloat(this.modelOffsetZ)
         .putFloat(this.waveOffsetX)
         .putFloat(this.waveOffsetZ);
   }
}
