package net.diebuddies.math;

public class EaseOutElastic implements Curve {
   @Override
   public float get(float time) {
      float c4 = (float) (java.lang.Math.PI * 2.0 / 3.0);
      return (float)java.lang.Math.pow(2.0, (double)(-10.0F * time)) * (float)java.lang.Math.sin((double)((time * 10.0F - 0.75F) * c4)) + 1.0F;
   }
}
