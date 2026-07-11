package net.diebuddies.physics.snow.math;

import org.joml.Matrix4d;
import org.joml.Vector3d;

public abstract class SDF {
   private Matrix4d transformation;
   private Matrix4d transformationInverted;
   private Vector3d tmp = new Vector3d();
   private boolean priority;

   public SDF() {
      this.transformation = new Matrix4d();
      this.transformationInverted = new Matrix4d();
   }

   public double getDistance(double x, double y, double z) {
      this.transformationInverted.transformPosition(x, y, z, this.tmp);
      return this.calculateSDF(this.tmp.x, this.tmp.y, this.tmp.z);
   }

   public double getDistance(Vector3d position) {
      return this.getDistance(position.x, position.y, position.z);
   }

   public void setTransformation(Matrix4d transformation) {
      this.transformation.set(transformation);
      this.transformationInverted.set(transformation).invert();
   }

   public double getX() {
      return this.transformation.m30();
   }

   public double getY() {
      return this.transformation.m31();
   }

   public double getZ() {
      return this.transformation.m32();
   }

   public void setPriority(boolean priority) {
      this.priority = priority;
   }

   public boolean hasPriority() {
      return this.priority;
   }

   protected abstract double calculateSDF(double var1, double var3, double var5);

   public abstract double getBounds();
}
