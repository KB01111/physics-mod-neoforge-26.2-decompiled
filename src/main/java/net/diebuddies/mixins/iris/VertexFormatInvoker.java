package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({VertexFormat.class})
public interface VertexFormatInvoker {
   @Invoker("<init>")
   static VertexFormat physicsmod$invokeInit(List<VertexFormatElement> elements, int vertexSize, int stepRate) {
      throw new AssertionError();
   }
}
