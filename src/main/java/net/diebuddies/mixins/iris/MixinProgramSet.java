package net.diebuddies.mixins.iris;

import java.util.function.Function;
import net.diebuddies.compat.Iris;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin({ProgramSet.class})
public class MixinProgramSet {
   @Inject(
      at = {@At("HEAD")},
      method = {"readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;"}
   )
   private static void readProgramSourceHead(
      AbsolutePackPath directory,
      Function<AbsolutePackPath, String> sourceProvider,
      String program,
      ProgramSet programSet,
      ShaderProperties properties,
      BlendModeOverride defaultBlendModeOverride,
      boolean readTesselation,
      CallbackInfoReturnable<ProgramSource> info
   ) {
      if (program.equalsIgnoreCase("gbuffers_entities") || program.equalsIgnoreCase("gbuffers_entities_translucent") || program.equalsIgnoreCase("shadow")) {
         Iris.injectIntoEntityOrShadowShader.set(true);
      }
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"readProgramSource(Lnet/irisshaders/iris/shaderpack/include/AbsolutePackPath;Ljava/util/function/Function;Ljava/lang/String;Lnet/irisshaders/iris/shaderpack/programs/ProgramSet;Lnet/irisshaders/iris/shaderpack/properties/ShaderProperties;Lnet/irisshaders/iris/gl/blending/BlendModeOverride;Z)Lnet/irisshaders/iris/shaderpack/programs/ProgramSource;"}
   )
   private static void readProgramSourceTail(
      AbsolutePackPath directory,
      Function<AbsolutePackPath, String> sourceProvider,
      String program,
      ProgramSet programSet,
      ShaderProperties properties,
      BlendModeOverride defaultBlendModeOverride,
      boolean readTesselation,
      CallbackInfoReturnable<ProgramSource> info
   ) {
      Iris.injectIntoEntityOrShadowShader.set(false);
   }
}
