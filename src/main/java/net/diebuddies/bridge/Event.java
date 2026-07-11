package net.diebuddies.bridge;

public abstract class Event<T> {
   protected volatile T invoker;

   public final T invoker() {
      return this.invoker;
   }

   public abstract void register(T var1);
}
