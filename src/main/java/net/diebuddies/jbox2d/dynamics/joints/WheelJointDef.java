package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class WheelJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2();
   public final Vec2 localAnchorB = new Vec2();
   public final Vec2 localAxisA = new Vec2();
   public boolean enableMotor;
   public float maxMotorTorque;
   public float motorSpeed;
   public float frequencyHz;
   public float dampingRatio;

   public WheelJointDef() {
      super(JointType.WHEEL);
      this.localAxisA.set(1.0F, 0.0F);
      this.enableMotor = false;
      this.maxMotorTorque = 0.0F;
      this.motorSpeed = 0.0F;
   }

   public void initialize(Body b1, Body b2, Vec2 anchor, Vec2 axis) {
      this.bodyA = b1;
      this.bodyB = b2;
      b1.getLocalPointToOut(anchor, this.localAnchorA);
      b2.getLocalPointToOut(anchor, this.localAnchorB);
      this.bodyA.getLocalVectorToOut(axis, this.localAxisA);
   }
}
