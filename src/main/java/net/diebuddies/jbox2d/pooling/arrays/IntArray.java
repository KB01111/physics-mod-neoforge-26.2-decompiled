package net.diebuddies.jbox2d.pooling.arrays;

import java.util.HashMap;

public class IntArray {
   private final HashMap<Integer, int[]> map = new HashMap<>();

   public int[] get(int argLength) {
      assert argLength > 0;

      if (!this.map.containsKey(argLength)) {
         this.map.put(argLength, this.getInitializedArray(argLength));
      }

      assert ((int[])this.map.get(argLength)).length == argLength : "Array not built of correct length";

      return this.map.get(argLength);
   }

   protected int[] getInitializedArray(int argLength) {
      return new int[argLength];
   }
}
