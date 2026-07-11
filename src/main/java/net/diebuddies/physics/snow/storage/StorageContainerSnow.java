package net.diebuddies.physics.snow.storage;

import net.diebuddies.physics.ocean.storage.FullStorageType;
import net.diebuddies.physics.ocean.storage.StorageContainer;

public interface StorageContainerSnow extends StorageContainer {
   byte getData(FullStorageType var1, FullStorageType var2, int var3, int var4, int var5);

   void setData(int var1, int var2, int var3, byte var4);

   boolean setAndCompareData(int var1, int var2, int var3, byte var4);

   void setAndInvalidate(int var1, int var2, int var3, byte var4);
}
