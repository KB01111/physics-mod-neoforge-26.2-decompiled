package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;

public class DistanceJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2(0.0F, 0.0F);
   public final Vec2 localAnchorB = new Vec2(0.0F, 0.0F);
   public float length = 1.0F;
   public float frequencyHz = 0.0F;
   public float dampingRatio = 0.0F;

   public DistanceJointDef() {
      super(JointType.DISTANCE);
   }

   public void initialize(Body b1, Body b2, Vec2 anchor1, Vec2 anchor2) {
      this.bodyA = b1;
      this.bodyB = b2;
      this.localAnchorA.set(this.bodyA.getLocalPoint(anchor1));
      this.localAnchorB.set(this.bodyB.getLocalPoint(anchor2));
      Vec2 d = anchor2.sub(anchor1);
      this.length = d.length();
   }
}
