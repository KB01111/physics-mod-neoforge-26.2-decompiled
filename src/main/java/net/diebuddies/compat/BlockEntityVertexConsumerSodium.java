package net.diebuddies.compat;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.caffeinemc.mods.sodium.api.vertex.buffer.VertexBufferWriter;
import net.diebuddies.physics.render.BlockEntityVertexConsumer;
import org.lwjgl.system.MemoryStack;

public class BlockEntityVertexConsumerSodium extends BlockEntityVertexConsumer implements VertexBufferWriter {
   public BlockEntityVertexConsumerSodium(GpuTextureView gpuTexture) {
      super(gpuTexture);
   }

   public void push(MemoryStack stack, long ptr, int count, VertexFormat format) {
   }
}
