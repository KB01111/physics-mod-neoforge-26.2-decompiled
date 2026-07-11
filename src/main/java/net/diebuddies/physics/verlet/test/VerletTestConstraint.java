package net.diebuddies.physics.verlet.test;

import com.mojang.blaze3d.vertex.PoseStack;

public interface VerletTestConstraint {
   boolean initAsyncData(VerletSimulationTest var1);

   void updateBefore(double var1, VerletSimulationTest var3);

   void updateAfter(double var1, VerletSimulationTest var3);

   void subStep(double var1, VerletSimulationTest var3);

   void renderBefore(PoseStack var1, double var2, VerletSimulationTest var4);

   void render(PoseStack var1, double var2, VerletSimulationTest var4);

   void renderAfter(PoseStack var1, double var2, VerletSimulationTest var4);
}
