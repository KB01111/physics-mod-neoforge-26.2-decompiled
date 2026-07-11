package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.VertexArrayCache.VertexArray;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.render.util.DummyVertexFormat;
import org.lwjgl.opengl.ARBVertexAttribBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   targets = {"com.mojang.blaze3d.opengl.VertexArrayCache$Separate"}
)
public abstract class MixinVertexArrayCacheSeparate {
   @Redirect(
      method = {"bindVertexArray"},
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_enableVertexAttribArray(I)V"
      )
   )
   private void physicsmod$handleEnableVertexAttribArray(int index, VertexFormat[] formats, GpuBufferSlice[] vertexBuffers, VertexArray lastBoundVertexArray) {
      if (!DummyVertexFormat.isDummy(formats[0], index)) {
         GlStateManager._enableVertexAttribArray(index);
      }
   }

   @Redirect(
      method = {"bindVertexArray"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/opengl/ARBVertexAttribBinding;glVertexAttribIFormat(IIII)V"
      )
   )
   private void physicsmod$handleVertexAttribIFormat(
      int attribIndex, int size, int type, int relativeOffset, VertexFormat[] formats, GpuBufferSlice[] vertexBuffers, VertexArray lastBoundVertexArray
   ) {
      if (!DummyVertexFormat.isDummy(formats[0], attribIndex)) {
         ARBVertexAttribBinding.glVertexAttribIFormat(attribIndex, size, type, relativeOffset);
      }
   }

   @Redirect(
      method = {"bindVertexArray"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/opengl/ARBVertexAttribBinding;glVertexAttribFormat(IIIZI)V"
      )
   )
   private void physicsmod$handleVertexAttribFormat(
      int attribIndex,
      int size,
      int type,
      boolean normalized,
      int relativeOffset,
      VertexFormat[] formats,
      GpuBufferSlice[] vertexBuffers,
      VertexArray lastBoundVertexArray
   ) {
      if (!DummyVertexFormat.isDummy(formats[0], attribIndex)) {
         ARBVertexAttribBinding.glVertexAttribFormat(attribIndex, size, type, normalized, relativeOffset);
      }
   }

   @Redirect(
      method = {"bindVertexArray"},
      at = @At(
         value = "INVOKE",
         target = "Lorg/lwjgl/opengl/ARBVertexAttribBinding;glVertexAttribBinding(II)V"
      )
   )
   private void physicsmod$handleVertexAttribBinding(
      int attribIndex, int bindingIndex, VertexFormat[] formats, GpuBufferSlice[] vertexBuffers, VertexArray lastBoundVertexArray
   ) {
      if (!DummyVertexFormat.isDummy(formats[0], attribIndex)) {
         ARBVertexAttribBinding.glVertexAttribBinding(attribIndex, bindingIndex);
      }
   }
}
