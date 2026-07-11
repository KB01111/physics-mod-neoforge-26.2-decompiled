package net.diebuddies.physics.ocean.storage;

public class FullStorageType2DShort {
   private short[] storage;

   public FullStorageType2DShort(int size) {
      this.storage = new short[size];
   }

   public short getData(int index) {
      return this.storage[index];
   }

   public void setData(int index, short value) {
      this.storage[index] = value;
   }

   public boolean setAndCompareData(int index, short value) {
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public short[] getArray() {
      return this.storage;
   }
}
