package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class PrismaticJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2();
   public final Vec2 localAnchorB = new Vec2();
   public final Vec2 localAxisA = new Vec2(1.0F, 0.0F);
   public float referenceAngle = 0.0F;
   public boolean enableLimit = false;
   public float lowerTranslation = 0.0F;
   public float upperTranslation = 0.0F;
   public boolean enableMotor = false;
   public float maxMotorForce = 0.0F;
   public float motorSpeed = 0.0F;

   public PrismaticJointDef() {
      super(JointType.PRISMATIC);
   }

   public void initialize(Body b1, Body b2, Vec2 anchor, Vec2 axis) {
      this.bodyA = b1;
      this.bodyB = b2;
      this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
      this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
      this.bodyA.getLocalVectorToOut(axis, this.localAxisA);
      this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
   }
}
