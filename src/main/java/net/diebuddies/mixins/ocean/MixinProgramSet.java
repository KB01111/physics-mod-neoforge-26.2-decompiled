package net.diebuddies.mixins.ocean;

import java.util.EnumMap;
import java.util.function.Function;
import net.diebuddies.compat.Iris;
import net.diebuddies.physics.liquid.ShaderInjectionLiquids;
import net.diebuddies.physics.ocean.ProgramSetOcean;
import net.diebuddies.physics.ocean.ShaderInjectionOcean;
import net.diebuddies.physics.smoke.ShaderInjectionSmoke;
import net.diebuddies.util.ShaderType;
import net.irisshaders.iris.shaderpack.ShaderPack;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.loading.ProgramId;
import net.irisshaders.iris.shaderpack.programs.ProgramSet;
import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import net.irisshaders.iris.shaderpack.properties.ShaderProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ProgramSet.class})
public class MixinProgramSet implements ProgramSetOcean {
   @Shadow
   @Final
   private EnumMap<ProgramId, ProgramSource> gbufferPrograms;
   @Unique
   private ProgramSource physicsmod$oceanSource;
   @Unique
   private ProgramSource physicsmod$oceanShadowSource;
   @Unique
   private ProgramSource physicsmod$liquidsSource;
   @Unique
   private ProgramSource physicsmod$liquidsShadowSource;
   @Unique
   private ProgramSource physicsmod$smokeSource;
   @Unique
   private ProgramSource physicsmod$smokeShadowSource;

