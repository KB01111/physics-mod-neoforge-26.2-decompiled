package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;

public class RopeJointDef extends JointDef {
   public final Vec2 localAnchorA = new Vec2();
   public final Vec2 localAnchorB = new Vec2();
   public float maxLength;

   public RopeJointDef() {
      super(JointType.ROPE);
      this.localAnchorA.set(-1.0F, 0.0F);
      this.localAnchorB.set(1.0F, 0.0F);
   }
}
