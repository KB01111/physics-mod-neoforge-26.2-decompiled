package net.diebuddies.compat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.diebuddies.physics.StarterClient;

public final class Replay {
   private static final String REPLAYMOD_REPLAY_CLASS = "com.replaymod.replay.ReplayModReplay";
   private static volatile boolean initialized = false;
   private static volatile boolean available = false;
   private static Field instanceField;
   private static Method getReplayHandlerMethod;
   private static Method getReplaySenderMethod;
   private static Method getReplaySpeedMethod;

   private Replay() {
   }

   public static double getPlaybackSpeed() {
      if (!StarterClient.replay) {
         return 1.0;
      } else {
         initReflection();
         if (!available) {
            return 1.0;
         } else {
            try {
               Object replayModReplay = instanceField.get(null);
               if (replayModReplay == null) {
                  return 1.0;
               }

               Object handler = getReplayHandlerMethod.invoke(replayModReplay);
               if (handler == null) {
                  return 1.0;
               }

               Object sender = getReplaySenderMethod.invoke(handler);
               if (sender == null) {
                  return 1.0;
               }

               if (getReplaySpeedMethod.invoke(sender) instanceof Number n) {
                  return n.doubleValue();
               }
            } catch (Throwable var5) {
            }

            return 1.0;
         }
      }
   }

   private static void initReflection() {
      if (!initialized) {
         synchronized (Replay.class) {
            if (!initialized) {
               try {
                  Class<?> replayModReplayClass = Class.forName("com.replaymod.replay.ReplayModReplay");
                  instanceField = getAnyField(replayModReplayClass, "instance");
                  instanceField.setAccessible(true);
                  getReplayHandlerMethod = replayModReplayClass.getMethod("getReplayHandler");
                  Class<?> handlerClass = getReplayHandlerMethod.getReturnType();
                  getReplaySenderMethod = handlerClass.getMethod("getReplaySender");
                  Class<?> senderClass = getReplaySenderMethod.getReturnType();
                  getReplaySpeedMethod = senderClass.getMethod("getReplaySpeed");
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
