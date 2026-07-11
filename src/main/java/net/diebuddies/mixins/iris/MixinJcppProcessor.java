package net.diebuddies.mixins.iris;

import net.diebuddies.compat.Iris;
import net.diebuddies.util.ShaderFixes;
import net.irisshaders.iris.helpers.StringPair;
import net.irisshaders.iris.shaderpack.preprocessor.JcppProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin({JcppProcessor.class})
public class MixinJcppProcessor {
   @Inject(
      at = {@At("RETURN")},
      method = {"glslPreprocessSource"},
      cancellable = true
   )
   private static void glslPreprocessSource(String source, Iterable<StringPair> environmentDefines, CallbackInfoReturnable<String> info) {
      String shaderSource = (String)info.getReturnValue();
      if (shaderSource != null) {
         if (Iris.injectIntoEntityOrShadowShader.get()) {
            shaderSource = ShaderFixes.applyFixes(shaderSource);
         }

         shaderSource = ShaderFixes.applyAllFixes(shaderSource);
         info.setReturnValue(shaderSource);
      }
   }
}
