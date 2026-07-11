package net.diebuddies.util;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;

public class PerformanceTracker {
   private static ThreadLocal<PerformanceTracker> threadLocal = ThreadLocal.withInitial(() -> new PerformanceTracker());
   private Object2LongMap<String> tracked = new Object2LongOpenHashMap();
   private Object2LongMap<String> results = new Object2LongOpenHashMap();
   private Object2LongMap<String> pause = new Object2LongOpenHashMap();

   private static PerformanceTracker getInstance() {
      return threadLocal.get();
   }

   public static void start(String identifier) {
      PerformanceTracker tracker = getInstance();
      tracker.results.removeLong(identifier);
      tracker.tracked.put(identifier, System.nanoTime());
   }

   public static void startNoFlush(String identifier) {
      getInstance().tracked.put(identifier, System.nanoTime());
   }

   public static void flush(String identifier) {
      getInstance().results.removeLong(identifier);
   }

   public static void pause(String identifier) {
      PerformanceTracker tracker = getInstance();
      tracker.pause.put(identifier, System.nanoTime());
   }

   public static void unpause(String identifier) {
      PerformanceTracker tracker = getInstance();
      long diff = System.nanoTime() - tracker.pause.removeLong(identifier);
      tracker.tracked.put(identifier, tracker.tracked.getOrDefault(identifier, 0L) + diff);
   }

   public static void end(String identifier) {
      PerformanceTracker tracker = getInstance();
      long nanoTime = tracker.tracked.getLong(identifier);
      long diff = tracker.results.getOrDefault(identifier, 0L);
      tracker.results.put(identifier, diff + (System.nanoTime() - nanoTime));
   }

   public static double getInMillis(String identifier) {
      return (double)getInstance().results.getOrDefault(identifier, 0L) / 1000000.0;
   }

   public static long getInNanos(String identifier) {
      return getInstance().results.getOrDefault(identifier, 0L);
   }

   public static String getInMillisFormatted(String identifier) {
      return String.format("%.2f", (double)getInstance().results.getOrDefault(identifier, 0L) / 1000000.0);
   }

   public static void flushAll() {
      getInstance().results.clear();
   }
}
