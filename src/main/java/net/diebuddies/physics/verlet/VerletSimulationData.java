package net.diebuddies.physics.verlet;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class VerletSimulationData {
   public List<VerletPoint> points = new ObjectArrayList();
   public List<VerletStick> sticks = new ObjectArrayList();
   public List<VerletQuad> quads = new ObjectArrayList();
   public List<VerletTriangle> triangles = new ObjectArrayList();
   public List<VerletLine> lines = new ObjectArrayList();
   public Vector3d offset;
   public Vector3d bufferOffset;
   public Matrix4d transformation = new Matrix4d();
   public Matrix4d bufferTransformation = new Matrix4d();

   public VerletSimulationData(Vector3d offset) {
      if (offset == null) {
         this.offset = null;
         this.bufferOffset = null;
      } else {
         this.offset = new Vector3d(offset);
         this.bufferOffset = new Vector3d(offset);
      }
   }
}
