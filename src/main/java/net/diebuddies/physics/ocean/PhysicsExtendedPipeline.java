package net.diebuddies.physics.ocean;

import com.mojang.blaze3d.opengl.GlProgram;

public interface PhysicsExtendedPipeline {
   GlProgram physicsmod$getOceanShader();

   GlProgram physicsmod$getOceanShadowShader();

   GlProgram physicsmod$getLiquidShader();

   GlProgram physicsmod$getLiquidShadowShader();

   GlProgram physicsmod$getSmokeShader();

   GlProgram physicsmod$getSmokeShadowShader();

   boolean physicsmod$renderOceanShadow();

   boolean physicsmod$renderLiquidShadow();

   boolean physicsmod$renderSmokeShadow();
}
