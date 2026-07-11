package net.diebuddies.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.diebuddies.physics.StarterClient;

public final class Flashback {
   private static float lastTick = 0.0F;
   private static double delta;
   private static final String FLASHBACK_CLASS = "com.moulberry.flashback.Flashback";
   private static final String EXPORT_JOB_CLASS = "com.moulberry.flashback.exporting.ExportJob";
   private static final String EXPORT_JOB_FIELD = "EXPORT_JOB";
   private static volatile boolean initialized = false;
   private static volatile boolean available = false;
   private static Method getReplayServerMethod;
   private static Field exportJobField;
   private static Method getCurrentTickDoubleMethod;
   private static Method getPartialReplayTickMethod;

   private Flashback() {
   }

   public static double getPlaybackSpeed(double deltaInSeconds) {
      if (!StarterClient.flashback) {
         return deltaInSeconds;
      } else {
         initReflection();
         if (!available) {
            return deltaInSeconds;
         } else {
            try {
               Object replayServer = getReplayServerMethod.invoke(null);
               if (replayServer != null) {
                  return delta;
               }
            } catch (Throwable var3) {
            }

            return deltaInSeconds;
         }
      }
   }

   public static void calculatePlaybackSpeed() {
      if (StarterClient.flashback) {
         initReflection();
         if (available) {
            try {
               Object replayServer = getReplayServerMethod.invoke(null);
               if (replayServer == null) {
                  return;
               }

               Object exportJob = exportJobField.get(null);
               float tick;
               if (exportJob != null) {
                  tick = getCurrentTickDoubleMethod.invoke(exportJob) instanceof Number n ? n.floatValue() : 0.0F;
               } else {
                  tick = getPartialReplayTickMethod.invoke(replayServer) instanceof Number n ? n.floatValue() : 0.0F;
               }

               delta = Math.max(0.0, (double)(tick - lastTick)) * 50.0 / 1000.0;
               lastTick = tick;
            } catch (Throwable var5) {
            }
         }
      }
   }

   private static void initReflection() {
      if (!initialized) {
         synchronized (Flashback.class) {
            if (!initialized) {
               try {
                  Class<?> flashbackClass = Class.forName("com.moulberry.flashback.Flashback");
                  getReplayServerMethod = flashbackClass.getMethod("getReplayServer");
                  getReplayServerMethod.setAccessible(true);
                  exportJobField = getAnyField(flashbackClass, "EXPORT_JOB");
                  exportJobField.setAccessible(true);
                  Class<?> exportJobClass = Class.forName("com.moulberry.flashback.exporting.ExportJob");
                  getCurrentTickDoubleMethod = exportJobClass.getMethod("getCurrentTickDouble");
                  getCurrentTickDoubleMethod.setAccessible(true);
                  Class<?> replayServerClass = getReplayServerMethod.getReturnType();
                  getPartialReplayTickMethod = replayServerClass.getMethod("getPartialReplayTick");
                  getPartialReplayTickMethod.setAccessible(true);
                  available = true;
               } catch (Throwable var9) {
                  available = false;
               } finally {
                  initialized = true;
               }
            }
         }
      }
   }

   private static Field getAnyField(Class<?> type, String name) throws NoSuchFieldException {
      try {
         return type.getField(name);
      } catch (NoSuchFieldException var4) {
         Field f = type.getDeclaredField(name);
         f.setAccessible(true);
         return f;
      }
   }
}
