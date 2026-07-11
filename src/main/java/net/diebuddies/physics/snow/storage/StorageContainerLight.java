package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.ocean.storage.StorageContainer;

public interface StorageContainerLight extends StorageContainer {
   byte getData(int var1, int var2, int var3);

   void setData(int var1, int var2, int var3, byte var4);

   boolean setAndCompareData(int var1, int var2, int var3, byte var4);

   void setAndInvalidate(int var1, int var2, int var3, byte var4);
}
