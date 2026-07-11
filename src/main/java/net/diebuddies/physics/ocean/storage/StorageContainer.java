package net.diebuddies.physics.ocean.storage;

public interface StorageContainer {
   void setData(StorageType var1);

   byte getData(int var1);

   void setData(int var1, byte var2);

   boolean setAndCompareData(int var1, byte var2);

   StorageType getStorageType();

   int getSize();
}
