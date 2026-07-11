package net.diebuddies.util;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Pool<T> {
   private Object[] stack;
   private Supplier<T> create;
   private Consumer<T> reset;
   private int size;

   public Pool(int initialCapacity, Supplier<T> create, Consumer<T> reset) {
      this.stack = new Object[Math.max(1, initialCapacity)];
      this.create = create;
      this.reset = reset;
      this.size = 0;
   }

   public void prefill(int count) {
      this.ensureCapacity(this.size + count);

      for (int i = 0; i < count; i++) {
         this.stack[this.size++] = this.create.get();
      }
   }

   public T obtain() {
      if (this.size > 0) {
         T object = (T)this.stack[--this.size];
         this.stack[this.size] = null;
         return object;
      } else {
         return this.create.get();
      }
   }

   public void free(T object) {
      this.reset.accept(object);
      this.ensureCapacity(this.size + 1);
      this.stack[this.size++] = object;
   }

   public void freeAll(Iterable<T> drawCalls) {
      for (T object : drawCalls) {
         this.free(object);
      }
   }

   private void ensureCapacity(int wanted) {
      if (wanted > this.stack.length) {
         int newCap = this.stack.length;

         while (newCap < wanted) {
            newCap <<= 1;
         }

         this.stack = Arrays.copyOf(this.stack, newCap);
      }
   }
}
