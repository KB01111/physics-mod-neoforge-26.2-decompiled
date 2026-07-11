package net.diebuddies.physics.snow.math;

import org.joml.Vector3d;

public class RayHit {
   public Vector3d normal;
   public Vector3d point;

   public RayHit(Vector3d normal, Vector3d point) {
      this.normal = normal;
      this.point = point;
   }
}
