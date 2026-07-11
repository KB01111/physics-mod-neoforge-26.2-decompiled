package net.diebuddies.physics.snow.contouring;

public class Vertex {
   public double x;
   public double y;
   public double z;
   public float normalX;
   public float normalY;
   public float normalZ;
   public int light;

   public Vertex(double x, double y, double z, float normalX, float normalY, float normalZ, int light) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.normalX = normalX;
      this.normalY = normalY;
      this.normalZ = normalZ;
      this.light = light;
   }

   public Vertex() {
   }

   public Vertex set(double x, double y, double z, float normalX, float normalY, float normalZ, int light) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.normalX = normalX;
      this.normalY = normalY;
      this.normalZ = normalZ;
      this.light = light;
      return this;
   }
}
