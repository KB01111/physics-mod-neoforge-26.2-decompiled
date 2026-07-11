package net.diebuddies.jbox2d.pooling.normal;

import net.diebuddies.jbox2d.pooling.IDynamicStack;

public abstract class MutableStack<E> implements IDynamicStack<E> {
   public MutableStack(int argInitSize) {
   }

   private void extendStack(int argSize) {
   }

   @Override
   public final E pop() {
      return this.newInstance();
   }

   @Override
   public final void push(E argObject) {
   }

   protected abstract E newInstance();

   protected abstract E[] newArray(int var1);
}
