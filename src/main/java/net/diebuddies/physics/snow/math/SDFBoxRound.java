package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;

public class SDFBoxRound extends SDF {
   private Vector3d halfSize;
   private double radius;
   private double bounds;

   public SDFBoxRound(double halfWidth, double halfHeight, double halfDepth, double radius) {
      this.halfSize = new Vector3d(halfWidth, halfHeight, halfDepth);
      this.radius = radius;
      this.bounds = this.halfSize.length() + radius;
   }

   public SDFBoxRound() {
      this.halfSize = new Vector3d();
   }

   @Override
   protected double calculateSDF(double x, double y, double z) {
      double tmpX = Math.abs(x) - this.halfSize.x;
      double tmpY = Math.abs(y) - this.halfSize.y;
      double tmpZ = Math.abs(z) - this.halfSize.z;
      double qmX = Math.max(tmpX, 0.0);
      double qmY = Math.max(tmpY, 0.0);
      double qmZ = Math.max(tmpZ, 0.0);
      return Vector3d.length(qmX, qmY, qmZ) + Math.min(Math.max(tmpX, Math.max(tmpY, tmpZ)), 0.0) - this.radius;
   }

   public void setHalfSize(Vector3d halfSize) {
      this.halfSize.set(halfSize);
      this.bounds = halfSize.length() + this.radius;
   }

   public Vector3d getHalfSize() {
      return this.halfSize;
   }

   @Override
   public double getBounds() {
      return this.bounds;
   }
}
