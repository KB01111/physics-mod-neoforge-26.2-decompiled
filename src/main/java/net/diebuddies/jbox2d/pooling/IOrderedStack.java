package net.diebuddies.jbox2d.pooling;

public interface IOrderedStack<E> {
   E pop();

   E[] pop(int var1);

   void push(int var1);
}
