package net.diebuddies.physics.verlet.constraints;

import com.mojang.math.Axis;
import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.joml.Matrix4d;
import org.joml.Matrix4fStack;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class OceanPhysicsDisplacementConstraint extends RenderConstraint {
   private Entity entity;
   private Matrix4d gravityTransformation;
   private Vector3d gravity;

   public OceanPhysicsDisplacementConstraint(Entity entity) {
      this.entity = entity;
      this.gravityTransformation = new Matrix4d();
      this.gravity = new Vector3d();
   }

   @Override
   public void render(Matrix4fStack matrixStack, double renderPercent, SubmitNodeCollector submitNodeStorage, VerletSimulation simulation) {
      super.render(matrixStack, renderPercent, submitNodeStorage, simulation);
   }

   private void calculateGravityTransformation(double renderPercent) {
      float actualYRot = 0.0F;
      Entity vehicle = this.entity.getVehicle();
      EntityOcean entityOcean = (EntityOcean)this.entity;
      if (this.entity instanceof LivingEntity living) {
         actualYRot = Mth.rotLerp((float)renderPercent, living.yBodyRotO, living.yBodyRot);
      } else {
         actualYRot = this.entity.getViewYRot((float)renderPercent);
      }

      float currentYRot = (float)(-Math.toRadians((double)(actualYRot - (float) Math.PI)));
      double forwardZ = Math.cos((double)currentYRot);
      double forwardX = Math.sin((double)currentYRot);
      double leftZ = -forwardX;
      double roll = entityOcean.getPhysicsRoll((float)renderPercent);
      double pitch = entityOcean.getPhysicsPitch((float)renderPercent);
      float diffRot = 0.0F;
      if (vehicle != null && vehicle instanceof AbstractBoat) {
         diffRot = vehicle.getViewYRot((float)renderPercent) - actualYRot;
      }

      this.gravityTransformation.identity();
      this.gravityTransformation.rotate(Axis.YP.rotationDegrees(-diffRot));
      this.gravityTransformation.rotate(Axis.of(new Vector3f((float)forwardX, 0.0F, (float)forwardZ)).rotationDegrees((float)(-Math.toDegrees(roll))));
      this.gravityTransformation.rotate(Axis.of(new Vector3f((float)forwardZ, 0.0F, (float)leftZ)).rotationDegrees((float)Math.toDegrees(pitch)));
      this.gravityTransformation.invert();
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      super.updateBefore(delta, simulation);
      this.gravity.set(simulation.getGravity());
      this.gravityTransformation.transformDirection(simulation.getGravity());
   }

   @Override
   public void updateAfter(double delta, VerletSimulation simulation) {
      super.updateAfter(delta, simulation);
      simulation.setGravity(this.gravity);
   }
}
