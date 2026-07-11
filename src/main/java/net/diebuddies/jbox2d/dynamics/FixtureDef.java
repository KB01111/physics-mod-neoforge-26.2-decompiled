package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.collision.shapes.Shape;

public class FixtureDef {
   public Shape shape = null;
   public Object userData;
   public float friction;
   public float restitution;
   public float density;
   public boolean isSensor;
   public Filter filter;

   public FixtureDef() {
      this.shape = null;
      this.userData = null;
      this.friction = 0.2F;
      this.restitution = 0.0F;
      this.density = 0.0F;
      this.filter = new Filter();
      this.isSensor = false;
   }

   public Shape getShape() {
      return this.shape;
   }

   public void setShape(Shape shape) {
      this.shape = shape;
   }

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object userData) {
      this.userData = userData;
   }

   public float getFriction() {
      return this.friction;
   }

   public void setFriction(float friction) {
      this.friction = friction;
   }

   public float getRestitution() {
      return this.restitution;
   }

   public void setRestitution(float restitution) {
      this.restitution = restitution;
   }

   public float getDensity() {
      return this.density;
   }

   public void setDensity(float density) {
      this.density = density;
   }

   public boolean isSensor() {
      return this.isSensor;
   }

   public void setSensor(boolean isSensor) {
      this.isSensor = isSensor;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public void setFilter(Filter filter) {
      this.filter = filter;
   }
}
