package net.diebuddies.jbox2d.pooling.stacks;

public class DynamicIntStack {
   private int[] stack;
   private int size;
   private int position;

   public DynamicIntStack(int initialSize) {
      this.stack = new int[initialSize];
      this.position = 0;
      this.size = initialSize;
   }

   public void reset() {
      this.position = 0;
   }

   public int pop() {
      assert this.position > 0;

      return this.stack[--this.position];
   }

   public void push(int i) {
      if (this.position == this.size) {
         int[] old = this.stack;
         this.stack = new int[this.size * 2];
         this.size = this.stack.length;
         System.arraycopy(old, 0, this.stack, 0, old.length);
      }

      this.stack[this.position++] = i;
   }

   public int getCount() {
      return this.position;
   }
}
