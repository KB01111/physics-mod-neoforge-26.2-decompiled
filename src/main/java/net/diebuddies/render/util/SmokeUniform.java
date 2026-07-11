package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.joml.Matrix4f;

public record SmokeUniform(
   Matrix4f viewMatrix,
   Matrix4f projectionMatrix,
   Matrix4f inverseProjectionMatrix,
   float smokeColorRed,
   float smokeColorGreen,
   float smokeColorBlue,
   float smokeDensity,
   float smokeDenseColorRed,
   float smokeDenseColorGreen,
   float smokeDenseColorBlue,
   float renderPercent,
   float cameraOffsetX,
   float cameraOffsetY,
   float cameraOffsetZ,
   int zZeroToOne
) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putMat4f(this.viewMatrix)
         .putMat4f(this.projectionMatrix)
         .putMat4f(this.inverseProjectionMatrix)
         .putVec4(this.smokeColorRed, this.smokeColorGreen, this.smokeColorBlue, this.smokeDensity)
         .putVec4(this.smokeDenseColorRed, this.smokeDenseColorGreen, this.smokeDenseColorBlue, this.renderPercent)
         .putVec4(this.cameraOffsetX, this.cameraOffsetY, this.cameraOffsetZ, 0.0F)
         .putInt(this.zZeroToOne);
   }
}
