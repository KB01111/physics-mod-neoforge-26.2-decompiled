package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.StarterClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({RenderPipeline.class})
public class MixinRenderPipeline {
   @Inject(
      method = {"getVertexFormatBindings"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void physicsmod$irisFormats1(CallbackInfoReturnable<VertexFormat[]> info) {
      if (StarterClient.iris()) {
         VertexFormat[] vfs = (VertexFormat[])info.getReturnValue();
         if (vfs == null || vfs.length == 0 || vfs[0] == null) {
            return;
         }

         VertexFormat[] actualvfs = new VertexFormat[vfs.length];
         boolean changed = false;

         for (int i = 0; i < vfs.length; i++) {
            actualvfs[i] = Iris.remapFormat(vfs[i]);
            changed |= actualvfs[i] != vfs[i];
         }

         if (changed) {
            info.setReturnValue(actualvfs);
         }
      }
   }

   @Inject(
      method = {"getVertexFormatBinding"},
      at = {@At("RETURN")},
      cancellable = true
   )
   private void physicsmod$irisFormats2(int bindingIndex, CallbackInfoReturnable<VertexFormat> info) {
      if (StarterClient.iris()) {
         VertexFormat vf = (VertexFormat)info.getReturnValue();
         if (vf == null) {
            return;
         }

         VertexFormat actualVf = Iris.remapFormat((VertexFormat)info.getReturnValue());
         if (vf != actualVf) {
            info.setReturnValue(actualVf);
         }
      }
   }
}
