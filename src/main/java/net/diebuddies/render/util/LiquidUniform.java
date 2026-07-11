package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.joml.Matrix4fc;

public record LiquidUniform(
   Matrix4fc modelViewMat,
   Matrix4fc projMat,
   Matrix4fc invProjectionMat,
   Matrix4fc invViewMat,
   Matrix4fc viewMat,
   float liquidCameraPosX,
   float liquidCameraPosY,
   float liquidCameraPosZ,
   float renderPercent,
   float waterBoundsX,
   float waterBoundsY,
   float waterBoundsZ,
   float waterBoundsW,
   float cameraOffsetX,
   float cameraOffsetY,
   float cameraOffsetZ,
   float padding,
   int zZeroToOne
) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putMat4f(this.modelViewMat)
         .putMat4f(this.projMat)
         .putMat4f(this.invProjectionMat)
         .putMat4f(this.invViewMat)
         .putMat4f(this.viewMat)
         .putVec4(this.liquidCameraPosX, this.liquidCameraPosY, this.liquidCameraPosZ, this.renderPercent)
         .putVec4(this.waterBoundsX, this.waterBoundsY, this.waterBoundsZ, this.waterBoundsW)
         .putVec4(this.cameraOffsetX, this.cameraOffsetY, this.cameraOffsetZ, this.padding)
         .putInt(this.zZeroToOne);
   }
}
