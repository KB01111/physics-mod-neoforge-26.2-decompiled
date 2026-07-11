package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;

public record BrightnessUniform(int lightU, int lightV, int overlayU, int overlayV) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer).putInt(this.lightU).putInt(this.lightV).putInt(this.overlayU).putInt(this.overlayV);
   }
}
