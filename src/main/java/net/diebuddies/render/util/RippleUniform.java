package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.joml.Matrix4fc;

public record RippleUniform(
   Matrix4fc modelViewMat,
   Matrix4fc projMat,
   float rippleCameraPosX,
   float rippleCameraPosY,
   float rippleCameraPosZ,
   float renderPercent,
   float simulationCenterX,
   float simulationCenterZ,
   float previousSimulationCenterX,
   float previousSimulationCenterZ,
   float texelWorldSize,
   float damping,
   float propagation,
   float simulationStep,
   float rippleRange,
   float borderDamping,
   float impulseStrength,
   float padding
) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer)
         .putMat4f(this.modelViewMat)
         .putMat4f(this.projMat)
         .putVec4(this.rippleCameraPosX, this.rippleCameraPosY, this.rippleCameraPosZ, this.renderPercent)
         .putVec4(this.simulationCenterX, this.simulationCenterZ, this.previousSimulationCenterX, this.previousSimulationCenterZ)
         .putVec4(this.texelWorldSize, this.damping, this.propagation, this.simulationStep)
         .putVec4(this.rippleRange, this.borderDamping, this.impulseStrength, this.padding);
   }
}
