package net.diebuddies.compat;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.diebuddies.physics.render.DummyVertexConsumer;
import org.lwjgl.system.MemoryStack;

public class DummyVertexConsumerSodium extends DummyVertexConsumer implements VertexBufferWriter {
   public DummyVertexConsumerSodium(GpuTextureView gpuTexture) {
      super(gpuTexture);
   }

   public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
   }
}
