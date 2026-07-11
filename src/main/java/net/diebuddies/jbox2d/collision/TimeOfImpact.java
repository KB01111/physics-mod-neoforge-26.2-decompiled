package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Sweep;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class TimeOfImpact {
   public static final int MAX_ITERATIONS = 20;
   public static final int MAX_ROOT_ITERATIONS = 50;
   public static int toiCalls = 0;
   public static int toiIters = 0;
   public static int toiMaxIters = 0;
   public static int toiRootIters = 0;
   public static int toiMaxRootIters = 0;
   private final Distance.SimplexCache cache = new Distance.SimplexCache();
   private final DistanceInput distanceInput = new DistanceInput();
   private final Transform xfA = new Transform();
   private final Transform xfB = new Transform();
   private final DistanceOutput distanceOutput = new DistanceOutput();
   private final SeparationFunction fcn = new SeparationFunction();
   private final int[] indexes = new int[2];
   private final Sweep sweepA = new Sweep();
   private final Sweep sweepB = new Sweep();
   private final IWorldPool pool;

   public TimeOfImpact(IWorldPool argPool) {
      this.pool = argPool;
   }

   public final void timeOfImpact(TimeOfImpact.TOIOutput output, TimeOfImpact.TOIInput input) {
      toiCalls++;
      output.state = TimeOfImpact.TOIOutputState.UNKNOWN;
      output.t = input.tMax;
      Distance.DistanceProxy proxyA = input.proxyA;
      Distance.DistanceProxy proxyB = input.proxyB;
      this.sweepA.set(input.sweepA);
      this.sweepB.set(input.sweepB);
      this.sweepA.normalize();
      this.sweepB.normalize();
      float tMax = input.tMax;
      float totalRadius = proxyA.m_radius + proxyB.m_radius;
      float target = MathUtils.max(Settings.linearSlop, totalRadius - 3.0F * Settings.linearSlop);
      float tolerance = 0.25F * Settings.linearSlop;

      assert target > tolerance;

      float t1 = 0.0F;
      int iter = 0;
      this.cache.count = 0;
      this.distanceInput.proxyA = input.proxyA;
      this.distanceInput.proxyB = input.proxyB;
      this.distanceInput.useRadii = false;

      while (true) {
         this.sweepA.getTransform(this.xfA, t1);
         this.sweepB.getTransform(this.xfB, t1);
         this.distanceInput.transformA = this.xfA;
         this.distanceInput.transformB = this.xfB;
         this.pool.getDistance().distance(this.distanceOutput, this.cache, this.distanceInput);
         if (this.distanceOutput.distance <= 0.0F) {
            output.state = TimeOfImpact.TOIOutputState.OVERLAPPED;
            output.t = 0.0F;
            break;
         }

         if (this.distanceOutput.distance < target + tolerance) {
            output.state = TimeOfImpact.TOIOutputState.TOUCHING;
            output.t = t1;
            break;
         }

         this.fcn.initialize(this.cache, proxyA, this.sweepA, proxyB, this.sweepB, t1);
         boolean done = false;
         float t2 = tMax;
         int pushBackIter = 0;

         int rootIterCount;
         do {
            float s2 = this.fcn.findMinSeparation(this.indexes, t2);
            if (s2 > target + tolerance) {
               output.state = TimeOfImpact.TOIOutputState.SEPARATED;
               output.t = tMax;
               done = true;
               break;
            }

            if (s2 > target - tolerance) {
               t1 = t2;
               break;
            }

            float s1 = this.fcn.evaluate(this.indexes[0], this.indexes[1], t1);
            if (s1 < target - tolerance) {
               output.state = TimeOfImpact.TOIOutputState.FAILED;
               output.t = t1;
               done = true;
               break;
            }

            if (s1 <= target + tolerance) {
               output.state = TimeOfImpact.TOIOutputState.TOUCHING;
               output.t = t1;
               done = true;
               break;
            }

            rootIterCount = 0;
            float a1 = t1;
            float a2 = t2;

            do {
               float t;
               if ((rootIterCount & 1) == 1) {
                  t = a1 + (target - s1) * (a2 - a1) / (s2 - s1);
               } else {
                  t = 0.5F * (a1 + a2);
               }

               rootIterCount++;
               toiRootIters++;
               float s = this.fcn.evaluate(this.indexes[0], this.indexes[1], t);
               if (MathUtils.abs(s - target) < tolerance) {
                  t2 = t;
                  break;
               }

               if (s > target) {
                  a1 = t;
                  s1 = s;
               } else {
                  a2 = t;
                  s2 = s;
               }
            } while (rootIterCount != 50);

            toiMaxRootIters = MathUtils.max(toiMaxRootIters, rootIterCount);
         } while (++pushBackIter != Settings.maxPolygonVertices && rootIterCount != 50);

         iter++;
         toiIters++;
         if (done) {
            break;
         }

         if (iter == 20) {
            output.state = TimeOfImpact.TOIOutputState.FAILED;
            output.t = t1;
            break;
         }
      }

      toiMaxIters = MathUtils.max(toiMaxIters, iter);
   }

   public static class TOIInput {
      public final Distance.DistanceProxy proxyA = new Distance.DistanceProxy();
      public final Distance.DistanceProxy proxyB = new Distance.DistanceProxy();
      public final Sweep sweepA = new Sweep();
      public final Sweep sweepB = new Sweep();
      public float tMax;
   }

   public static class TOIOutput {
      public TimeOfImpact.TOIOutputState state;
      public float t;
   }

   public static enum TOIOutputState {
      UNKNOWN,
      FAILED,
      OVERLAPPED,
      TOUCHING,
      SEPARATED;
   }
}