   @Inject(
      at = {@At("TAIL")},
      method = {"<init>"}
   )
   public void physicsmod$createCustomShaders(
      AbsolutePackPath directory, Function<AbsolutePackPath, String> sourceProvider, ShaderProperties shaderProperties, ShaderPack pack, CallbackInfo info
   ) {
      String customOceanVertex = sourceProvider.apply(directory.resolve("physics_ocean_v2.vsh"));
      String customOceanGeometry = sourceProvider.apply(directory.resolve("physics_ocean_v2.gsh"));
      String customOceanControlTess = sourceProvider.apply(directory.resolve("physics_ocean_v2.tcs"));
      String customOceanEvalTess = sourceProvider.apply(directory.resolve("physics_ocean_v2.tes"));
      String customOceanFragment = sourceProvider.apply(directory.resolve("physics_ocean_v2.fsh"));
      if (customOceanVertex != null) {
         Iris.preprocessOceanStage.set(null);
         this.physicsmod$oceanSource = new ProgramSource(
            "gbuffers_water",
            customOceanVertex,
            customOceanGeometry,
            customOceanControlTess,
            customOceanEvalTess,
            customOceanFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Water.getBlendModeOverride()
         );
      } else {
         Iris.preprocessOceanStage.set(ShaderType.VERTEX);
         String vertexSource = sourceProvider.apply(directory.resolve("gbuffers_water.vsh"));
         Iris.preprocessOceanStage.set(ShaderType.GEOMETRY);
         String geometrySource = sourceProvider.apply(directory.resolve("gbuffers_water.gsh"));
         Iris.preprocessOceanStage.set(ShaderType.FRAGMENT);
         String fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_water.fsh"));
         this.physicsmod$oceanSource = new ProgramSource(
            "gbuffers_water",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Water.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$oceanSource).setVertexSource(ShaderInjectionOcean.getVertexSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$oceanSource).setFragmentSource(ShaderInjectionOcean.getFragmentSource(fragmentSource, false));
      }

      String customOceanShadowVertex = sourceProvider.apply(directory.resolve("physics_ocean_shadow_v2.vsh"));
      String customOceanShadowGeometry = sourceProvider.apply(directory.resolve("physics_ocean_shadow_v2.gsh"));
      String customOceanShadowControlTess = sourceProvider.apply(directory.resolve("physics_ocean_shadow_v2.tcs"));
      String customOceanShadowEvalTess = sourceProvider.apply(directory.resolve("physics_ocean_shadow_v2.tes"));
      String customOceanShadowFragment = sourceProvider.apply(directory.resolve("physics_ocean_shadow_v2.fsh"));
      if (customOceanShadowVertex != null) {
         Iris.preprocessOceanStage.set(null);
         this.physicsmod$oceanShadowSource = new ProgramSource(
            "shadow_water",
            customOceanShadowVertex,
            customOceanShadowGeometry,
            customOceanShadowControlTess,
            customOceanShadowEvalTess,
            customOceanShadowFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowWater.getBlendModeOverride()
         );
      } else {
         Iris.preprocessOceanStage.set(ShaderType.VERTEX);
         String vertexSource = sourceProvider.apply(directory.resolve("shadow.vsh"));
         Iris.preprocessOceanStage.set(ShaderType.GEOMETRY);
         String geometrySource = sourceProvider.apply(directory.resolve("shadow.gsh"));
         Iris.preprocessOceanStage.set(ShaderType.FRAGMENT);
         String fragmentSource = sourceProvider.apply(directory.resolve("shadow.fsh"));
         this.physicsmod$oceanShadowSource = new ProgramSource(
            "shadow_water",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowWater.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$oceanShadowSource).setVertexSource(ShaderInjectionOcean.getVertexSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$oceanShadowSource).setFragmentSource(ShaderInjectionOcean.getFragmentSource(fragmentSource, true));
      }

      Iris.preprocessOceanStage.set(null);
      String customLiquidVertex = sourceProvider.apply(directory.resolve("physics_liquid.vsh"));
      String customLiquidGeometry = sourceProvider.apply(directory.resolve("physics_liquid.gsh"));
      String customLiquidControlTess = sourceProvider.apply(directory.resolve("physics_liquid.tcs"));
      String customLiquidEvalTess = sourceProvider.apply(directory.resolve("physics_liquid.tes"));
      String customLiquidFragment = sourceProvider.apply(directory.resolve("physics_liquid.fsh"));
      if (customLiquidVertex != null) {
         this.physicsmod$liquidsSource = new ProgramSource(
            "gbuffers_water",
            customLiquidVertex,
            customLiquidGeometry,
            customLiquidControlTess,
            customLiquidEvalTess,
            customLiquidFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Water.getBlendModeOverride()
         );
      } else {
         String vertexSource = sourceProvider.apply(directory.resolve("gbuffers_water.vsh"));
         String geometrySource = sourceProvider.apply(directory.resolve("gbuffers_water.gsh"));
         String fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_water.fsh"));
         this.physicsmod$liquidsSource = new ProgramSource(
            "gbuffers_water",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Water.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$liquidsSource).setVertexSource(ShaderInjectionLiquids.getVertexSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$liquidsSource).setFragmentSource(ShaderInjectionLiquids.getFragmentSource(fragmentSource));
      }

      String customLiquidShadowVertex = sourceProvider.apply(directory.resolve("physics_liquid_shadow.vsh"));
      String customLiquidShadowGeometry = sourceProvider.apply(directory.resolve("physics_liquid_shadow.gsh"));
      String customLiquidShadowControlTess = sourceProvider.apply(directory.resolve("physics_liquid_shadow.tcs"));
      String customLiquidShadowEvalTess = sourceProvider.apply(directory.resolve("physics_liquid_shadow.tes"));
      String customLiquidShadowFragment = sourceProvider.apply(directory.resolve("physics_liquid_shadow.fsh"));
      if (customLiquidShadowVertex != null) {
         this.physicsmod$liquidsShadowSource = new ProgramSource(
            "shadow_water",
            customLiquidShadowVertex,
            customLiquidShadowGeometry,
            customLiquidShadowControlTess,
            customLiquidShadowEvalTess,
            customLiquidShadowFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowWater.getBlendModeOverride()
         );
      } else {
         String vertexSource = sourceProvider.apply(directory.resolve("shadow.vsh"));
         String geometrySource = sourceProvider.apply(directory.resolve("shadow.gsh"));
         String fragmentSource = sourceProvider.apply(directory.resolve("shadow.fsh"));
         this.physicsmod$liquidsShadowSource = new ProgramSource(
            "shadow_water",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowWater.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$liquidsShadowSource).setVertexSource(ShaderInjectionLiquids.getVertexShadowSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$liquidsShadowSource).setFragmentSource(ShaderInjectionLiquids.getFragmentShadowSource(fragmentSource));
      }

      String customSmokeVertex = sourceProvider.apply(directory.resolve("physics_smoke.vsh"));
      String customSmokeGeometry = sourceProvider.apply(directory.resolve("physics_smoke.gsh"));
      String customSmokeControlTess = sourceProvider.apply(directory.resolve("physics_smoke.tcs"));
      String customSmokeEvalTess = sourceProvider.apply(directory.resolve("physics_smoke.tes"));
      String customSmokeFragment = sourceProvider.apply(directory.resolve("physics_smoke.fsh"));
      if (customLiquidVertex != null) {
         this.physicsmod$smokeSource = new ProgramSource(
            "gbuffers_entities",
            customSmokeVertex,
            customSmokeGeometry,
            customSmokeControlTess,
            customSmokeEvalTess,
            customSmokeFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Entities.getBlendModeOverride()
         );
      } else {
         String vertexSource = sourceProvider.apply(directory.resolve("gbuffers_entities.vsh"));
         String geometrySource = sourceProvider.apply(directory.resolve("gbuffers_entities.gsh"));
         String fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_entities.fsh"));
         if (fragmentSource == null) {
            vertexSource = sourceProvider.apply(directory.resolve("gbuffers_textured_lit.vsh"));
            geometrySource = sourceProvider.apply(directory.resolve("gbuffers_textured_lit.gsh"));
            fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_textured_lit.fsh"));
            if (fragmentSource == null) {
               vertexSource = sourceProvider.apply(directory.resolve("gbuffers_textured.vsh"));
               geometrySource = sourceProvider.apply(directory.resolve("gbuffers_textured.gsh"));
               fragmentSource = sourceProvider.apply(directory.resolve("gbuffers_textured.fsh"));
            }
         }

         this.physicsmod$smokeSource = new ProgramSource(
            "gbuffers_entities",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.Entities.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$smokeSource).setVertexSource(ShaderInjectionSmoke.getVertexSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$smokeSource).setFragmentSource(ShaderInjectionSmoke.getFragmentSource(fragmentSource));
      }

      String customSmokeShadowVertex = sourceProvider.apply(directory.resolve("physics_smoke_shadow.vsh"));
      String customSmokeShadowGeometry = sourceProvider.apply(directory.resolve("physics_smoke_shadow.gsh"));
      String customSmokeShadowControlTess = sourceProvider.apply(directory.resolve("physics_smoke_shadow.tcs"));
      String customSmokeShadowEvalTess = sourceProvider.apply(directory.resolve("physics_smoke_shadow.tes"));
      String customSmokeShadowFragment = sourceProvider.apply(directory.resolve("physics_smoke_shadow.fsh"));
      if (customLiquidShadowVertex != null) {
         this.physicsmod$smokeShadowSource = new ProgramSource(
            "shadow_entities",
            customSmokeShadowVertex,
            customSmokeShadowGeometry,
            customSmokeShadowControlTess,
            customSmokeShadowEvalTess,
            customSmokeShadowFragment,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowEntities.getBlendModeOverride()
         );
      } else {
         String vertexSource = sourceProvider.apply(directory.resolve("shadow.vsh"));
         String geometrySource = sourceProvider.apply(directory.resolve("shadow.gsh"));
         String fragmentSource = sourceProvider.apply(directory.resolve("shadow.fsh"));
         this.physicsmod$smokeShadowSource = new ProgramSource(
            "shadow_entities",
            vertexSource,
            geometrySource,
            null,
            null,
            fragmentSource,
            (ProgramSet)(Object)this,
            shaderProperties,
            ProgramId.ShadowEntities.getBlendModeOverride()
         );
         ((MixinProgramSource)(Object)this.physicsmod$smokeShadowSource).setVertexSource(ShaderInjectionSmoke.getVertexShadowSource(vertexSource));
         ((MixinProgramSource)(Object)this.physicsmod$smokeShadowSource).setFragmentSource(ShaderInjectionSmoke.getFragmentShadowSource(fragmentSource));
      }
   }

   @Override
   public ProgramSource getOceanSource() {
      return this.physicsmod$oceanSource;
   }

   @Override
   public ProgramSource getOceanShadowSource() {
      return this.physicsmod$oceanShadowSource;
   }

   @Override
   public ProgramSource getLiquidsSource() {
      return this.physicsmod$liquidsSource;
   }

   @Override
   public ProgramSource getLiquidsShadowSource() {
      return this.physicsmod$liquidsShadowSource;
   }

   @Override
   public ProgramSource getSmokeSource() {
      return this.physicsmod$smokeSource;
   }

   @Override
   public ProgramSource getSmokeShadowSource() {
      return this.physicsmod$smokeShadowSource;
   }
}
