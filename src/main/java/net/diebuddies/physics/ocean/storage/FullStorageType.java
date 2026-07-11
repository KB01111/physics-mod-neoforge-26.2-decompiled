package net.diebuddies.physics.ocean.storage;

public class FullStorageType implements StorageType {
   public byte[] storage;

   public FullStorageType(byte[] storage) {
      this.storage = storage;
   }

   @Override
   public byte getData(int index) {
      return this.storage[index];
   }

   @Override
   public void setData(StorageContainer storage, int index, byte value) {
      this.storage[index] = value;
   }

   @Override
   public boolean setAndCompareData(StorageContainer storage, int index, byte value) {
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
