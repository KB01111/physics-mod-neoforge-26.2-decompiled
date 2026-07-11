package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;

public record GaussianDepthBlurUniform(
   float offsetX, float offsetY, float texelSizeX, float texelSizeY, float near, float far, float p00, float blurWorldDistance, int zZeroToOne, int reverseZ
) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putVec4(this.offsetX, this.offsetY, this.texelSizeX, this.texelSizeY)
         .putVec4(this.near, this.far, this.p00, this.blurWorldDistance)
         .putInt(this.zZeroToOne)
         .putInt(this.reverseZ);
   }
}
