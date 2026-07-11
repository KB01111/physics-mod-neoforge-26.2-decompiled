package net.diebuddies.jbox2d.dynamics.joints;

public class GearJointDef extends JointDef {
   public Joint joint1 = null;
   public Joint joint2 = null;
   public float ratio;

   public GearJointDef() {
      super(JointType.GEAR);
   }
}
