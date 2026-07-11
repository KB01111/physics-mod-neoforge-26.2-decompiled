package net.diebuddies.physics.ocean.storage;

public class EqualStorageType implements StorageType {
   public volatile byte value;

   public EqualStorageType(byte value) {
      this.value = value;
   }

   @Override
   public byte getData(int index) {
      return this.value;
   }

   @Override
   public void setData(StorageContainer storage, int index, byte value) {
      if (this.value != value) {
         byte[] data = new byte[storage.getSize()];

         for (int i = 0; i < data.length; i++) {
            data[i] = this.value;
         }

         data[index] = value;
         storage.setData(AdaptiveStorageType.fromFull(data));
      }
   }

   @Override
   public boolean setAndCompareData(StorageContainer storage, int index, byte value) {
      if (this.value != value) {
         this.setData(storage, index, value);
         return true;
      } else {
         return false;
      }
   }
}
