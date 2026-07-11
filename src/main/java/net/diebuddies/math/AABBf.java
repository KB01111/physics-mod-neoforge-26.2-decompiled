package net.diebuddies.math;

import org.joml.Vector3f;

public class AABBf {
   public Vector3f start;
   public Vector3f end;

   public AABBf(Vector3f start, Vector3f end) {
      this.start = start;
      this.end = end;
   }
}
