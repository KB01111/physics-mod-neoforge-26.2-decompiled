package net.diebuddies.mixins.liquid;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.compat.Iris;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.StarterClient;
import org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {VertexFormat.class},
   priority = 1100
)
public class MixinProgramManager {
   @Inject(
      at = {@At("HEAD")},
      method = {"bindAttributesIris"}
   )
   private void physicsmod$changeLiquidAttributes(boolean isFallback, int programId, CallbackInfo info) {
      if (StarterClient.iris()) {
         if (Iris.compilingLiquidShadowShader.get()) {
            GL32C.glBindAttribLocation(programId, Data.LIQUID_LIGHT.getAttribute(), "physics_light");
            GL32C.glBindAttribLocation(programId, Data.LIQUID_POS.getAttribute(), "physics_offset");
            GL32C.glBindAttribLocation(programId, Data.LIQUID_POS_NEW.getAttribute(), "physics_offsetNew");
            StarterClient.logger.info("binding liquid attributes");
         } else if (Iris.compilingSmokeShader.get()) {
            GL32C.glBindAttribLocation(programId, Data.SMOKE_LIGHT.getAttribute(), "physics_light");
            GL32C.glBindAttribLocation(programId, Data.SMOKE_POS.getAttribute(), "physics_offset");
            GL32C.glBindAttribLocation(programId, Data.SMOKE_POS_NEW.getAttribute(), "physics_offsetNew");
            StarterClient.logger.info("binding smoke attributes");
         } else if (Iris.compilingOceanShader.get()) {
            GL32C.glBindAttribLocation(programId, Data.OCEAN_WAVINESS_SHADER.getAttribute(), "physics_waviness");
            StarterClient.logger.info("binding ocean attributes");
         }
      }
   }
}
