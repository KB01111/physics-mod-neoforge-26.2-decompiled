package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;

public class MouseJointDef extends JointDef {
   public final Vec2 target = new Vec2();
   public float maxForce;
   public float frequencyHz;
   public float dampingRatio;

   public MouseJointDef() {
      super(JointType.MOUSE);
      this.target.set(0.0F, 0.0F);
      this.maxForce = 0.0F;
      this.frequencyHz = 5.0F;
      this.dampingRatio = 0.7F;
   }
}
