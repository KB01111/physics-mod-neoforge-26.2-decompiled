package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;

public class SDFSphere extends SDF {
   private double radius;

   public SDFSphere(double radius) {
      this.radius = radius;
   }

   public SDFSphere() {
   }

   @Override
   protected double calculateSDF(double x, double y, double z) {
      return Vector3d.length(x, y, z) - this.radius;
   }

   @Override
   public double getBounds() {
      return this.radius;
   }
}
