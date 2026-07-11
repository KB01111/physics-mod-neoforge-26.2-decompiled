package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class RevoluteJointDef extends JointDef {
   public Vec2 localAnchorA = new Vec2(0.0F, 0.0F);
   public Vec2 localAnchorB = new Vec2(0.0F, 0.0F);
   public float referenceAngle = 0.0F;
   public boolean enableLimit;
   public float lowerAngle = 0.0F;
   public float upperAngle = 0.0F;
   public boolean enableMotor;
   public float motorSpeed;
   public float maxMotorTorque = 0.0F;

   public RevoluteJointDef() {
      super(JointType.REVOLUTE);
      this.motorSpeed = 0.0F;
      this.enableLimit = false;
      this.enableMotor = false;
   }

   public void initialize(Body b1, Body b2, Vec2 anchor) {
      this.bodyA = b1;
      this.bodyB = b2;
      this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
      this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
      this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
   }
}
