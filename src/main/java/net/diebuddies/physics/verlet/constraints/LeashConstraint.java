package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.minecraft.client.renderer.entity.state.EntityRenderState.LeashState;
import net.minecraft.util.ARGB;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

public class LeashConstraint implements VerletConstraint {
   private Vector3d leashOriginAsync = new Vector3d();
   private Vector3d leashTargetAsync = new Vector3d();
   private Vector3d leashOrigin = new Vector3d();
   private Vector3d leashTarget = new Vector3d();

   public LeashConstraint(VerletSimulation simulation, LeashState leashState) {
      this.leashOrigin = new Vector3d(leashState.start.x, leashState.start.y, leashState.start.z);
      this.leashTarget = new Vector3d(leashState.end.x, leashState.end.y, leashState.end.z);
      int pointCount = 20;
      double totalLength = ConfigClient.leashLength;

      for (int i = 0; i < pointCount; i++) {
         float perc = (float)(i - 1) / (float)pointCount;
         Vector3d position = new Vector3d(
            Math.lerp(this.leashOrigin.x, this.leashTarget.x, (double)perc),
            Math.lerp(this.leashOrigin.y, this.leashTarget.y, (double)perc),
            Math.lerp(this.leashOrigin.z, this.leashTarget.z, (double)perc)
         );
         float colMod = i % 2 == 0 ? 0.7F : 1.0F;
         float r = 0.5F * colMod;
         float g = 0.4F * colMod;
         float b = 0.3F * colMod;
         VerletPoint point = new VerletPoint(position, i == 0 || i == pointCount - 1, ARGB.colorFromFloat(1.0F, r, g, b));
         point.uv.set(0.01F, 0.99F);
         simulation.addPoint(point);
      }

      for (int i = 0; i < pointCount - 1; i++) {
         simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / (double)pointCount));
         simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
      }
   }

   public void setLeashState(LeashState leashState) {
      this.leashOrigin.set(leashState.start.x, leashState.start.y, leashState.start.z);
      this.leashTarget.set(leashState.end.x, leashState.end.y, leashState.end.z);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      this.leashOriginAsync.set(this.leashOrigin);
      this.leashTargetAsync.set(this.leashTarget);
      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      VerletPoint originPoint = simulation.getPoints().get(0);
      VerletPoint targetPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      originPoint.position.set(this.leashOriginAsync).sub(simulation.getOffset());
      targetPoint.position.set(this.leashTargetAsync).sub(simulation.getOffset());
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   @Override
   public void renderBefore(Matrix4fStack matrixStack, double delta, VerletSimulation simulation) {
      VerletPoint armPoint = simulation.getPoints().get(0);
      VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      armPoint.bufferPosition.set(this.leashOrigin).sub(simulation.getOffset());
      armPoint.bufferPrevPosition.set(armPoint.bufferPosition);
      hookPoint.bufferPosition.set(this.leashTarget).sub(simulation.getOffset());
      hookPoint.bufferPrevPosition.set(hookPoint.bufferPosition);
   }
}
