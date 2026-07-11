package net.diebuddies.jbox2d.particle;

public class StackQueue<T> {
   private T[] m_buffer;
   private int m_front;
   private int m_back;
   private int m_end;

   public void reset(T[] buffer) {
      this.m_buffer = buffer;
      this.m_front = 0;
      this.m_back = 0;
      this.m_end = buffer.length;
   }

   public void push(T task) {
      if (this.m_back >= this.m_end) {
         System.arraycopy(this.m_buffer, this.m_front, this.m_buffer, 0, this.m_back - this.m_front);
         this.m_back = this.m_back - this.m_front;
         this.m_front = 0;
         if (this.m_back >= this.m_end) {
            return;
         }
      }

      this.m_buffer[this.m_back++] = task;
   }

   public T pop() {
      assert this.m_front < this.m_back;

      return this.m_buffer[this.m_front++];
   }

   public boolean empty() {
      return this.m_front >= this.m_back;
   }

   public T front() {
      return this.m_buffer[this.m_front];
   }
}
