package net.diebuddies.jbox2d.dynamics;

import net.diebuddies.jbox2d.common.Vec2;

public class BodyDef {
   public BodyType type;
   public Object userData = null;
   public Vec2 position = new Vec2();
   public float angle = 0.0F;
   public Vec2 linearVelocity = new Vec2();
   public float angularVelocity = 0.0F;
   public float linearDamping = 0.0F;
   public float angularDamping = 0.0F;
   public boolean allowSleep = true;
   public boolean awake = true;
   public boolean fixedRotation = false;
   public boolean bullet = false;
   public boolean active;
   public float gravityScale;

   public BodyDef() {
      this.type = BodyType.STATIC;
      this.active = true;
      this.gravityScale = 1.0F;
   }

   public BodyType getType() {
      return this.type;
   }

   public void setType(BodyType type) {
      this.type = type;
   }

   public Object getUserData() {
      return this.userData;
   }

   public void setUserData(Object userData) {
      this.userData = userData;
   }

   public Vec2 getPosition() {
      return this.position;
   }

   public void setPosition(Vec2 position) {
      this.position = position;
   }

   public float getAngle() {
      return this.angle;
   }

   public void setAngle(float angle) {
      this.angle = angle;
   }

   public Vec2 getLinearVelocity() {
      return this.linearVelocity;
   }

   public void setLinearVelocity(Vec2 linearVelocity) {
      this.linearVelocity = linearVelocity;
   }

   public float getAngularVelocity() {
      return this.angularVelocity;
   }

   public void setAngularVelocity(float angularVelocity) {
      this.angularVelocity = angularVelocity;
   }

   public float getLinearDamping() {
      return this.linearDamping;
   }

   public void setLinearDamping(float linearDamping) {
      this.linearDamping = linearDamping;
   }

   public float getAngularDamping() {
      return this.angularDamping;
   }

   public void setAngularDamping(float angularDamping) {
      this.angularDamping = angularDamping;
   }

   public boolean isAllowSleep() {
      return this.allowSleep;
   }

   public void setAllowSleep(boolean allowSleep) {
      this.allowSleep = allowSleep;
   }

   public boolean isAwake() {
      return this.awake;
   }

   public void setAwake(boolean awake) {
      this.awake = awake;
   }

   public boolean isFixedRotation() {
      return this.fixedRotation;
   }

   public void setFixedRotation(boolean fixedRotation) {
      this.fixedRotation = fixedRotation;
   }

   public boolean isBullet() {
      return this.bullet;
   }

   public void setBullet(boolean bullet) {
      this.bullet = bullet;
   }

   public boolean isActive() {
      return this.active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public float getGravityScale() {
      return this.gravityScale;
   }

   public void setGravityScale(float gravityScale) {
      this.gravityScale = gravityScale;
   }
}
