package net.diebuddies.physics.ragdoll;

import org.joml.Vector3d;

public class RagdollJoint {
   public int index1;
   public int index2;
   public Vector3d point1;
   public Vector3d point2;
   public boolean fixed;
   public boolean stopCollision;

   public RagdollJoint(int index1, int index2, Vector3d point1, Vector3d point2) {
      this.index1 = index1;
      this.index2 = index2;
      this.point1 = point1;
      this.point2 = point2;
   }
}
