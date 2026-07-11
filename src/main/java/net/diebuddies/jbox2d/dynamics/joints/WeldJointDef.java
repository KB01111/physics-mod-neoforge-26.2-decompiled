package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class WeldJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2();
   public final Vec2 localAnchorB = new Vec2();
   public float referenceAngle = 0.0F;
   public float frequencyHz;
   public float dampingRatio;

   public WeldJointDef() {
      super(JointType.WELD);
   }

   public void initialize(Body bA, Body bB, Vec2 anchor) {
      this.bodyA = bA;
      this.bodyB = bB;
      this.bodyA.getLocalPointToOut(anchor, this.localAnchorA);
      this.bodyB.getLocalPointToOut(anchor, this.localAnchorB);
      this.referenceAngle = this.bodyB.getAngle() - this.bodyA.getAngle();
   }
}
