package net.diebuddies.physics.ocean.storage;

public class StorageSimple implements StorageContainer {
   public volatile StorageType data;
   public final int size;

   public StorageSimple(byte data, int size) {
      this.size = size;
      this.data = new EqualStorageType(data);
   }

   public StorageSimple(byte[] data, int size) {
      this.size = size;
      this.data = AdaptiveStorageType.fromFull(data);
   }

   @Override
   public byte getData(int index) {
      return this.data.getData(index);
   }

   @Override
   public void setData(int index, byte value) {
      this.data.setData(this, index, value);
   }

   @Override
   public boolean setAndCompareData(int index, byte value) {
      return this.data.setAndCompareData(this, index, value);
   }

   @Override
   public StorageType getStorageType() {
      return this.data;
   }

   @Override
   public int getSize() {
      return this.size;
   }

   @Override
   public void setData(StorageType data) {
      this.data = data;
   }
}
