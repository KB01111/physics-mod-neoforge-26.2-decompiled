package net.diebuddies.mixins.ocean;

import com.mojang.blaze3d.opengl.GlProgram;
import java.util.Optional;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.ocean.PhysicsExtendedPipeline;
import net.diebuddies.physics.ocean.ProgramSetOcean;
import net.irisshaders.iris.helpers.FakeChainedJsonException;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.pipeline.programs.ShaderKey;
import net.irisshaders.iris.pipeline.programs.ShaderSupplier;
import net.irisshaders.iris.pipeline.transform.Patch;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shadows.ShadowRenderTargets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({IrisRenderingPipeline.class})
public class MixinNewWorldRenderingPipeline implements PhysicsExtendedPipeline {
   @Unique
   private ProgramSet physicsmod$programSet;
   @Unique
   private GlProgram physicsmod$oceanShader;
   @Unique
   private GlProgram physicsmod$oceanShadowShader;
   @Unique
   private GlProgram physicsmod$liquidShader;
   @Unique
   private GlProgram physicsmod$liquidShadowShader;
   @Unique
   private GlProgram physicsmod$smokeShader;
   @Unique
   private GlProgram physicsmod$smokeShadowShader;
   @Unique
   private boolean physicsmod$renderOceanShadow;
   @Unique
   private boolean physicsmod$renderLiquidShadow;
   @Unique
   private boolean physicsmod$renderSmokeShadow;
   @Shadow
   private ShadowRenderTargets shadowRenderTargets;

   @Shadow
   public ShaderSupplier createShader(String name, Optional<ProgramSource> source, ShaderKey key, Patch patchType) {
      return null;
   }

   @Shadow
   public ShaderSupplier createShadowShader(String name, Optional<ProgramSource> source, ShaderKey key, Patch patchType) {
      return null;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void constructor(ProgramSet programSet, CallbackInfo info) {
      this.physicsmod$programSet = programSet;

      try {
         Iris.compilingOceanShader.set(true);
         this.physicsmod$oceanShader = (GlProgram)(Object)this.createShader(
               "physics_ocean", ((ProgramSetOcean)programSet).getOceanSource().requireValid(), ShaderKey.TERRAIN_TRANSLUCENT, Patch.VANILLA
            )
            .shader()
            .get();
         this.physicsmod$renderOceanShadow = this.shadowRenderTargets != null;
         if (this.physicsmod$renderOceanShadow) {
            this.physicsmod$oceanShadowShader = (GlProgram)(Object)this.createShadowShader(
                  "physics_ocean_shadow", ((ProgramSetOcean)programSet).getOceanShadowSource().requireValid(), ShaderKey.SHADOW_TERRAIN_CUTOUT, Patch.VANILLA
               )
               .shader()
               .get();
         }

         Iris.compilingOceanShader.set(false);
         StarterClient.logger.info("constructed ocean shader successfully");
      } catch (Exception var7) {
         this.physicsmod$oceanShader = null;
         this.physicsmod$oceanShadowShader = null;
         Iris.compilingOceanShader.set(false);
         StarterClient.logger.info("failed constructing ocean shader");
         Iris.oceanError = "This shader (or shaderpack settings) is not supported by ocean physics!";
         if (var7 instanceof FakeChainedJsonException fake) {
            fake.getTrueException().printStackTrace();
         } else {
            var7.printStackTrace();
         }
      }

      try {
         Iris.compilingLiquidShader.set(true);
         this.physicsmod$liquidShader = (GlProgram)(Object)this.createShader(
               "physics_liquids", ((ProgramSetOcean)programSet).getLiquidsSource().requireValid(), ShaderKey.TERRAIN_TRANSLUCENT, Patch.VANILLA
            )
            .shader()
            .get();
         this.physicsmod$renderLiquidShadow = this.shadowRenderTargets != null;
         Iris.compilingLiquidShader.set(false);
         if (this.physicsmod$renderLiquidShadow) {
            Iris.compilingLiquidShadowShader.set(true);
            this.physicsmod$liquidShadowShader = (GlProgram)(Object)this.createShadowShader(
                  "physics_liquids_shadow",
                  ((ProgramSetOcean)programSet).getLiquidsShadowSource().requireValid(),
                  ShaderKey.SHADOW_TERRAIN_CUTOUT,
                  Patch.VANILLA
               )
               .shader()
               .get();
            Iris.compilingLiquidShadowShader.set(false);
         }

         StarterClient.logger.info("constructed liquids shader successfully");
      } catch (Exception var6) {
         this.physicsmod$liquidShader = null;
         this.physicsmod$liquidShadowShader = null;
         Iris.compilingLiquidShadowShader.set(false);
         Iris.compilingLiquidShader.set(false);
         StarterClient.logger.info("failed constructing liquids shader");
         Iris.liquidsError = "This shader (or shaderpack settings) is not supported by liquid physics!";
         if (var6 instanceof FakeChainedJsonException fake) {
            fake.getTrueException().printStackTrace();
         } else {
            var6.printStackTrace();
         }
      }

      try {
         Iris.compilingSmokeShader.set(true);
         this.physicsmod$smokeShader = (GlProgram)(Object)this.createShader(
               "physics_smoke", ((ProgramSetOcean)programSet).getSmokeSource().requireValid(), ShaderKey.ENTITIES_CUTOUT, Patch.VANILLA
            )
            .shader()
            .get();
         this.physicsmod$renderSmokeShadow = this.shadowRenderTargets != null;
         if (this.physicsmod$renderSmokeShadow) {
            this.physicsmod$smokeShadowShader = (GlProgram)(Object)this.createShadowShader(
                  "physics_smoke_shadow", ((ProgramSetOcean)programSet).getSmokeShadowSource().requireValid(), ShaderKey.SHADOW_ENTITIES_CUTOUT, Patch.VANILLA
               )
               .shader()
               .get();
         }

         Iris.compilingSmokeShader.set(false);
         StarterClient.logger.info("constructed smoke shader successfully");
      } catch (Exception var5) {
         this.physicsmod$smokeShader = null;
         this.physicsmod$smokeShadowShader = null;
         Iris.compilingSmokeShader.set(false);
         StarterClient.logger.info("failed constructing smoke shader");
         Iris.smokeError = "This shader (or shaderpack settings) is not supported by smoke physics!";
         if (var5 instanceof FakeChainedJsonException fake) {
            fake.getTrueException().printStackTrace();
         } else {
            var5.printStackTrace();
         }
      }
   }

   @Override
   public GlProgram physicsmod$getOceanShader() {
      return this.physicsmod$oceanShader;
   }

   @Override
   public GlProgram physicsmod$getOceanShadowShader() {
      return this.physicsmod$oceanShadowShader;
   }

   @Override
   public GlProgram physicsmod$getLiquidShader() {
      return this.physicsmod$liquidShader;
   }

   @Override
   public GlProgram physicsmod$getLiquidShadowShader() {
      return this.physicsmod$liquidShadowShader;
   }

   @Override
   public GlProgram physicsmod$getSmokeShader() {
      return this.physicsmod$smokeShader;
   }

   @Override
   public GlProgram physicsmod$getSmokeShadowShader() {
      return this.physicsmod$smokeShadowShader;
   }

   @Override
   public boolean physicsmod$renderOceanShadow() {
      return this.physicsmod$renderOceanShadow;
   }

   @Override
   public boolean physicsmod$renderLiquidShadow() {
      return this.physicsmod$renderLiquidShadow;
   }

   @Override
   public boolean physicsmod$renderSmokeShadow() {
      return this.physicsmod$renderSmokeShadow;
   }
}
