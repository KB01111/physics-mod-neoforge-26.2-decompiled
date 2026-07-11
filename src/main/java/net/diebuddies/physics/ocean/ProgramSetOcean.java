package net.diebuddies.physics.ocean;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;

public interface ProgramSetOcean {
   ProgramSource getOceanSource();

   ProgramSource getOceanShadowSource();

   ProgramSource getLiquidsSource();

   ProgramSource getLiquidsShadowSource();

   ProgramSource getSmokeSource();

   ProgramSource getSmokeShadowSource();
}
