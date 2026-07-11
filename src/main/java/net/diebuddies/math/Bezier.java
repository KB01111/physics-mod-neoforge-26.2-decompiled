package net.diebuddies.math;

public class Bezier implements Curve {
   public static final Bezier EASE_IN_EXPO = new Bezier(0.0F, 1.0F, 0.9F, 1.0F);
   public static final Bezier EASE_OUT_EXPO = new Bezier(0.0F, 1.0F, 0.06F, 0.1F);
   public static final Bezier BOUNCE = new Bezier(0.0F, 1.0F, 0.0F, 2.0F);
   public static final Bezier EASE_IN_OUT_EXPO = new Bezier(0.0F, 1.0F, 0.0F, 1.0F);
   public float p0;
   public float p1;
   public float c0;
   public float c1;

   public Bezier(float p0, float p1, float c0, float c1) {
      this.p0 = p0;
      this.p1 = p1;
      this.c0 = c0;
      this.c1 = c1;
   }

   @Override
   public float get(float time) {
      return this.p0 * (float)java.lang.Math.pow((double)(1.0F - time), 3.0)
         + this.c0 * (float)java.lang.Math.pow((double)(1.0F - time), 2.0) * 3.0F * time
         + this.c1 * (float)java.lang.Math.pow((double)time, 2.0) * 3.0F * (1.0F - time)
         + this.p1 * (float)java.lang.Math.pow((double)time, 3.0);
   }

   public static Bezier[] getCubicBSpline(float[] dataPoint) {
      Bezier[] beziers = new Bezier[dataPoint.length - 1];
      float[] controlPoints = new float[(dataPoint.length - 1) * 2];
      float[] points = new float[dataPoint.length];

      for (int i = 0; i < dataPoint.length; i++) {
         int i1 = Math.clamp(i - 1, 0, dataPoint.length - 1);
         int i2 = Math.clamp(i, 0, dataPoint.length - 1);
         int i3 = Math.clamp(i + 1, 0, dataPoint.length - 1);
         float[] cp = getControlPoints(dataPoint[i1], dataPoint[i2], dataPoint[i3], 1.5F);
         int cpi1 = Math.clamp(i * 2 - 1, 0, controlPoints.length - 1);
         int cpi2 = Math.clamp(i * 2, 0, controlPoints.length - 1);
         controlPoints[cpi1] = cp[0];
         controlPoints[cpi2] = cp[1];
      }

      points[0] = dataPoint[0];
      points[points.length - 1] = dataPoint[dataPoint.length - 1];

      for (int i = 1; i < points.length - 1; i++) {
         points[i] = (controlPoints[i * 2 - 1] + controlPoints[i * 2]) * 0.5F;
      }

      for (int i = 0; i < beziers.length; i++) {
         beziers[i] = new Bezier(dataPoint[i], dataPoint[i + 1], controlPoints[i * 2], controlPoints[i * 2 + 1]);
      }

      return beziers;
   }

   private static float[] getControlPoints(float x0, float x1, float x2, float t) {
      float d01 = x1 - x0;
      float d12 = x2 - x1;
      float fa = t * d01 / (d01 + d12);
      float fb = t * d12 / (d01 + d12);
      float p1x = x1 - fa * (x2 - x0);
      float p2x = x1 + fb * (x2 - x0);
      return new float[]{p1x, p2x};
   }
}
