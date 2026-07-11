package net.diebuddies.math;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

public class FastRandomSource implements RandomSource {
   public RandomSource fork() {
      return null;
   }

   public PositionalRandomFactory forkPositional() {
      return null;
   }

   public void setSeed(long var1) {
   }

   public int nextInt() {
      return 0;
   }

   public int nextInt(int size) {
      return Math.randomInt(size);
   }

   public long nextLong() {
      return 0L;
   }

   public boolean nextBoolean() {
      return false;
   }

   public float nextFloat() {
      return 0.0F;
   }

   public double nextDouble() {
      return 0.0;
   }

   public double nextGaussian() {
      return 0.0;
   }
}
