package net.diebuddies.physics.ocean.storage;

public class FullStorageType2DBit {
   private byte[] storage;

   public FullStorageType2DBit(int size) {
      this.storage = new byte[size >> 3];
   }

   public int getData(int index) {
      int position = index >> 3;
      int bit = index & 7;
      return this.storage[position] & 1 << bit;
   }

   public void setData(int index) {
      int position = index >> 3;
      int bit = index & 7;
      this.storage[position] = (byte)(this.storage[position] | 1 << bit);
   }

   public void unsetData(int index) {
      int position = index >> 3;
      int bit = index & 7;
      this.storage[position] = (byte)(this.storage[position] & ~(1 << bit));
   }

   public boolean setAndCompareData(int index) {
      int position = index >> 3;
      int bit = index & 7;
      int stored = this.storage[position] & 1 << bit;
      if (stored > 0) {
         return false;
      } else {
         this.storage[position] = (byte)(this.storage[position] | 1 << bit);
         return true;
      }
   }

   public boolean unsetAndCompareData(int index) {
      int position = index >> 3;
      int bit = index & 7;
      int stored = this.storage[position] & 1 << bit;
      if (stored == 0) {
         return false;
      } else {
         this.storage[position] = (byte)(this.storage[position] & ~(1 << bit));
         return true;
      }
   }

   public byte[] getArray() {
      return this.storage;
   }
}
