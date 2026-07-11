package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import java.lang.reflect.InvocationTargetException;
import net.diebuddies.bridge.ReflectionsForge;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletHelper;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

public class ClosestPlayerConstraint implements VerletConstraint {
   private Player player;
   private Level level;
   private HumanoidModel model;
   private ModelCube[] modelCubes = new ModelCube[6];
   private VerletHelper helper = new VerletHelper();
   private double playerx;
   private double playery;
   private double playerz;
   private Vector3d invPoint = new Vector3d();
   private Matrix4d transform = new Matrix4d();
   private Matrix4d invTransform = new Matrix4d();
   private PoseStack modelMatrix = new PoseStack();
   private Quaternionf tmpQuat = new Quaternionf();

   public ClosestPlayerConstraint(Level level) {
      this.level = level;

      for (int i = 0; i < this.modelCubes.length; i++) {
         this.modelCubes[i] = new ModelCube();
      }
   }

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      Vector3d offset = simulation.getOffset();
      double px = offset.x;
      double py = offset.y;
      double pz = offset.z;
      if (simulation.getPoints().size() > 0) {
         Vector3d pos = simulation.getPoints().get(0).position;
         px += pos.x;
         py += pos.y;
         pz += pos.z;
      }

      this.player = this.level.getNearestPlayer(px, py, pz, 10.0, false);
      if (this.player != null) {
         LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(this.player);
         this.model = (HumanoidModel)renderer.getModel();

         try {
            HumanoidRenderState state = (HumanoidRenderState)renderer.createRenderState(this.player, 1.0F);
            this.modelCubes[0].part = this.model.hat;
            this.modelCubes[1].part = this.model.body;
            this.modelCubes[2].part = this.model.rightArm;
            this.modelCubes[3].part = this.model.leftArm;
            this.modelCubes[4].part = this.model.rightLeg;
            this.modelCubes[5].part = this.model.leftLeg;

            for (int i = 0; i < this.modelCubes.length; i++) {
               this.modelCubes[i].pose = this.modelCubes[i].part.storePose();
               this.modelCubes[i].updateHitbox();
            }

            this.modelMatrix.pushPose();
            this.model.setupAnim(state);
            this.playerTransformation(this.modelMatrix, simulation, this.player, 1.0F, 1.0F);

            for (int i = 0; i < this.modelCubes.length; i++) {
               ModelCube modelCube = this.modelCubes[i];
               Matrix4f currentPose = this.modelMatrix.last().pose();
               modelCube.transform.set(currentPose);
               this.translateAndRotate(modelCube.transform, modelCube.pose);
            }

            this.modelMatrix.popPose();
         } catch (Exception var15) {
         }
      }

      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      for (int i = 0; i < this.modelCubes.length; i++) {
         this.modelCubes[i].updateTransformation();
      }
   }

   @Override
   public void subStep(double percent, VerletSimulation simulation) {
      if (this.player != null) {
         this.doCollisionCheck(percent, simulation);
      }
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
   }

   private void playerTransformation(PoseStack modelMatrix, VerletSimulation simulation, Player player, float tickDelta, float animationProgress) {
      LivingEntityRenderer renderer = (LivingEntityRenderer)Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

      try {
         LivingEntityRenderState state = (LivingEntityRenderState)renderer.createRenderState(player, tickDelta);
         this.playerx = Mth.lerp((double)tickDelta, player.xOld, player.getX());
         this.playery = Mth.lerp((double)tickDelta, player.yOld, player.getY());
         this.playerz = Mth.lerp((double)tickDelta, player.zOld, player.getZ());
         this.playerx = this.playerx - simulation.getOffset().x;
         this.playery = this.playery - simulation.getOffset().y;
         this.playerz = this.playerz - simulation.getOffset().z;
         Vec3 positionOffset = renderer.getRenderOffset(state);
         modelMatrix.translate(positionOffset.x + this.playerx, positionOffset.y + this.playery, positionOffset.z + this.playerz);
         float g = Mth.rotLerp(tickDelta, player.yHeadRotO, player.yHeadRot);
         float yaw = ModelPartConstraint.solveBodyRot(player, g, tickDelta);
         if (player.getPose() == Pose.SLEEPING) {
            Direction direction = player.getBedOrientation();
            if (direction != null) {
               float eyeHeight = player.getEyeHeight(Pose.STANDING) - 0.1F;
               modelMatrix.translate((double)((float)(-direction.getStepX()) * eyeHeight), 0.0, (double)((float)(-direction.getStepZ()) * eyeHeight));
            }
         }

         try {
            ReflectionsForge.setupRotations.invoke(renderer, state, modelMatrix, yaw, tickDelta);
         } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException var13) {
            var13.printStackTrace();
         }

         modelMatrix.scale(-1.0F, -1.0F, 1.0F);
         modelMatrix.scale(0.9375F, 0.9375F, 0.9375F);
         modelMatrix.translate(0.0, -1.501F, 0.0);
      } catch (Exception var14) {
      }
   }

   private void doCollisionCheck(double percent, VerletSimulation simulation) {
   }

   public void translateAndRotate(Matrix4d transform, PartPose pose) {
      transform.translate((double)(pose.x() / 16.0F), (double)(pose.y() / 16.0F), (double)(pose.z() / 16.0F));
      if (pose.zRot() != 0.0F) {
         transform.rotate(this.tmpQuat.rotationZ(pose.zRot()));
      }

      if (pose.yRot() != 0.0F) {
         transform.rotate(this.tmpQuat.rotationY(pose.yRot()));
      }

      if (pose.xRot() != 0.0F) {
         transform.rotate(this.tmpQuat.rotationX(pose.xRot()));
      }
   }
}
