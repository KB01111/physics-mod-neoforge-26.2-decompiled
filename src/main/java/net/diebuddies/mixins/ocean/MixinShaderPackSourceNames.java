package net.diebuddies.mixins.ocean;

import com.google.common.collect.ImmutableList.Builder;
import net.irisshaders.iris.shaderpack.include.ShaderPackSourceNames;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({ShaderPackSourceNames.class})
public class MixinShaderPackSourceNames {
   @Unique
   private static boolean physicsmod$hasAdded = false;

   @Inject(
      at = {@At("TAIL")},
      method = {"addStarts"}
   )
   private static void physicsmod$addOceanFilesSupport(Builder<String> potentialFileNames, String baseName, CallbackInfo info) {
      if (!physicsmod$hasAdded) {
         physicsmod$hasAdded = !physicsmod$hasAdded;
         potentialFileNames.add("physics_ocean.vsh");
         potentialFileNames.add("physics_ocean.tcs");
         potentialFileNames.add("physics_ocean.tes");
         potentialFileNames.add("physics_ocean.gsh");
         potentialFileNames.add("physics_ocean.fsh");
         potentialFileNames.add("physics_ocean_shadow.vsh");
         potentialFileNames.add("physics_ocean_shadow.tcs");
         potentialFileNames.add("physics_ocean_shadow.tes");
         potentialFileNames.add("physics_ocean_shadow.gsh");
         potentialFileNames.add("physics_ocean_shadow.fsh");
         potentialFileNames.add("physics_liquid.vsh");
         potentialFileNames.add("physics_liquid.tcs");
         potentialFileNames.add("physics_liquid.tes");
         potentialFileNames.add("physics_liquid.gsh");
         potentialFileNames.add("physics_liquid.fsh");
         potentialFileNames.add("physics_liquid_shadow.vsh");
         potentialFileNames.add("physics_liquid_shadow.tcs");
         potentialFileNames.add("physics_liquid_shadow.tes");
         potentialFileNames.add("physics_liquid_shadow.gsh");
         potentialFileNames.add("physics_liquid_shadow.fsh");
      }
   }
}
