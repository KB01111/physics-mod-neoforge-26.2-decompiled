package net.diebuddies.render.util;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import net.minecraft.client.renderer.DynamicUniformStorage.DynamicUniform;
import org.joml.Vector3fc;

public record LightUniform(Vector3fc light1, Vector3fc light2) implements DynamicUniform {
   public void write(ByteBuffer byteBuffer) {
      Std140Builder.intoBuffer(byteBuffer).putVec3(this.light1).putVec3(this.light2);
   }
}
