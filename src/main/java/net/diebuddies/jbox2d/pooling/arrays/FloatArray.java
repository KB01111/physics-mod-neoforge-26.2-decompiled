package net.diebuddies.jbox2d.pooling.arrays;

import java.util.HashMap;

public class FloatArray {
   private final HashMap<Integer, float[]> map = new HashMap<>();

   public float[] get(int argLength) {
      assert argLength > 0;

      if (!this.map.containsKey(argLength)) {
         this.map.put(argLength, this.getInitializedArray(argLength));
      }

      assert ((float[])this.map.get(argLength)).length == argLength : "Array not built of correct length";

      return this.map.get(argLength);
   }

   protected float[] getInitializedArray(int argLength) {
      return new float[argLength];
   }
}
