package net.diebuddies.bridge;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.function.Function;

public final class EventFactoryImpl {
   private static final List<ArrayBackedEvent<?>> ARRAY_BACKED_EVENTS = new ObjectArrayList();

   private EventFactoryImpl() {
   }

   public static void invalidate() {
      ARRAY_BACKED_EVENTS.forEach(ArrayBackedEvent::update);
   }

   public static <T> Event<T> createArrayBacked(Class<? super T> type, Function<T[], T> invokerFactory) {
      ArrayBackedEvent<T> event = new ArrayBackedEvent<>(type, invokerFactory);
      ARRAY_BACKED_EVENTS.add(event);
      return event;
   }

   private static <T> T buildEmptyInvoker(Class<T> handlerClass, Function<T[], T> invokerSetup) {
      Method funcIfMethod = null;

      for (Method m : handlerClass.getMethods()) {
         if ((m.getModifiers() & 2050) == 0) {
            if (funcIfMethod != null) {
               throw new IllegalStateException("Multiple virtual methods in " + handlerClass + "; cannot build empty invoker!");
            }

            funcIfMethod = m;
         }
      }

      if (funcIfMethod == null) {
         throw new IllegalStateException("No virtual methods in " + handlerClass + "; cannot build empty invoker!");
      } else {
         Object defValue = null;

         try {
            MethodHandle target = MethodHandles.lookup().unreflect(funcIfMethod);
            MethodType type = target.type().dropParameterTypes(0, 1);
            if (type.returnType() != void.class) {
               MethodType objTargetType = MethodType.genericMethodType(type.parameterCount())
                  .changeReturnType(type.returnType())
                  .insertParameterTypes(0, target.type().parameterType(0));
               MethodHandle objTarget = MethodHandles.explicitCastArguments(target, objTargetType);
               Object[] args = new Object[target.type().parameterCount()];
               Object[] emptyListeners = (Object[])Array.newInstance(handlerClass, 0);
               args[0] = invokerSetup.apply((T[])emptyListeners);
               defValue = objTarget.invokeWithArguments(args);
            }
         } catch (Throwable var9) {
            throw new RuntimeException(var9);
         }

         Object returnValue = defValue;
         return (T)Proxy.newProxyInstance(EventFactoryImpl.class.getClassLoader(), new Class[]{handlerClass}, (proxy, method, argsx) -> returnValue);
      }
   }
}
