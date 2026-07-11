package net.diebuddies.physics.verlet.constraints;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletStick;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.phys.Vec3;
import org.joml.Math;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;

public class FishingHookConstraint implements VerletConstraint {
   private Vector3d playerPosAsync = new Vector3d();
   private Vector3d hookPosAsync = new Vector3d();
   private Vector3d playerPos = new Vector3d();
   private Vector3d hookPos = new Vector3d();

   public FishingHookConstraint(VerletSimulation simulation, FishingHookRenderer fishingHookRenderer, FishingHook fishingHook, Player player, float tickDelta) {
      this.calculatePlayerAndHookPos(fishingHookRenderer, fishingHook, player, tickDelta);
      int pointCount = 48;
      double totalLength = ConfigClient.fishingLineLength;

      for (int i = 0; i < pointCount; i++) {
         float perc = (float)(i - 1) / (float)pointCount;
         Vector3d position = new Vector3d(
            Math.lerp(this.playerPos.x, this.hookPos.x, (double)perc),
            Math.lerp(this.playerPos.y, this.hookPos.y, (double)perc),
            Math.lerp(this.playerPos.z, this.hookPos.z, (double)perc)
         );
         VerletPoint point = new VerletPoint(position, i == 0 || i == pointCount - 1, ARGB.color(255, 0, 0, 0));
         point.uv.set(0.01F, 0.99F);
         simulation.addPoint(point);
      }

      for (int i = 0; i < pointCount - 1; i++) {
         simulation.addStick(new VerletStick(simulation.getPoints().get(i), simulation.getPoints().get(i + 1), totalLength / (double)pointCount));
         simulation.addLine(new VerletLine(simulation.getPoints().get(i), simulation.getPoints().get(i + 1)));
      }
   }

   public void calculatePlayerAndHookPos(FishingHookRenderer fishingHookRenderer, FishingHook fishingHook, Player player, float tickDelta) {
      float attackAnim = player.getAttackAnim(tickDelta);
      float attackAnim2 = Mth.sin((double)(Mth.sqrt(attackAnim) * (float) java.lang.Math.PI));
      Vec3 handPos = fishingHookRenderer.getPlayerHandPos(player, attackAnim2, tickDelta);
      double playerX = handPos.x;
      double playerY = handPos.y;
      double playerZ = handPos.z;
      double hookX = Mth.lerp((double)tickDelta, fishingHook.xo, fishingHook.getX());
      double hookY = Mth.lerp((double)tickDelta, fishingHook.yo, fishingHook.getY()) + 0.25;
      double hookZ = Mth.lerp((double)tickDelta, fishingHook.zo, fishingHook.getZ());
      this.playerPos.set(playerX, playerY, playerZ);
      this.hookPos.set(hookX, hookY, hookZ);
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      this.playerPosAsync.set(this.playerPos);
      this.hookPosAsync.set(this.hookPos);
      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      VerletPoint armPoint = simulation.getPoints().get(0);
      VerletPoint hookPoint = simulation.getPoints().get(simulation.getPoints().size() - 1);
      armPoint.position.set(this.playerPosAsync).sub(simulation.getOffset());
      hookPoint.position.set(this.hookPosAsync).sub(simulation.getOffset());
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
      armPoint.bufferPosition.set(this.playerPos).sub(simulation.getOffset());
      armPoint.bufferPrevPosition.set(armPoint.bufferPosition);
      hookPoint.bufferPosition.set(this.hookPos).sub(simulation.getOffset());
      hookPoint.bufferPrevPosition.set(hookPoint.bufferPosition);
   }
}
