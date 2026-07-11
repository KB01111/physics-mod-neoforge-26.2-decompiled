package net.diebuddies.math;

import java.util.Random;

public class PerlinNoise {
   private final int[] permutation;
   private final int size;
   private final int sizeMinusOne;

   public PerlinNoise(Random seed, int tiles) {
      this.size = tiles;
      this.sizeMinusOne = tiles - 1;
      this.permutation = new int[tiles * 2];
      int i = 0;

      while (i < tiles) {
         this.permutation[i] = i++;
      }

      for (int ix = 0; ix < tiles; ix++) {
         int swap = seed.nextInt(tiles - ix) + ix;
         int element = this.permutation[ix];
         this.permutation[ix] = this.permutation[swap];
         this.permutation[swap] = element;
         this.permutation[ix + tiles] = this.permutation[ix];
      }
   }

   public PerlinNoise(Random seed) {
      this(seed, 256);
   }

   public double noise(double x, double y) {
      int xi = Math.fastFloor(x) & this.sizeMinusOne;
      int yi = Math.fastFloor(y) & this.sizeMinusOne;
      int g1 = this.permutation[this.permutation[xi] + yi];
      int g2 = this.permutation[this.permutation[xi + 1] + yi];
      int g3 = this.permutation[this.permutation[xi] + yi + 1];
      int g4 = this.permutation[this.permutation[xi + 1] + yi + 1];
      double xf = x - (double)Math.fastFloor(x);
      double yf = y - (double)Math.fastFloor(y);
      double u = fade(xf);
      double v = fade(yf);
      return lerp(v, lerp(u, grad(g1, xf, yf), grad(g2, xf - 1.0, yf)), lerp(u, grad(g3, xf, yf - 1.0), grad(g4, xf - 1.0, yf - 1.0)));
   }

   public double noise(double x, double y, double z) {
      int X = Math.fastFloor(x) & this.sizeMinusOne;
      int Y = Math.fastFloor(y) & this.sizeMinusOne;
      int Z = Math.fastFloor(z) & this.sizeMinusOne;
      x -= (double)Math.fastFloor(x);
      y -= (double)Math.fastFloor(y);
      z -= (double)Math.fastFloor(z);
      double u = fade(x);
      double v = fade(y);
      double w = fade(z);
      int A = this.permutation[X] + Y;
      int AA = this.permutation[A] + Z;
      int AB = this.permutation[A + 1] + Z;
      int B = this.permutation[X + 1] + Y;
      int BA = this.permutation[B] + Z;
      int BB = this.permutation[B + 1] + Z;
      return lerp(
         w,
         lerp(
            v,
            lerp(u, grad(this.permutation[AA], x, y, z), grad(this.permutation[BA], x - 1.0, y, z)),
            lerp(u, grad(this.permutation[AB], x, y - 1.0, z), grad(this.permutation[BB], x - 1.0, y - 1.0, z))
         ),
         lerp(
            v,
            lerp(u, grad(this.permutation[AA + 1], x, y, z - 1.0), grad(this.permutation[BA + 1], x - 1.0, y, z - 1.0)),
            lerp(u, grad(this.permutation[AB + 1], x, y - 1.0, z - 1.0), grad(this.permutation[BB + 1], x - 1.0, y - 1.0, z - 1.0))
         )
      );
   }

   private static final double lerp(double amount, double left, double right) {
      return org.joml.Math.lerp(left, right, amount);
   }

   private static final double fade(double t) {
      return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
   }

   private static final double grad(int hash, double x, double y) {
      switch (hash & 3) {
         case 0:
            return x + y;
         case 1:
            return -x + y;
         case 2:
            return x - y;
         case 3:
            return -x - y;
         default:
            return 0.0;
      }
   }

   private static final double grad(int hash, double x, double y, double z) {
      int h = hash & 15;
      double u = h < 8 ? x : y;
      double v = h < 4 ? y : (h != 12 && h != 14 ? z : x);
      return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
   }
}
