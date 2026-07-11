package net.diebuddies.physics.ocean.storage;

public class FullStorageType2DByte {
   private byte[] storage;

   public FullStorageType2DByte(int size) {
      this.storage = new byte[size];
   }

   public FullStorageType2DByte(byte[] data) {
      this.storage = data;
   }

   public byte getData(int index) {
      return this.storage[index];
   }

   public void setData(int index, byte value) {
      this.storage[index] = value;
   }

   public boolean setAndCompareData(int index, byte value) {
      if (this.storage[index] == value) {
         return false;
      } else {
         this.storage[index] = value;
         return true;
      }
   }

   public byte[] getArray() {
      return this.storage;
   }
}
