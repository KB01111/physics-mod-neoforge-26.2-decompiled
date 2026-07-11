package net.diebuddies.physics;

import physx.extensions.PxJoint;

public class GrabHand {
   private boolean previousTriggered;
   private PxJoint joint;

   public boolean isPreviousTriggered() {
      return this.previousTriggered;
   }

   public void setPreviousTriggered(boolean triggered) {
      this.previousTriggered = triggered;
   }

   public PxJoint getJoint() {
      return this.joint;
   }

   public void setJoint(PxJoint joint) {
      this.joint = joint;
   }
}
