package net.diebuddies.jbox2d.common;

public class Timer {
   private long resetNanos;

   public Timer() {
      this.reset();
   }

   public void reset() {
      this.resetNanos = System.nanoTime();
   }

   public float getMilliseconds() {
      return (float)((System.nanoTime() - this.resetNanos) / 1000L) * 1.0F / 1000.0F;
   }
}
