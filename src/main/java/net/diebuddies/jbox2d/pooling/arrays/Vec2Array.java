package net.diebuddies.jbox2d.pooling.arrays;

import java.util.HashMap;
import net.diebuddies.jbox2d.common.Vec2;

public class Vec2Array {
   private final HashMap<Integer, Vec2[]> map = new HashMap<>();

   public Vec2[] get(int argLength) {
      assert argLength > 0;

      if (!this.map.containsKey(argLength)) {
         this.map.put(argLength, this.getInitializedArray(argLength));
      }

      assert ((Vec2[])this.map.get(argLength)).length == argLength : "Array not built of correct length";

      return this.map.get(argLength);
   }

   protected Vec2[] getInitializedArray(int argLength) {
      Vec2[] ray = new Vec2[argLength];

      for (int i = 0; i < ray.length; i++) {
         ray[i] = new Vec2();
      }

      return ray;
   }
}
