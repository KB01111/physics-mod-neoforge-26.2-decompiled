package net.diebuddies.physics.ocean.test;

import org.joml.Vector2d;

public class OceanPatternTest {
   public static double DRAG_MULT = 0.048;

   public static void main(String[] args) {
      double[][] sample = new double[5][5];
      int sampleSize = 6;

      for (int x = 0; x < sample.length; x++) {
         for (int y = 0; y < sample[x].length; y++) {
            sample[x][y] = getwaves(new Vector2d((double)x, (double)y), sampleSize, 0.0);
         }
      }

      for (int x = 5; x < 1000000; x++) {
         double[][] testSample = new double[5][5];

         for (int xo = 0; xo < sample.length; xo++) {
            for (int y = 0; y < sample[0].length; y++) {
               testSample[xo][y] = getwaves(new Vector2d((double)x, (double)y), sampleSize, 0.0);
            }
         }

         boolean same = true;

         for (int i = 0; i < sample.length && same; i++) {
            for (int j = 0; j < sample[0].length && same; j++) {
               if (Math.abs(sample[i][j] - testSample[i][j]) > 0.1) {
                  same = false;
               }
            }
         }

         if (same) {
            System.out.println("found repeating pattern at x " + x);
         } else if (x % 100000 == 0) {
            System.out.println("processed " + x);
         }
      }
   }

   public static Vector2d wavedx(Vector2d position, Vector2d direction, double speed, double frequency, double timeshift) {
      double x = direction.dot(position) * frequency + timeshift * speed;
      double wave = Math.exp(Math.sin(x) - 1.0);
      double dx = wave * Math.cos(x);
      return new Vector2d(wave, -dx);
   }

   public static double getwaves(Vector2d position, int iterations, double time) {
      double iter = 0.0;
      double phase = 6.0;
      double speed = 2.0;
      double weight = 1.0;
      double w = 0.0;
      double ws = 0.0;

      for (int i = 0; i < iterations; i++) {
         Vector2d p = new Vector2d(Math.sin(iter), Math.cos(iter));
         Vector2d res = wavedx(position, p, speed, phase, time);
         position.add(p.mul(res.y * weight * DRAG_MULT));
         w += res.x * weight;
         iter += 96.0;
         ws += weight;
         weight = org.joml.Math.lerp(0.0, 0.2, weight);
         phase *= 1.18;
         speed *= 1.07;
      }

      return w / ws;
   }
}
