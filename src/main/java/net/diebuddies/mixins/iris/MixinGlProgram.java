package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.Uniform;
import com.mojang.blaze3d.pipeline.BindGroupLayout;
import java.util.List;
import java.util.Map;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.StarterClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GlProgram.class})
public abstract class MixinGlProgram {
   @Shadow
   @Final
   private Map<String, Uniform> uniformsByName;
   @Shadow
   @Final
   private int programId;

   @Inject(
      method = {"setupBindGroupLayouts"},
      at = {@At("RETURN")}
   )
   private void physicsmod$registerIrisOceanUniformBlocks(List<BindGroupLayout> bindGroupLayouts, CallbackInfo info) {
      if (StarterClient.iris()) {
         Iris.registerProperUniforms((GlProgram)(Object)this, this.programId, this.uniformsByName);
      }
   }
}
