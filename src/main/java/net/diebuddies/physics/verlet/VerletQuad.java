package net.diebuddies.physics.verlet;

import org.joml.Vector2f;
import org.joml.Vector3d;

public class VerletQuad {
   public VerletQuad.VerletReference point1;
   public VerletQuad.VerletReference point2;
   public VerletQuad.VerletReference point3;
   public VerletQuad.VerletReference point4;
   public Vector3d normal;
   public Vector3d bufferNormal;

   public VerletQuad(VerletPoint point1, VerletPoint point2, VerletPoint point3, VerletPoint point4) {
      this.point1 = new VerletQuad.VerletReference(point1, point1.uv);
      this.point2 = new VerletQuad.VerletReference(point2, point2.uv);
      this.point3 = new VerletQuad.VerletReference(point3, point3.uv);
      this.point4 = new VerletQuad.VerletReference(point4, point4.uv);
      this.normal = new Vector3d();
      this.bufferNormal = new Vector3d();
   }

   public VerletQuad(
      VerletPoint point1, VerletPoint point2, VerletPoint point3, VerletPoint point4, Vector2f uv1, Vector2f uv2, Vector2f uv3, Vector2f uv4, boolean flipUV
   ) {
      this.point1 = new VerletQuad.VerletReference(point1, uv1);
      this.point2 = new VerletQuad.VerletReference(point2, uv2);
      this.point3 = new VerletQuad.VerletReference(point3, uv3);
      this.point4 = new VerletQuad.VerletReference(point4, uv4);
      this.normal = new Vector3d();
      this.bufferNormal = new Vector3d();
      if (flipUV) {
         uv1.y = 1.0F - uv1.y;
         uv2.y = 1.0F - uv2.y;
         uv3.y = 1.0F - uv3.y;
         uv4.y = 1.0F - uv4.y;
      }
   }

   public static class VerletReference {
      public Vector3d position;
      public Vector3d bufferPosition;
      public Vector3d bufferPrevPosition;
      public Vector3d renderPosition;
      public Vector3d normal;
      public Vector3d bufferNormal;
      public Vector2f uv;
      public int rgba;

      public VerletReference(VerletPoint position, Vector2f uv) {
         this.position = position.position;
         this.renderPosition = position.renderPosition;
         this.bufferPosition = position.bufferPosition;
         this.bufferPrevPosition = position.bufferPrevPosition;
         this.normal = position.normal;
         this.bufferNormal = position.bufferNormal;
         this.uv = uv;
         this.rgba = position.rgba;
      }
   }
}
