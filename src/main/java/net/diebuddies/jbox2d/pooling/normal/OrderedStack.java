package net.diebuddies.jbox2d.pooling.normal;

public abstract class OrderedStack<E> {
   public OrderedStack(int argStackSize, int argContainerSize) {
   }

   public final E pop() {
      return this.newInstance();
   }

   public final void push(int argNum) {
   }

   protected abstract E newInstance();
}
