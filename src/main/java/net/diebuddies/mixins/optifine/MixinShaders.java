package net.diebuddies.mixins.optifine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.compat.Optifine;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.opengl.Data;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.liquid.ShaderInjectionLiquids;
import net.diebuddies.physics.ocean.ShaderInjectionOcean;
import net.diebuddies.physics.smoke.ShaderInjectionSmoke;
import net.diebuddies.render.MainRenderer;
import net.diebuddies.util.ShaderFixes;
import net.diebuddies.util.ShaderType;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.optifine.shaders.Program;
import org.lwjgl.opengl.GL32C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(
   targets = {"net.optifine.shaders.Shaders"}
)
public class MixinShaders {
   @Inject(
      at = {@At("HEAD")},
      method = {"setupProgram"}
   )
   private static void physicsmod$getCompilingProgram(Program program, String vShaderPath, String gShaderPath, String fShaderPath, CallbackInfo info) {
      Optifine.compilingProgram = program;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createCompShader"}
   )
   private static void physicsmod$switchToCompStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.COMPUTE;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createVertShader"}
   )
   private static void physicsmod$switchToVertStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.VERTEX;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createGeomShader"}
   )
   private static void physicsmod$switchToGeomStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.GEOMETRY;
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"createFragShader"}
   )
   private static void physicsmod$switchToFragStage(@Coerce Object program, String filename, CallbackInfoReturnable<Integer> info) {
      Optifine.compileStage = ShaderType.FRAGMENT;
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"setCameraShadow"}
   )
   private static void physicsmod$setActiveMatrices(PoseStack matrixStack, Camera activeRenderInfo, float partialTicks, CallbackInfo info) {
      Optifine.shadowView.set(matrixStack.last().pose());
   }

   @Inject(
      method = {"setupProgram"},
      at = {@At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/vertex/VertexFormat;getElementAttributeNames()Ljava/util/List;",
         shift = Shift.AFTER
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private static void physicsmod$setupAttributes(
      Program program,
      String vShaderPath,
      String gShaderPath,
      String fShaderPath,
      CallbackInfo ci,
      int vShader,
      int gShader,
      int fShader,
      int programId,
      VertexFormat vertexFormat
   ) {
      if (program == Optifine.liquidProgram || program == Optifine.liquidShadowProgram) {
         GL32C.glBindAttribLocation(programId, Data.LIQUID_LIGHT.getAttribute(), "physics_light");
         GL32C.glBindAttribLocation(programId, Data.LIQUID_POS.getAttribute(), "physics_offset");
         GL32C.glBindAttribLocation(programId, Data.LIQUID_POS_NEW.getAttribute(), "physics_offsetNew");
         StarterClient.logger.info("binding optifine liquid attributes");
      } else if (program == Optifine.smokeProgram || program == Optifine.smokeShadowProgram) {
         GL32C.glBindAttribLocation(programId, Data.SMOKE_LIGHT.getAttribute(), "physics_light");
         GL32C.glBindAttribLocation(programId, Data.SMOKE_POS.getAttribute(), "physics_offset");
         GL32C.glBindAttribLocation(programId, Data.SMOKE_POS_NEW.getAttribute(), "physics_offsetNew");
         StarterClient.logger.info("binding optifine smoke attributes");
      } else if (program == Optifine.oceanProgram || program == Optifine.oceanShadowProgram) {
         GL32C.glBindAttribLocation(programId, Data.OCEAN_WAVINESS_SHADER.getAttribute(), "physics_waviness");
         StarterClient.logger.info("binding optifine ocean attributes");
      }
   }

   @ModifyVariable(
      method = {"shaderSource"},
      at = @At("HEAD"),
      ordinal = 0
   )
   private static String physicsmod$applyOceanChanges(String code) {
      if (Optifine.compilingProgram == Optifine.oceanProgram || Optifine.compilingProgram == Optifine.oceanShadowProgram) {
         boolean shadow = Optifine.compilingProgram == Optifine.oceanShadowProgram;
         if (Optifine.compileStage == ShaderType.VERTEX) {
            code = ShaderFixes.preprocessOptifineSource(code);
            code = ShaderInjectionOcean.getVertexSource(code);
         } else if (Optifine.compileStage == ShaderType.FRAGMENT) {
            code = ShaderFixes.preprocessOptifineSource(code);
            code = ShaderInjectionOcean.getFragmentSource(code, shadow);
         }
      } else if (Optifine.compilingProgram == Optifine.liquidProgram || Optifine.compilingProgram == Optifine.liquidShadowProgram) {
         boolean shadow = Optifine.compilingProgram == Optifine.liquidShadowProgram;
         if (Optifine.compileStage == ShaderType.VERTEX) {
            code = ShaderFixes.preprocessOptifineSource(code);
            if (shadow) {
               code = ShaderInjectionLiquids.getVertexShadowSource(code);
            } else {
               code = ShaderInjectionLiquids.getVertexSource(code);
            }
         } else if (Optifine.compileStage == ShaderType.FRAGMENT) {
            code = ShaderFixes.preprocessOptifineSource(code);
            if (shadow) {
               code = ShaderInjectionLiquids.getFragmentShadowSource(code);
            } else {
               code = ShaderInjectionLiquids.getFragmentSource(code);
            }
         }
      } else if (Optifine.compilingProgram == Optifine.smokeProgram || Optifine.compilingProgram == Optifine.smokeShadowProgram) {
         boolean shadow = Optifine.compilingProgram == Optifine.smokeShadowProgram;
         if (Optifine.compileStage == ShaderType.VERTEX) {
            code = ShaderFixes.preprocessOptifineSource(code);
            if (shadow) {
               code = ShaderInjectionSmoke.getVertexShadowSource(code);
            } else {
               code = ShaderInjectionSmoke.getVertexSource(code);
            }
         } else if (Optifine.compileStage == ShaderType.FRAGMENT) {
            code = ShaderFixes.preprocessOptifineSource(code);
            if (shadow) {
               code = ShaderInjectionSmoke.getFragmentShadowSource(code);
            } else {
               code = ShaderInjectionSmoke.getFragmentSource(code);
            }
         }
      }

      return code;
   }

   @Inject(
      at = {@At("RETURN")},
      method = {"renderFinal"}
   )
   private static void physicsmod$renderVolumetricShadow(CallbackInfo info) {
      if (Optifine.isUsingShadersNoInternal()) {
         MainRenderer mainRenderer = ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).physicsmod$getMainRenderer();
         mainRenderer.renderVolumetricSmoke(Minecraft.getInstance().level);
      }
   }
}
