package net.diebuddies.mixins.optifine;

import net.diebuddies.compat.Optifine;
import net.diebuddies.util.ShaderFixes;
import net.diebuddies.util.ShaderFixesOptifine;
import net.optifine.shaders.Program;
import net.optifine.util.LineBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"net.optifine.shaders.ShadersCompatibility"}
)
public class MixinShadersCompatibility {
   @ModifyVariable(
      method = {"remap"},
      at = @At("HEAD"),
      ordinal = 0
   )
   private static LineBuffer physicsmod$injectFixes(LineBuffer lines) {
      String program = Optifine.compilingProgram.getName();
      if (!program.contains("gbuffers_entities")
         && !program.contains("gbuffers_entities_glowing")
         && !program.contains("gbuffers_entities_translucent")
         && !program.contains("shadow")) {
         LineBuffer buffer = new LineBuffer();

         for (String line : lines) {
            buffer.add(ShaderFixes.applyAllFixes(line));
         }

         return buffer;
      } else {
         LineBuffer buffer = new LineBuffer();

         for (String line : lines) {
            buffer.add(ShaderFixes.applyAllFixes(ShaderFixes.applyFixes(line)));
         }

         return buffer;
      }
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"remap"},
      cancellable = true
   )
   private static void physicsmod$injectOptifineFixes(Program program, @Coerce Object shaderType, LineBuffer lines, CallbackInfoReturnable<LineBuffer> info) {
      String programName = Optifine.compilingProgram.getName();
      if (!programName.contains("gbuffers_entities")
         && !programName.contains("gbuffers_entities_glowing")
         && !programName.contains("gbuffers_entities_translucent")
         && !programName.contains("shadow")) {
         info.setReturnValue(ShaderFixesOptifine.applyOptifineFixes((LineBuffer)info.getReturnValue(), true));
      } else {
         info.setReturnValue(ShaderFixesOptifine.applyOptifineFixes((LineBuffer)info.getReturnValue(), false));
      }
   }

   @Unique
   private static String physicsmod$turnIntoOneString(LineBuffer lines) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < lines.size(); i++) {
         sb.append(lines.get(i));
         sb.append("\n");
      }

      return sb.toString();
   }
}
