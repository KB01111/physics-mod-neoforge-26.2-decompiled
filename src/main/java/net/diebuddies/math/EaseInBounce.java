package net.diebuddies.math;

public class EaseInBounce implements Curve {
   @Override
   public float get(float time) {
      return 1.0F - easeOutBounce(1.0F - time);
   }

   private static float easeOutBounce(float time) {
      float n1 = 7.5625F;
      float d1 = 2.75F;
      if (time < 1.0F / d1) {
         return n1 * time * time;
      } else if (time < 2.0F / d1) {
         float var5;
         return n1 * (var5 = time - 1.5F / d1) * var5 + 0.75F;
      } else {
         float var3;
         float var4;
         return (double)time < 2.5 / (double)d1 ? n1 * (var3 = time - 2.25F / d1) * var3 + 0.9375F : n1 * (var4 = time - 2.625F / d1) * var4 + 0.984375F;
      }
   }
}
