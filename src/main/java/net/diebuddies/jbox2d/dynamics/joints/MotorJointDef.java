package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class MotorJointDef extends JointDef {
   public final Vec2 linearOffset = new Vec2();
   public float angularOffset = 0.0F;
   public float maxForce = 1.0F;
   public float maxTorque = 1.0F;
   public float correctionFactor = 0.3F;

   public MotorJointDef() {
      super(JointType.MOTOR);
   }

   public void initialize(Body bA, Body bB) {
      this.bodyA = bA;
      this.bodyB = bB;
      Vec2 xB = this.bodyB.getPosition();
      this.bodyA.getLocalPointToOut(xB, this.linearOffset);
      float angleA = this.bodyA.getAngle();
      float angleB = this.bodyB.getAngle();
      this.angularOffset = angleB - angleA;
   }
}
