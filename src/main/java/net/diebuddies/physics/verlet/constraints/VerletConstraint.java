package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.joml.Matrix4fStack;

public interface VerletConstraint {
   default boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      return false;
   }

   default void updateBefore(double delta, VerletSimulation simulation) {
   }

   default void updateAfter(double delta, VerletSimulation simulation) {
   }

   default void preSubStep(double percent, VerletSimulation simulation) {
   }

   default void subStep(double percent, VerletSimulation simulation) {
   }

   default void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }

   default void render(Matrix4fStack matrixStack, double delta, SubmitNodeCollector submitNodeStorage, VerletSimulation simulation) {
   }

   default void renderAfter(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
   }
}
