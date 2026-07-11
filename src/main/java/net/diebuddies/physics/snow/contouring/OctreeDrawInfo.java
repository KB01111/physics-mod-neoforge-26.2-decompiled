package net.diebuddies.physics.snow.contouring;

import org.joml.Vector3f;

public class OctreeDrawInfo {
   public double x;
   public double y;
   public double z;
   public float normalX;
   public float normalY;
   public float normalZ;
   public byte corners;
   public int light;
   public int index;

   public float normalLengthSquared() {
      return Vector3f.lengthSquared(this.normalX, this.normalY, this.normalZ);
   }
}
