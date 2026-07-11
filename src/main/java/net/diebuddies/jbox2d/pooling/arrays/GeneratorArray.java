package net.diebuddies.jbox2d.pooling.arrays;

import java.util.HashMap;
import net.diebuddies.jbox2d.particle.VoronoiDiagram;

public class GeneratorArray {
   private final HashMap<Integer, VoronoiDiagram.Generator[]> map = new HashMap<>();

   public VoronoiDiagram.Generator[] get(int length) {
      assert length > 0;

      if (!this.map.containsKey(length)) {
         this.map.put(length, this.getInitializedArray(length));
      }

      assert ((VoronoiDiagram.Generator[])this.map.get(length)).length == length : "Array not built of correct length";

      return this.map.get(length);
   }

   protected VoronoiDiagram.Generator[] getInitializedArray(int length) {
      VoronoiDiagram.Generator[] ray = new VoronoiDiagram.Generator[length];

      for (int i = 0; i < ray.length; i++) {
         ray[i] = new VoronoiDiagram.Generator();
      }

      return ray;
   }
}
