package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class FrictionJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2();
   public final Vec2 localAnchorB = new Vec2();
   public float maxForce = 0.0F;
   public float maxTorque = 0.0F;

   public FrictionJointDef() {
      super(JointType.FRICTION);
   }

   public void initialize(Body bA, Body bB, Vec2 anchor) {
      this.bodyA = bA;
      this.bodyB = bB;
      bA.getLocalPointToOut(anchor, this.localAnchorA);
      bB.getLocalPointToOut(anchor, this.localAnchorB);
   }
}
