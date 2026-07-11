package net.diebuddies.mixins.optifine;

import net.optifine.shaders.Program;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
   targets = {"net.optifine.shaders.Programs"}
)
public class MixinPrograms {
   @Shadow
   private Program makeGbuffers(String name, Program backupProgram) {
      return null;
   }

   @Shadow
   private Program makeShadow(String name, Program backupProgram) {
      return null;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"getPrograms"}
   )
   private void physicsmod$addOceanShader(CallbackInfoReturnable<Program[]> info) {
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"makeShadowcomps"}
   )
   private void physicsmod$addOceanShadowShader(String prefix, int count, CallbackInfoReturnable<Program[]> info) {
   }
}
