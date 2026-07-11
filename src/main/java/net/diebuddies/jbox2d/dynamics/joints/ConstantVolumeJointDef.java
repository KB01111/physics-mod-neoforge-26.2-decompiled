package net.diebuddies.jbox2d.dynamics.joints;

import java.util.ArrayList;
import net.diebuddies.jbox2d.dynamics.Body;

public class ConstantVolumeJointDef extends JointDef {
   public float frequencyHz;
   public float dampingRatio;
   ArrayList<Body> bodies = new ArrayList<>();
   ArrayList<DistanceJoint> joints = null;

   public ConstantVolumeJointDef() {
      super(JointType.CONSTANT_VOLUME);
      this.collideConnected = false;
      this.frequencyHz = 0.0F;
      this.dampingRatio = 0.0F;
   }

   public void addBody(Body argBody) {
      this.bodies.add(argBody);
      if (this.bodies.size() == 1) {
         this.bodyA = argBody;
      }

      if (this.bodies.size() == 2) {
         this.bodyB = argBody;
      }
   }

   public void addBodyAndJoint(Body argBody, DistanceJoint argJoint) {
      this.addBody(argBody);
      if (this.joints == null) {
         this.joints = new ArrayList<>();
      }

      this.joints.add(argJoint);
   }
}
