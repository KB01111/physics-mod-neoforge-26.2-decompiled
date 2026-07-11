package net.diebuddies.physics.ocean.storage;

public interface StorageType {
   byte getData(int var1);

   void setData(StorageContainer var1, int var2, byte var3);

   boolean setAndCompareData(StorageContainer var1, int var2, byte var3);
}
