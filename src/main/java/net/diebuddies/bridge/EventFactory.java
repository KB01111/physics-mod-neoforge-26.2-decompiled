package net.diebuddies.bridge;

import java.util.function.Function;

public final class EventFactory {
   private static boolean profilingEnabled = true;

   private EventFactory() {
   }

   public static boolean isProfilingEnabled() {
      return profilingEnabled;
   }

   public static void invalidate() {
      EventFactoryImpl.invalidate();
   }

   public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
      return EventFactoryImpl.createArrayBacked(type, invokerFactory);
   }

   public static <T> Event<T> createArrayBacked(Class<T> type, T emptyInvoker, Function<T[], T> invokerFactory) {
      return createArrayBacked(type, listeners -> {
         if (listeners.length == 0) {
            return emptyInvoker;
         } else {
            return listeners.length == 1 ? listeners[0] : invokerFactory.apply(listeners);
         }
      });
   }

   public static String getHandlerName(Object handler) {
      return handler.getClass().getName();
   }
}
