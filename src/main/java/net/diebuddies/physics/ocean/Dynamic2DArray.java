package net.diebuddies.physics.ocean;

public class Dynamic2DArray {
   public byte[] arr;
   private int width;
   private int height;

   public Dynamic2DArray(int initialWidth, int initialHeight) {
      this.arr = new byte[initialWidth * initialHeight];
      this.width = initialWidth;
      this.height = initialHeight;
   }

   public void request(int width, int height) {
      this.width = width;
      this.height = height;
      int size = width * height;
      if (this.arr.length < size) {
         this.arr = new byte[size + size >> 1];
      }
   }

   public byte get(int x, int y) {
      return this.arr[y * this.width + x];
   }

   public byte getClamp(int x, int y) {
      return this.arr[Math.min(y, this.height - 1) * this.width + Math.min(x, this.width - 1)];
   }

   public void set(int x, int y, byte value) {
      this.arr[y * this.width + x] = value;
   }

   public void set(int index, byte value) {
      this.arr[index] = value;
   }

   public int index(int x, int y) {
      return y * this.width + x;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Dynamic2DArray copy() {
      Dynamic2DArray copy = new Dynamic2DArray(this.width, this.height);
      copy.arr = new byte[this.arr.length];
      return copy;
   }
}
