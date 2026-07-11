package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.render.util.DummyVertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"com.mojang.blaze3d.opengl.VertexArrayCache$Emulated"}
)
public abstract class MixinVertexArrayCacheEmulated {
   @Redirect(
      method = {"setupCombinedAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_enableVertexAttribArray(I)V"
      )
   )
   private static void physicsmod$handleEnableVertexAttribArray(int index, VertexFormat[] formats, boolean enable, GpuBufferSlice[] vertexBuffers) {
      if (!DummyVertexFormat.isDummy(formats[0], index)) {
         GlStateManager._enableVertexAttribArray(index);
      }
   }

   @Redirect(
      method = {"setupCombinedAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_vertexAttribIPointer(IIIIJ)V"
      )
   )
   private static void physicsmod$handleVertexAttribIPointer(
      int index, int size, int type, int stride, long pointer, VertexFormat[] formats, boolean enable, GpuBufferSlice[] vertexBuffers
   ) {
      if (!DummyVertexFormat.isDummy(formats[0], index)) {
         GlStateManager._vertexAttribIPointer(index, size, type, stride, pointer);
      }
   }

   @Redirect(
      method = {"setupCombinedAttributes"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_vertexAttribPointer(IIIZIJ)V"
      )
   )
   private static void physicsmod$handleVertexAttribPointer(
      int index, int size, int type, boolean normalized, int stride, long pointer, VertexFormat[] formats, boolean enable, GpuBufferSlice[] vertexBuffers
   ) {
      if (!DummyVertexFormat.isDummy(formats[0], index)) {
         GlStateManager._vertexAttribPointer(index, size, type, normalized, stride, pointer);
      }
   }
}
